package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.settings.LuaSettingExtended;
import eu.faircode.xlua.api.settings.LuaSettingsDatabase;
import eu.faircode.xlua.api.standard.UserIdentityPacket;
import eu.faircode.xlua.api.xlua.XLuaCall;
import eu.faircode.xlua.api.xmock.XMockQuery;
import eu.faircode.xlua.api.xmock.call.KillAppCommand;
import eu.faircode.xlua.dialogs.SettingAddDialog;
import eu.faircode.xlua.utilities.CollectionUtil;
import eu.faircode.xlua.utilities.UiUtil;
import eu.faircode.xlua.utilities.ViewUtil;

public class FragmentSettings  extends Fragment implements View.OnClickListener {
    private static final String TAG = "XLua.FragmentSettings";

    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private AdapterSetting rvAdapter;
    private RecyclerView rvSettings;

    private TextView tvPackageName;
    private TextView tvPackageUid;
    private TextView tvPackageFull;
    private ImageView ivPackageIcon;

    private ImageView ivExpander;
    private CardView cvAppView;

    private Button btProperties;
    private Button btConfigs;
    private Button btKill;

    private AppGeneric application;

    private FloatingActionButton floatingActionOne, floatingActionTwo, floatingActionThree;
    private Animation fabOpen, fabClose, fromBottom, toBottom;
    private boolean isActionOpen = false;

    private boolean isViewOpen = true;
    private int lastHeight = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View main = inflater.inflate(R.layout.settingrecyclerview, container, false);
        initRefresh(main);
        initRecyclerView(main);

        application = AppGeneric.from(getArguments(), getContext());
        Log.i(TAG, "Application Object Created=" + application);

        ivExpander = main.findViewById(R.id.ivExpanderSettingsApp);
        cvAppView = main.findViewById(R.id.cvAppInfoSettings);

        btProperties = main.findViewById(R.id.btSettingsToProperties);
        btConfigs = main.findViewById(R.id.btSettingsToConfigs);
        btKill = main.findViewById(R.id.btSettingsKillApp);

        tvPackageName = main.findViewById(R.id.tvSettingsPackageName);
        tvPackageUid = main.findViewById(R.id.tvSettingsPackageUid);
        tvPackageFull = main.findViewById(R.id.tvSettingsPackageFull);
        ivPackageIcon = main.findViewById(R.id.ivSettingsAppIcon);

        tvPackageName.setText(application.getName());
        tvPackageFull.setText(application.getPackageName());
        tvPackageUid.setText(String.valueOf(application.getUid()));
        application.initIcon(ivPackageIcon, Objects.requireNonNull(getContext()));

        floatingActionOne = main.findViewById(R.id.flSettingsButtonOne);
        floatingActionTwo = main.findViewById(R.id.flSettingsButtonTwo);
        floatingActionThree = main.findViewById(R.id.flSettingsButtonThree);

        fabOpen = AnimationUtils.loadAnimation
                (getContext(),R.anim.rotate_open_anim_one);
        fabClose = AnimationUtils.loadAnimation
                (getContext(),R.anim.rotate_close_anim_one);
        fromBottom = AnimationUtils.loadAnimation
                (getContext(),R.anim.from_bottom_anim_one);
        toBottom = AnimationUtils.loadAnimation
                (getContext(),R.anim.to_bottom_anim_one);

        rvSettings.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // If scrolling up, show the FAB; if scrolling down, hide the FAB
                if (dy > 0 && floatingActionOne.isShown()) {
                    if(isActionOpen)
                        invokeFloatingAction();

                    floatingActionOne.hide();
                } else if (dy < 0 && !floatingActionOne.isShown()) {
                    floatingActionOne.show();
                }
            }
        });

        cvAppView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = cvAppView.getHeight();
                if(height != lastHeight) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) cvAppView.getLayoutParams();
                    int totalHeight = height + layoutParams.topMargin + layoutParams.bottomMargin + 15;
                    Log.i(TAG, "Height changed before=" + lastHeight + " now=" + height);
                    rvSettings.setPadding(0, totalHeight, 0, 0);

                    int lastHeightCopy = lastHeight;
                    lastHeight = height;

                    UiUtil.setSwipeRefreshLayoutEndOffset(getContext(), swipeRefresh, totalHeight);

                    LinearLayoutManager layoutManager = (LinearLayoutManager) rvSettings.getLayoutManager();
                    assert layoutManager != null;
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();

                    if (firstVisiblePosition == 0) {
                        if(height > lastHeightCopy)
                            rvSettings.scrollBy(0, -totalHeight);
                    }
                }
            }
        });

        updateExpanded();
        wire();
        return main;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.i(TAG, " onClick id=" + id);

        switch (id) {
            case R.id.btSettingsKillApp:
                final XResult res = KillAppCommand.invokeEx(v.getContext(), application.getPackageName(), application.getUid());
                Toast.makeText(getContext(), res.getResultMessage(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.ivExpanderSettingsApp:
                updateExpanded();
                break;
            case R.id.btSettingsToProperties:
                //have ability to go back and resume where we were last
                //Save this instance jump there go back here if needed
                Intent propsIntent = new Intent(v.getContext(), ActivityProperties.class);
                Log.i(TAG, "opening props with package=" + application);
                propsIntent.putExtra("packageName", application.getPackageName());
                v.getContext().startActivity(propsIntent);
                break;
            case R.id.btSettingsToConfigs:
                Intent configIntent = new Intent(v.getContext(), ActivityConfig.class);
                configIntent.putExtra("packageName", application.getPackageName());
                v.getContext().startActivity(configIntent);
                break;
            case R.id.flSettingsButtonOne:
                invokeFloatingAction();
                break;
            case R.id.flSettingsButtonTwo:
                //
                break;
            case R.id.flSettingsButtonThree:
                SettingAddDialog setDialog = new SettingAddDialog();
                assert getFragmentManager() != null;
                setDialog.setApplication(application);
                setDialog.show(getFragmentManager(), "Add Setting");
                break;
        }

    }

    private void invokeFloatingAction() {
        isActionOpen = !isActionOpen;
        //Start the Animation
        if(isActionOpen) {
            floatingActionThree.startAnimation(fromBottom);
            floatingActionTwo.startAnimation(fromBottom);
            floatingActionOne.startAnimation(fabOpen);
        }else {
            floatingActionThree.startAnimation(toBottom);
            floatingActionTwo.startAnimation(toBottom);
            floatingActionOne.startAnimation(fabClose);
        }

        //Set the Visibility
        int visibility = isActionOpen ? View.VISIBLE : View.INVISIBLE;
        floatingActionTwo.setVisibility(visibility);
        floatingActionThree.setVisibility(visibility);

        //Set to be clickable or not
        floatingActionThree.setLongClickable(isActionOpen);
        floatingActionThree.setClickable(isActionOpen);
    }

    private void wire() {
        ivExpander.setOnClickListener(this);
        floatingActionOne.setOnClickListener(this);
        floatingActionTwo.setOnClickListener(this);
        floatingActionThree.setOnClickListener(this);
        btProperties.setOnClickListener(this);
        btKill.setOnClickListener(this);
        btConfigs.setOnClickListener(this);
        cvAppView.setOnClickListener(this);
    }

    void updateExpanded() {
        isViewOpen = !isViewOpen;
        ViewUtil.setViewsVisibility(ivExpander, isViewOpen, btProperties, btConfigs, btKill);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(application != null)
            loadData();
    }

    public void filter(String query) {
        if (rvAdapter != null)
            rvAdapter.getFilter().filter(query);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initRefresh(final View view) {
        progressBar = view.findViewById(R.id.pbSettings);
        int colorAccent = XUtil.resolveColor(requireContext(), R.attr.colorAccent);
        swipeRefresh = view.findViewById(R.id.swipeRefreshSettings);
        swipeRefresh.setColorSchemeColors(colorAccent, colorAccent, colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    private void initRecyclerView(final View view) {
        rvSettings = view.findViewById(R.id.rvSettings);
        rvSettings.setVisibility(View.VISIBLE);
        rvSettings.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
                return true;
            }
        };

        llm.setAutoMeasureEnabled(true);
        rvSettings.setLayoutManager(llm);
        rvAdapter = new AdapterSetting(getFragmentManager());
        rvSettings.setAdapter(rvAdapter);
    }

    public void loadData() {
        Log.i(TAG, "Starting data loader");
        LoaderManager manager = getActivity().getSupportLoaderManager();
        manager.restartLoader(ActivityMain.LOADER_DATA, new Bundle(), dataLoaderCallbacks).forceLoad();
    }

    LoaderManager.LoaderCallbacks dataLoaderCallbacks = new LoaderManager.LoaderCallbacks<SettingsDataHolder>() {
        @Override
        public Loader<SettingsDataHolder> onCreateLoader(int id, Bundle args) {
            return new SettingsDataLoader(getContext()).setApp(application);
        }

        @Override
        public void onLoadFinished(Loader<SettingsDataHolder> loader, SettingsDataHolder data) {
            Log.i(TAG, "onLoadFinished");
            if(data.exception == null) {
                ActivityBase activity = (ActivityBase) getActivity();
                if (!data.theme.equals(activity.getThemeName()))
                    activity.recreate();

                if(CollectionUtil.isValid(data.settings)) {
                    Collections.sort(data.settings, new Comparator<LuaSettingExtended>() {
                        @Override
                        public int compare(LuaSettingExtended o1, LuaSettingExtended o2) {
                            if(o1 == null && o2 == null || o1.getName() == null || o2.getName() == null)
                                return 0;

                            return o1.getName().compareToIgnoreCase(o2.getName());
                        }
                    });

                    rvAdapter.set(data.settings, application);
                }
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }else {
                Log.e(TAG, Log.getStackTraceString(data.exception));
                Snackbar.make(getView(), data.exception.toString(), Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<SettingsDataHolder> loader) {
            // Do nothing
        }
    };

    private static class SettingsDataLoader extends AsyncTaskLoader<SettingsDataHolder> {
        private AppGeneric application;
        public SettingsDataLoader setApp(AppGeneric application) {
            this.application = application;
            return this;
        }

        SettingsDataLoader(Context context) {
            super(context);
            setUpdateThrottle(1000);
        }

        @Nullable
        @Override
        public SettingsDataHolder loadInBackground() {
            Log.i(TAG, "Data loader started");
            SettingsDataHolder data = new SettingsDataHolder();
            try {
                data.theme = XLuaCall.getTheme(getContext());
                Log.i(TAG, "Getting settings for=" + application);
                data.settings = new ArrayList<>(XMockQuery.getAllSettings(getContext(), application));
                Log.i(TAG, "settings from cursor=" + data.settings.size());
            }catch (Throwable ex) {
                data.settings.clear();
                data.exception = ex;
                Log.e(TAG, ex.getMessage());
            }

            Log.i(TAG, "DataLoader Settings Finished=" + data.settings.size());
            return data;
        }
    }

    private static class SettingsDataHolder {
        String theme;
        List<LuaSettingExtended> settings = new ArrayList<>();
        Throwable exception = null;
    }
}
