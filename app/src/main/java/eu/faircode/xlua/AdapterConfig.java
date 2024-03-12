package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.faircode.xlua.api.configs.MockConfig;
import eu.faircode.xlua.api.settings.XLuaSettingPacket;
import eu.faircode.xlua.api.config.XMockConfigSetting;
import eu.faircode.xlua.api.config.XMockConfigConversions;
import eu.faircode.xlua.api.config.XMockConfig;
import eu.faircode.xlua.api.settingsex.LuaSettingEx;
import eu.faircode.xlua.api.xlua.call.PutSettingCommand;
import eu.faircode.xlua.randomizers.IRandomizer;
import eu.faircode.xlua.randomizers.GlobalRandoms;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.SettingUtil;
import eu.faircode.xlua.utilities.ViewUtil;

public class AdapterConfig extends RecyclerView.Adapter<AdapterConfig.ViewHolder> {
    private static final String TAG = "XLua.AdapterConfig";

    private MockConfig config = null;

    private final List<LuaSettingEx> settings = new ArrayList<>();
    private final HashMap<String, Boolean> expanded = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public class ViewHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

        final View itemView;

        final TextView tvSettingName;
        final CheckBox cbSettingEnabled;
        final TextInputEditText tiSettingsValue;
        final ImageView ivExpanderSettings;

        final TextView tvDescription;

        final ImageView ivBtRandom;
        final Spinner spRandomSelector;
        final ArrayAdapter<IRandomizer> spRandomizer;

        ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            ivExpanderSettings = itemView.findViewById(R.id.ivSettingConfigExpander);
            tvSettingName = itemView.findViewById(R.id.tvSettingConfigName);
            cbSettingEnabled = itemView.findViewById(R.id.cbEnableConfigSetting);
            tiSettingsValue = itemView.findViewById(R.id.tiConfigSettingsValue);

            //randomizers = RandomizersGlobal.getRandomizers();
            spRandomSelector = itemView.findViewById(R.id.spConfigRandomSelection);
            ivBtRandom = itemView.findViewById(R.id.ivBtRandomConfigSettingValue);
            tvDescription = itemView.findViewById(R.id.tvConfigSettingDescription);

            spRandomizer = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item);
            initDropDown();
            if(DebugUtil.isDebug())
                Log.i(TAG, "Created the Adapter Item");
        }

        public void initDropDown() {
            //Start of Drop Down
            spRandomizer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
                        Log.i(TAG, "RANDOMIZER SELECTED=" + name);

                    if (name == null ? spRandomSelector.getTag() != null : !name.equals(spRandomSelector.getTag()))
                        spRandomSelector.setTag(name);
                }
            });

            spRandomizer.clear();
            spRandomizer.addAll(GlobalRandoms.getRandomizers());
        }

        private void unWire() {
            itemView.setOnClickListener(null);
            cbSettingEnabled.setOnCheckedChangeListener(null);
            ivBtRandom.setOnClickListener(null);
        }

        private void wire() {
            itemView.setOnClickListener(this);
            cbSettingEnabled.setOnCheckedChangeListener(this);
            ivBtRandom.setOnClickListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(final View view) {
            int id = view.getId();
            if(DebugUtil.isDebug())
                Log.i(TAG, "onClick id=" + id);

            final LuaSettingEx setting = settings.get(getAdapterPosition());
            String name = setting.getName();

            switch (id) {
                case R.id.itemViewConfig:
                    ViewUtil.internalUpdateExpanded(expanded, name);
                    updateExpanded();
                    break;
                case R.id.ivBtRandomConfigSettingValue:
                    Log.i(TAG, "Randomizer Button Selected");
                    IRandomizer randomizer = (IRandomizer) spRandomSelector.getSelectedItem();
                    String randomValue = randomizer.generateString();
                    Log.i(TAG, "Randomized Value=" + randomValue);
                    tiSettingsValue.setText(randomizer.generateString());
                    setting.setValue(randomValue);
                    break;
            }
        }

        @SuppressLint({"NotifyDataSetChanged", "NonConstantResourceId"})
        @Override
        public void onCheckedChanged(final CompoundButton cButton, boolean isChecked) {
            if(DebugUtil.isDebug())
                Log.i(TAG, "onCheckedChanged");

            final LuaSettingEx setting = settings.get(getAdapterPosition());
            final int id = cButton.getId();
            if(DebugUtil.isDebug())
                Log.i(TAG, "Item checked=" + id + " == " + setting);

            switch (id) {
                case R.id.cbEnableConfigSetting:
                    //setting.setEnabled(isChecked);
                    notifyDataSetChanged();
                    break;
            }
        }

        void updateExpanded() {
            if(DebugUtil.isDebug())
                Log.i(TAG, "Expanding Object");

            LuaSettingEx setting = settings.get(getAdapterPosition());
            String name = setting.getName();
            boolean isExpanded = expanded.containsKey(name) && Boolean.TRUE.equals(expanded.get(name));

            ViewUtil.setViewsVisibility(ivExpanderSettings, isExpanded, tiSettingsValue, spRandomSelector, ivBtRandom, tvDescription);
        }
    }

    AdapterConfig() { setHasStableIds(true); }

    void applyConfig(Context context, String packageName) {
        /*if(DebugUtil.isDebug())
            Log.i(TAG, "APPLYING:" + settings.size());

        for (XMockConfigSetting setting : settings) {
            if(DebugUtil.isDebug()) {
                Log.i(TAG, "Enum item setting");
                Log.i(TAG, "setting [" + setting + "]");
            }

            if(setting.isEnabled()) {
                XLuaSettingPacket packet = new XLuaSettingPacket();
                packet.setName(setting.getName());
                packet.setValue(setting.getValue());
                packet.setUser(0);
                packet.setCategory("settings");

                if(DebugUtil.isDebug())
                    Log.i(TAG, "Applying [" + packet + "]");

                int r = BundleUtil.readInteger(PutSettingCommand.invoke(context, packet), "result");
                if(DebugUtil.isDebug())
                    Log.i(TAG, "Result for apply =" + r);
            }
        }*/
    }

    @SuppressLint("NotifyDataSetChanged")
    void set(MockConfig config) {
        this.config = config;
        this.settings.clear();
        this.settings.addAll(config.getSettings());
        if(DebugUtil.isDebug())
            Log.i(TAG, "SELECTED SETTINGS COUNT=" + settings.size());

        notifyDataSetChanged();
    }

    public String getConfigName() { return config.getName(); }
    public List<LuaSettingEx> getEnabledSettings() {
        /*List<XMockConfigSetting> settingsEnabled = new ArrayList<>();
        for(XMockConfigSetting setting : settings)
            if(setting.isEnabled())
                settingsEnabled.add(setting);

        return settingsEnabled;*/
        return settings;
    }

    @Override
    public long getItemId(int position) { return settings.get(position).hashCode(); }

    @Override
    public int getItemCount() { return settings.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.configsetting, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(DebugUtil.isDebug())
            Log.i(TAG, "Adapter Item Creating Internal");

        holder.unWire();
        LuaSettingEx cSetting = settings.get(position);
        String settingName = cSetting.getName();

        holder.tvSettingName.setText(SettingUtil.cleanSettingName(settingName));
        holder.tiSettingsValue.setText(cSetting.getValue());
        holder.tvDescription.setText(cSetting.getDescription());
        //holder.cbSettingEnabled.setChecked(cSetting.isEnabled());

        for(int i = 0; i < holder.spRandomizer.getCount(); i++) {
            IRandomizer randomizer = holder.spRandomizer.getItem(i);
            if(randomizer != null && randomizer.isSetting(settingName)) {
                holder.spRandomSelector.setSelection(i);
                break;
            }
        }

        holder.updateExpanded();
        holder.wire();

        if(DebugUtil.isDebug())
            Log.i(TAG, "Adapter Item Created Internal");
    }
}
