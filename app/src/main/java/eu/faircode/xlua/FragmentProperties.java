package eu.faircode.xlua;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import eu.faircode.xlua.api.properties.MockPropConversions;
import eu.faircode.xlua.api.properties.MockPropGroupHolder;
import eu.faircode.xlua.api.properties.MockPropSetting;
import eu.faircode.xlua.api.settingsex.LuaSettingEx;
import eu.faircode.xlua.api.xlua.XLuaCall;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.api.xmock.XMockQuery;
import eu.faircode.xlua.api.props.XMockPropGroup;

public class FragmentProperties extends Fragment {
    private final static String TAG = "XLua.FragmentProperties";

    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private AdapterPropertiesGroup rvPropsAdapter;

    private RecyclerView rvPropGroups;
    private AppGeneric application;


    private TextView tvPackageName;
    private TextView tvPackage;
    private TextView tvPackageUid;

    private ImageView ivAppIcon;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View main = inflater.inflate(R.layout.proprecyclerview, container, false);
        initRefresh(main);
        initRecyclerView(main);

        application = AppGeneric.from(getArguments(), getContext());
        Log.i(TAG, "Application Object Created=" + application);

        tvPackageName = main.findViewById(R.id.tvPropertiesPackageName);
        tvPackage = main.findViewById(R.id.tvPropertiesPackageFull);
        tvPackageUid = main.findViewById(R.id.tvPropertiesPackageUid);
        ivAppIcon = main.findViewById(R.id.ivPropertiesAppIcon);


        tvPackageName.setText(application.getName());
        tvPackage.setText(application.getPackageName());
        tvPackageUid.setText(String.valueOf(application.getUid()));
        application.initIcon(ivAppIcon, Objects.requireNonNull(getContext()));

        return main;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    public void filter(String query) {
        if (rvPropsAdapter != null)
            rvPropsAdapter.getFilter().filter(query);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initRefresh(final View view) {
        progressBar = view.findViewById(R.id.pbProperties);
        int colorAccent = XUtil.resolveColor(Objects.requireNonNull(getContext()), R.attr.colorAccent);
        swipeRefresh = view.findViewById(R.id.swipeRefreshProperties);
        swipeRefresh.setColorSchemeColors(colorAccent, colorAccent, colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { loadData(); }
        });
    }

    private void initRecyclerView(final View view) {
        rvPropGroups = view.findViewById(R.id.rvProperties);
        rvPropGroups.setVisibility(View.VISIBLE);
        rvPropGroups.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
                return true;
            }
        };

        llm.setAutoMeasureEnabled(true);
        rvPropGroups.setLayoutManager(llm);
        rvPropsAdapter = new AdapterPropertiesGroup();
        rvPropGroups.setAdapter(rvPropsAdapter);
    }

    private void loadData() {
        Log.i(TAG, "Starting data loader");
        LoaderManager manager = getActivity().getSupportLoaderManager();
        manager.restartLoader(ActivityMain.LOADER_DATA, new Bundle(), dataLoaderCallbacks).forceLoad();
    }

    LoaderManager.LoaderCallbacks dataLoaderCallbacks = new LoaderManager.LoaderCallbacks<PropsDataHolder>() {
        @Override
        public Loader<PropsDataHolder> onCreateLoader(int id, Bundle args) {
            return new PropsDataLoader(getContext()).setApp(application);
        }

        @Override
        public void onLoadFinished(Loader<PropsDataHolder> loader, PropsDataHolder data) {
            Log.i(TAG, "onLoadFinished");
            if(data.exception == null) {
                ActivityBase activity = (ActivityBase) getActivity();
                if (!data.theme.equals(activity.getThemeName()))
                    activity.recreate();

                Collections.sort(data.propGroups, new Comparator<MockPropGroupHolder>() {
                    @Override
                    public int compare(MockPropGroupHolder o1, MockPropGroupHolder o2) {
                        return o1.getSettingName().compareToIgnoreCase(o2.getSettingName());
                    }
                });

                rvPropsAdapter.set(data.propGroups, application);
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }else {
                Log.e(TAG, Log.getStackTraceString(data.exception));
                Snackbar.make(getView(), data.exception.toString(), Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<PropsDataHolder> loader) {
            // Do nothing
        }
    };

    private static class PropsDataLoader extends AsyncTaskLoader<PropsDataHolder> {
        private AppGeneric application;
        public PropsDataLoader setApp(AppGeneric application) {
            this.application = application;
            return this;
        }

        PropsDataLoader(Context context) {
            super(context);
            setUpdateThrottle(1000);
        }

        @Nullable
        @Override
        public PropsDataHolder loadInBackground() {
            Log.i(TAG, "Data loader started");
            PropsDataHolder data = new PropsDataHolder();
            try {
                data.theme = XLuaCall.getTheme(getContext());
                Collection<MockPropSetting> props = XMockQuery.getAllProperties(getContext(), application.getPackageName());
                Collection<LuaSettingEx> settings = new ArrayList<>(XMockQuery.getAllSettings(getContext(), true, XLuaSettingsDatabase.GLOBAL_USER, application.getPackageName()));
                Log.i(TAG, "props size=" + props.size() + " settings size=" + settings.size());
                data.propGroups = new ArrayList<>(MockPropConversions.createHolders(getContext(), props, settings));
                Log.i(TAG, "prop groups from cursor=" + data.propGroups.size());
            }catch (Throwable ex) {
                data.propGroups.clear();
                data.exception = ex;
                Log.e(TAG, ex.getMessage());
            }

            Log.i(TAG, "DataLoader Props Finished=" + data.propGroups.size());
            return data;
        }
    }

    private static class PropsDataHolder {
        String theme;
        List<MockPropGroupHolder> propGroups = new ArrayList<>();
        Throwable exception = null;
    }
}
