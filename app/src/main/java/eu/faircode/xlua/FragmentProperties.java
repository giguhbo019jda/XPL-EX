package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import eu.faircode.xlua.api.settings.LuaSettingExtended;
import eu.faircode.xlua.api.xlua.XLuaCall;
import eu.faircode.xlua.api.xmock.XMockQuery;
import eu.faircode.xlua.dialogs.PropertyAddDialog;

public class FragmentProperties extends Fragment implements View.OnClickListener {
    private final static String TAG = "XLua.FragmentProperties";

    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private AdapterPropertiesGroup rvPropsAdapter;

    private RecyclerView rvPropGroups;
    private AppGeneric application;

    private TextView tvPropCount;

    private FloatingActionButton flMain, flAdd;
    private Animation fabOpen, fabClose, fromBottom, toBottom;
    private boolean isActionOpen = false;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View main = inflater.inflate(R.layout.proprecyclerview, container, false);
        application = AppGeneric.from(getArguments(), getContext());
        Log.i(TAG, "Application Object Created=" + application);

        tvPropCount = main.findViewById(R.id.tvPropCountProperties);

        initRefresh(main);
        initRecyclerView(main);

        TextView tvPackageName = main.findViewById(R.id.tvPropertiesPackageName);
        TextView tvPackage = main.findViewById(R.id.tvPropertiesPackageFull);
        TextView tvPackageUid = main.findViewById(R.id.tvPropertiesPackageUid);
        ImageView ivAppIcon = main.findViewById(R.id.ivPropertiesAppIcon);

        tvPackageName.setText(application.getName());
        tvPackage.setText(application.getPackageName());
        tvPackageUid.setText(String.valueOf(application.getUid()));
        application.initIcon(ivAppIcon, Objects.requireNonNull(getContext()));

        flMain = main.findViewById(R.id.flPropertiesMainButton);
        flAdd = main.findViewById(R.id.flPropertiesAddMapButton);

        fabOpen = AnimationUtils.loadAnimation (getContext(),R.anim.rotate_open_anim_one);
        fabClose = AnimationUtils.loadAnimation (getContext(),R.anim.rotate_close_anim_one);
        fromBottom = AnimationUtils.loadAnimation (getContext(),R.anim.from_bottom_anim_one);
        toBottom = AnimationUtils.loadAnimation (getContext(),R.anim.to_bottom_anim_one);

        rvPropGroups.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // If scrolling up, show the FAB; if scrolling down, hide the FAB
                if (dy > 0 && flMain.isShown()) {
                    if(isActionOpen)
                        invokeFloatingAction();

                    flMain.hide();
                } else if (dy < 0 && !flMain.isShown())
                    flMain.show();
            }
        });

        wire();
        loadData();
        return main;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.i(TAG, " onClick id=" + id);

        switch (id) {
            case R.id.flPropertiesMainButton:
                invokeFloatingAction();
                break;
            case R.id.flPropertiesAddMapButton:
                PropertyAddDialog setDialog = new PropertyAddDialog();
                assert getFragmentManager() != null;
                setDialog.show(getFragmentManager(), "Add Property");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void invokeFloatingAction() {
        isActionOpen = !isActionOpen;
        //Start the Animation
        if(isActionOpen) {
            flAdd.startAnimation(fromBottom);
            flMain.startAnimation(fabOpen);
        }else {
            flAdd.startAnimation(toBottom);
            flMain.startAnimation(fabClose);
        }

        //Set the Visibility
        int visibility = isActionOpen ? View.VISIBLE : View.INVISIBLE;
        flAdd.setVisibility(visibility);

        //Set to be clickable or not
        flAdd.setLongClickable(isActionOpen);
        flAdd.setClickable(isActionOpen);
    }

    private void wire() {
        flMain.setOnClickListener(this);
        flAdd.setOnClickListener(this);
    }

    public void filter(String query) {
        if (rvPropsAdapter != null)
            rvPropsAdapter.getFilter().filter(query);
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
        rvPropsAdapter = new AdapterPropertiesGroup(getFragmentManager(), application);
        rvPropGroups.setAdapter(rvPropsAdapter);
    }

    public void loadData() {
        Log.i(TAG, "Starting data loader");
        LoaderManager manager = getActivity().getSupportLoaderManager();
        manager.restartLoader(ActivityMain.LOADER_DATA, new Bundle(), dataLoaderCallbacks).forceLoad();
    }

    LoaderManager.LoaderCallbacks<PropsDataHolder> dataLoaderCallbacks = new LoaderManager.LoaderCallbacks<PropsDataHolder>() {
        @NonNull
        @Override
        public Loader<PropsDataHolder> onCreateLoader(int id, Bundle args) { return new PropsDataLoader(getContext()).setApp(application); }

        @SuppressLint("SetTextI18n")
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

                tvPropCount.setText(Integer.toString(data.totalProps));
                rvPropsAdapter.set(data.propGroups);
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
                Log.i(TAG, "Loading properties with application=" + application);
                Collection<MockPropSetting> props = XMockQuery.getAllProperties(getContext(), application);
                Collection<LuaSettingExtended> settings = new ArrayList<>(XMockQuery.getAllSettings(getContext(), true, application.getUid(), application.getPackageName()));
                data.totalProps = props.size();
                Log.i(TAG, "props size=" + props.size() + " settings size=" + settings.size());
                data.propGroups = new ArrayList<>(MockPropConversions.createHolders(getContext(), props, settings));
                Log.i(TAG, "prop groups from cursor=" + data.propGroups.size());
            }catch (Throwable ex) {
                data.propGroups.clear();
                data.exception = ex;
                data.totalProps = 0;
                Log.e(TAG, ex.getMessage());
            }

            Log.i(TAG, "DataLoader Props Finished=" + data.propGroups.size());
            return data;
        }
    }

    private static class PropsDataHolder {
        String theme;
        List<MockPropGroupHolder> propGroups = new ArrayList<>();
        int totalProps;
        Throwable exception = null;
    }
}
