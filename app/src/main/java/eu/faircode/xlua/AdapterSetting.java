package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.faircode.xlua.api.XResult;

import eu.faircode.xlua.api.settings.LuaSettingExtended;
import eu.faircode.xlua.api.settings.LuaSettingPacket;
import eu.faircode.xlua.api.xlua.XLuaCall;
import eu.faircode.xlua.dialogs.SettingDeleteDialog;
import eu.faircode.xlua.randomizers.GlobalRandoms;
import eu.faircode.xlua.randomizers.IRandomizer;
import eu.faircode.xlua.utilities.SettingUtil;
import eu.faircode.xlua.utilities.StringUtil;
import eu.faircode.xlua.utilities.ViewUtil;

public class AdapterSetting extends RecyclerView.Adapter<AdapterSetting.ViewHolder> implements Filterable {
    private static final String TAG = "XLua.AdapterSetting";

    private final List<LuaSettingExtended> settings = new ArrayList<>();
    private List<LuaSettingExtended> filtered = new ArrayList<>();

    //private final HashMap<String, XMockMappedSetting> modified = new HashMap<>();
    private final HashMap<String, Boolean> expanded = new HashMap<>();
    private final HashMap<LuaSettingExtended, String> modified = new HashMap<>();

    private AppGeneric application;
    private FragmentManager fragmentManager;
    private List<IRandomizer> randomizers;

    private boolean dataChanged = false;
    private CharSequence query = null;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Object lock = new Object();

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, TextWatcher {

        final View itemView;

        final ImageView ivSettingDrop;
        final TextView tvSettingName;
        final TextView tvSettingDescription;
        final TextInputEditText tiSettingValue;

        final ImageView ivBtSave;
        final ImageView ivBtDelete;
        final ImageView ivBtRandomize;

        final Spinner spRandomSelector;
        final ArrayAdapter<IRandomizer> spRandomizer;

        final CardView cvSetting;
        final ConstraintLayout clLayout;

        final TextView tvSettingNameFull;

        ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            cvSetting = itemView.findViewById(R.id.cvSetting);
            clLayout = itemView.findViewById(R.id.clSettingLayout);

            tvSettingNameFull = itemView.findViewById(R.id.tvSettingsSettingFullName);
            //appInfoLayout = itemView.findViewById(R.id.app_info_layout_settings);

            ivSettingDrop = itemView.findViewById(R.id.ivExpanderSettingsSetting);
            tvSettingName = itemView.findViewById(R.id.tvSettingNameLabel);
            tvSettingDescription = itemView.findViewById(R.id.tvSettingsSettingDescription);
            tiSettingValue = itemView.findViewById(R.id.tiSettingsSettingValue);

            ivBtSave = itemView.findViewById(R.id.ivBtSaveSettingSetting);
            ivBtDelete = itemView.findViewById(R.id.ivBtDeleteSetting);

            //Start of Drop Down
            spRandomizer = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item);
            spRandomizer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ivBtRandomize = itemView.findViewById(R.id.ivBtRandomSettingValue);
            spRandomSelector = itemView.findViewById(R.id.spSettingRandomizerSpinner);
            if(DebugUtil.isDebug())
                Log.i(TAG, "Created the Empty Array for Configs Fragment Config");

            spRandomSelector.setTag(null);
            spRandomSelector.setAdapter(spRandomizer);
            spRandomSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { updateSelection(); }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    updateSelection();
                }

                private void updateSelection() {
                    IRandomizer selected = (IRandomizer) spRandomSelector.getSelectedItem();
                    String name = selected.getName();
                    if(DebugUtil.isDebug())
                        Log.i(TAG, "Selected Randomizer=" + name);

                    if (name == null ? spRandomSelector.getTag() != null : !name.equals(spRandomSelector.getTag()))
                        spRandomSelector.setTag(name);
                }
            });

            spRandomizer.clear();
            spRandomizer.addAll(randomizers);
            //spRandomizer.addAll(GlobalRandoms.getRandomizers());
        }

        private void unWire() {
            itemView.setOnClickListener(null);
            ivBtRandomize.setOnClickListener(null);
            ivBtSave.setOnClickListener(null);
            tiSettingValue.removeTextChangedListener(this);
            ivSettingDrop.setOnClickListener(null);
            ivBtDelete.setOnClickListener(null);
        }

        private void wire() {
            itemView.setOnClickListener(this);
            ivBtRandomize.setOnClickListener(this);
            ivBtSave.setOnClickListener(this);
            tiSettingValue.addTextChangedListener(this);
            ivSettingDrop.setOnClickListener(this);
            ivBtDelete.setOnClickListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(final View view) {
            int id = view.getId();
            final LuaSettingExtended setting = filtered.get(getAdapterPosition());
            String name = setting.getName();

            Log.i(TAG, " onClick id=" + id + " selected=" + setting);

            switch (id) {
                case R.id.ivExpanderSettingsSetting:
                case R.id.itemViewSetting:
                    //Expand Setting
                    ViewUtil.internalUpdateExpanded(expanded, name);
                    updateExpanded();
                    break;
                case R.id.ivBtRandomSettingValue:
                    //Randomize Setting Value
                    IRandomizer randomizer = (IRandomizer) spRandomSelector.getSelectedItem();
                    String randomValue = randomizer.generateString();
                    SettingUtil.updateSetting(setting, randomValue, modified);
                    tiSettingValue.setText(setting.getValue());
                    break;
                case R.id.ivBtSaveSettingSetting:
                    if(modified.containsKey(setting))
                        sendSetting(view.getContext(), setting, false, false);
                    break;
                case R.id.ivBtDeleteSetting:
                    if(fragmentManager == null) {
                        sendSetting(itemView.getContext(), setting, true, false);
                    }else {
                        SettingDeleteDialog setDialog = new SettingDeleteDialog(setting, application);
                        setDialog.show(fragmentManager, "Delete Setting");
                        //setDialog.wait();
                    }

                    break;
            }
        }

        public void sendSetting(final Context context, final LuaSettingExtended setting, boolean deleteSetting, boolean forceKill) {
            final LuaSettingPacket packet = LuaSettingPacket.create(setting, LuaSettingPacket.getCodeInsertOrDelete(deleteSetting), forceKill)
                    .copyIdentification(application);

            executor.submit(new Runnable() {
                @Override
                public void run() {
                    synchronized (lock) {
                        final XResult ret = XLuaCall.sendSetting(context, packet);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void run() {
                                if(ret.succeeded()) {
                                    cvSetting.setCardBackgroundColor(XUtil.resolveColor(context, R.attr.cardForegroundColor));
                                    modified.remove(setting);
                                }

                                Toast.makeText(context, ret.getResultMessage(), Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void afterTextChanged(Editable editable) {
            LuaSettingExtended setting = filtered.get(getAdapterPosition());
            SettingUtil.updateSetting(setting,editable.toString(), modified);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        void updateExpanded() {
            LuaSettingExtended setting = filtered.get(getAdapterPosition());
            String name = setting.getName();
            Log.w(TAG, " name=" + name);
            boolean isExpanded = expanded.containsKey(name) && Boolean.TRUE.equals(expanded.get(name));
            ViewUtil.setViewsVisibility(ivSettingDrop, isExpanded, tvSettingDescription, tiSettingValue, spRandomSelector, ivBtRandomize, ivBtSave, ivBtDelete);
        }
    }

    AdapterSetting() { setHasStableIds(true); this.randomizers = GlobalRandoms.getRandomizers(); }
    AdapterSetting(FragmentManager fragmentManager) { this(); this.fragmentManager = fragmentManager; }

    void randomizeAll(Context context) {

    }

    void set(List<LuaSettingExtended> settings, AppGeneric application) {
        this.dataChanged = true;
        this.settings.clear();
        this.settings.addAll(settings);
        this.application = application;
        if(DebugUtil.isDebug())
            Log.i(TAG, "Internal Count=" + this.settings.size() + " app=" + application);

        getFilter().filter(query);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            private boolean expanded1 = false;

            @Override
            protected FilterResults performFiltering(CharSequence query) {
                AdapterSetting.this.query = query;
                List<LuaSettingExtended> visible = new ArrayList<>(settings);
                List<LuaSettingExtended> results = new ArrayList<>();

                if (TextUtils.isEmpty(query))
                    results.addAll(visible);
                else {
                    String q = query.toString().toLowerCase().trim();
                    for(LuaSettingExtended setting : visible) {
                        if(setting.getName().toLowerCase().contains(q))
                            results.add(setting);
                        else if(setting.getValue() != null && setting.getValue().toLowerCase().contains(q))
                            results.add(setting);
                    }
                }

                if (results.size() == 1) {
                    String settingName = results.get(0).getName();
                    if (!expanded.containsKey(settingName)) {
                        expanded1 = true;
                        expanded.put(settingName, true);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = results;
                filterResults.count = results.size();
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence query, FilterResults result) {
                final List<LuaSettingExtended> settings = (result.values == null ? new ArrayList<LuaSettingExtended>() : (List<LuaSettingExtended>) result.values);
                Log.i(TAG, "Filtered settings size=" + settings.size());

                if(dataChanged) {
                    dataChanged = false;
                    filtered = settings;
                    notifyDataSetChanged();
                }else {
                    DiffUtil.DiffResult diff =
                            DiffUtil.calculateDiff(new AppDiffCallback(expanded1, filtered, settings));
                    filtered = settings;
                    diff.dispatchUpdatesTo(AdapterSetting.this);
                }
            }
        };
    }

    private static class AppDiffCallback extends DiffUtil.Callback {
        private final boolean refresh;
        private final List<LuaSettingExtended> prev;
        private final List<LuaSettingExtended> next;

        AppDiffCallback(boolean refresh, List<LuaSettingExtended> prev, List<LuaSettingExtended> next) {
            this.refresh = refresh;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public int getOldListSize() {
            return prev.size();
        }

        @Override
        public int getNewListSize() {
            return next.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            LuaSettingExtended s1 = prev.get(oldItemPosition);
            LuaSettingExtended s2 = next.get(newItemPosition);
            return (!refresh && s1.getName().equalsIgnoreCase(s2.getName()));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            LuaSettingExtended s1 = prev.get(oldItemPosition);
            LuaSettingExtended s2 = next.get(newItemPosition);

            if(!s1.getName().equalsIgnoreCase(s2.getName()))
                return false;

            if(s1.getValue() == null || s2.getValue() == null)
                return false;

            return s1.getValue().equalsIgnoreCase(s2.getValue());
        }
    }

    @Override
    public long getItemId(int position) { return filtered.get(position).hashCode(); }

    @Override
    public int getItemCount() { return filtered.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.settingitem, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.unWire();
        LuaSettingExtended setting = filtered.get(position);
        String settingName = setting.getName();
        holder.tvSettingName.setText(SettingUtil.cleanSettingName(settingName));
        holder.tvSettingDescription.setText(SettingUtil.generateDescription(setting));
        holder.tvSettingNameFull.setText(setting.getName());

        if(StringUtil.isValidString(setting.getValue())) {
            holder.cvSetting.setCardBackgroundColor(XUtil.resolveColor(holder.itemView.getContext(), R.attr.colorSystem));
            holder.tiSettingValue.setText(setting.getValue());
        }else {
            holder.cvSetting.setCardBackgroundColor(XUtil.resolveColor(holder.itemView.getContext(), R.attr.cardForegroundColor));
            holder.tiSettingValue.setText("");
        }

        for(int i = 0; i < holder.spRandomizer.getCount(); i++) {
            IRandomizer randomizer = holder.spRandomizer.getItem(i);
            if(randomizer != null && randomizer.isSetting(settingName)) {
                holder.spRandomSelector.setSelection(i);
                break;
            }
        }

        holder.updateExpanded();
        holder.wire();
    }
}
