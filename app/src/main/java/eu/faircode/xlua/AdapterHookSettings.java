package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.settings.LuaSettingExtended;
import eu.faircode.xlua.api.xstandard.interfaces.ISettingUpdate;
import eu.faircode.xlua.logger.XLog;
import eu.faircode.xlua.random.GlobalRandoms;
import eu.faircode.xlua.random.IRandomizer;
import eu.faircode.xlua.ui.AlertMessage;
import eu.faircode.xlua.ui.ISettingTransaction;
import eu.faircode.xlua.ui.SettingsQue;
import eu.faircode.xlua.ui.dialogs.SettingDeleteDialog;
import eu.faircode.xlua.utilities.SettingUtil;
import eu.faircode.xlua.utilities.StringUtil;
import eu.faircode.xlua.utilities.UiUtil;
import eu.faircode.xlua.utilities.ViewUtil;

public class AdapterHookSettings extends RecyclerView.Adapter<AdapterHookSettings.ViewHolder> {
    private static final String TAG = "XLua.AdapterGroupHooks";

    private final List<IRandomizer> randomizers = GlobalRandoms.getRandomizers();

    private final List<LuaSettingExtended> settings = new ArrayList<>();
    private final HashMap<String, Boolean> expanded = new HashMap<>();

    private SettingsQue settingsQue;
    private AppGeneric application;
    private FragmentManager fragmentManager;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener,
            View.OnClickListener,
            TextWatcher,
            View.OnLongClickListener,
            AdapterView.OnItemSelectedListener,
            ISettingUpdate,
            ISettingTransaction {

        final View view;
        final TextView tvSettingName, tvSettingNameFull, tvSettingDescription;
        final TextInputEditText tiSettingValue;
        final ImageView btRandomize, btReset, btSave, btDelete, ivExpander;
        final Spinner spRandomSelector;
        final ArrayAdapter<IRandomizer> adapterRandomizer;

        ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            this.tvSettingName = view.findViewById(R.id.tvHookSettingName);
            this.tvSettingNameFull = view.findViewById(R.id.tvHookSettingFullName);
            this.tvSettingDescription = view.findViewById(R.id.tvHookSettingDescription);
            this.tiSettingValue = view.findViewById(R.id.tvHookSettingValue);
            this.ivExpander = view.findViewById(R.id.ivExpanderSettingValue);
            this.btSave = view.findViewById(R.id.ivBtHookSettingDelete);
            this.btDelete = view.findViewById(R.id.ivBtHookSettingSave);
            this.btReset = view.findViewById(R.id.ivBtHookSettingReset);

            this.btRandomize = view.findViewById(R.id.ivBtHookSettingRandomize);
            this.spRandomSelector = view.findViewById(R.id.spHookSettingRandomizer);

            this.adapterRandomizer = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item);
            this.adapterRandomizer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spRandomSelector.setTag(null);
            this.spRandomSelector.setAdapter(adapterRandomizer);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void unWire() {
            this.view.setOnClickListener(null);
            this.tvSettingName.setOnClickListener(null);
            this.btSave.setOnClickListener(null);
            this.btReset.setOnClickListener(null);
            this.btRandomize.setOnClickListener(null);
            this.btDelete.setOnClickListener(null);
            this.ivExpander.setOnClickListener(null);
            this.tvSettingName.setOnLongClickListener(null);
            this.tiSettingValue.removeTextChangedListener(this);
            this.spRandomSelector.setOnItemSelectedListener(null);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void wire() {
            this.view.setOnClickListener(this);
            this.tvSettingName.setOnClickListener(this);
            this.btSave.setOnClickListener(this);
            this.btReset.setOnClickListener(this);
            this.btRandomize.setOnClickListener(this);
            this.btDelete.setOnClickListener(this);
            this.ivExpander.setOnClickListener(this);
            this.tvSettingName.setOnLongClickListener(this);
            this.tiSettingValue.addTextChangedListener(this);
            this.spRandomSelector.setOnItemSelectedListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(final View view) {
            int code = view.getId();
            Log.i(TAG, "onClick=" + code);

            try {
                final int pos = getAdapterPosition();
                final LuaSettingExtended setting = settings.get(pos);
                XLog.i("onClick: position=" + pos);
                switch (code) {
                    case R.id.itemViewHookSettings:
                    case R.id.ivExpanderSettingValue:
                    case R.id.tvHookSettingName:
                        XLog.e("I HAVE BEEN CLICKEDDD: " + code + " pos=" + pos + " item=" + setting.toString());
                        ViewUtil.internalUpdateExpanded(expanded, setting.getName());
                        updateExpanded();
                        break;
                    case R.id.ivBtHookSettingDelete:
                        //unWire();
                        //view.setEnabled(false);
                        SettingDeleteDialog setDialog = new SettingDeleteDialog(setting, application, pos);
                        setDialog.setCallback(this);
                        setDialog.show(fragmentManager, "Delete Setting");
                        break;
                    case R.id.ivBtHookSettingSave:
                        settingsQue.sendSetting(view.getContext(), setting, false, false, this);
                        break;
                    case R.id.ivBtHookSettingReset:
                        if(setting.isModified()) {
                            setting.resetModified(true);
                            //SettingUtil.initCardViewColor(view.getContext(), tvSettingName, cvSetting, setting);
                        }
                        break;
                    case R.id.ivBtHookSettingRandomize:
                        setting.randomizeValue(view.getContext());
                        //SettingUtil.initCardViewColor(view.getContext(), tvSettingName, cvSetting, setting);
                        break;
                }
            }catch (Exception e) { XLog.e("onClick Failed: code=" + code, e, true); }
        }

        @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
        @Override
        public void onCheckedChanged(final CompoundButton cButton, final boolean isChecked) {  }

        void updateExpanded() {
            LuaSettingExtended setting = settings.get(getAdapterPosition());
            String name = setting.getName();
            boolean isExpanded = expanded.containsKey(name) && Boolean.TRUE.equals(expanded.get(name));
            int rotation = isExpanded ? 87 : 0;
            ivExpander.setRotation(rotation);
            ViewUtil.setViewsVisibility(null, isExpanded, tiSettingValue, btDelete, btRandomize, btReset, btSave, spRandomSelector, tvSettingDescription);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onLongClick(View v) {
            int code = view.getId();
            Log.i(TAG, "onLongClick=" + code);
            try {
                final LuaSettingExtended setting = settings.get(getAdapterPosition());
                switch (code) {
                    case R.id.tvHookSettingName:
                        String desc = SettingUtil.generateDescription(setting);
                        if(!desc.equalsIgnoreCase("N/A")) Toast.makeText(view.getContext(), desc, Toast.LENGTH_SHORT).show();
                        else Toast.makeText(view.getContext(), R.string.error_no_description_setting, Toast.LENGTH_SHORT).show();
                        return true;
                }
            }catch (Exception e) { XLog.e("onLongClick Failed: code=" + code, e); }
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { updateSelection(); }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { updateSelection(); }

        private void updateSelection() { UiUtil.handleSpinnerSelection(spRandomSelector, settings, getAdapterPosition()); }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSettingUpdatedSuccessfully(Context context, LuaSettingExtended setting, XResult result) {
            setting.updateValue();
            Toast.makeText(context, "Setting updated successfully!", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }

        @Override
        public void onSettingUpdateFailed(Context context, LuaSettingExtended setting, XResult result) { AlertMessage.displayMessageFailed(context, setting, result); }

        @Override
        public void onBatchFinished(Context context, List<LuaSettingExtended> successful, List<LuaSettingExtended> failed) { }

        @Override
        public void onException(Context context, Exception e, LuaSettingExtended setting) { AlertMessage.displayMessageException(context, setting, e); }

        @Override
        public void afterTextChanged(Editable editable) {
            XLog.i("afterTextChanged: text=" + editable.toString());
            try {
                LuaSettingExtended setting = settings.get(getAdapterPosition());
                if(!setting.isBusy()) {
                    //isBusy
                    String s = editable.toString();
                    if(TextUtils.isEmpty(s)) setting.setModifiedValue(null);
                    else setting.setModifiedValue(editable.toString());
                }
            }catch (Exception e) {  XLog.e("afterTextChanged Failed: " + editable.toString(), e, true); }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSettingFinished(LuaSettingExtended setting, int position, XResult result) {
            XLog.i("Finished Transaction: pos=" + position + " res = " + result.getResultMessage() + " setting=" + setting);
            try {
                setting.updateValue();
                setting.setIsBusy(false);
                Log.i(TAG, "Successfully updated setting=" + setting.getName());
                notifyDataSetChanged();
                //if(position <= 0) throw new Exception("Invalid Position: " + position);
                //notifyItemChanged(position);
            }catch (Exception e) {
                XLog.e("Failed to Init Update for Hooks: position=" + position, e, true);
                //notifyDataSetChanged();
            }
        }
    }

    AdapterHookSettings() { setHasStableIds(true); }
    AdapterHookSettings(FragmentManager manager, AppGeneric application) { this(); this.fragmentManager = manager; this.application = application; }

    public void set(List<LuaSettingExtended> settings) {
        if(!settings.isEmpty()) {
            for(LuaSettingExtended s : settings) {
                s.resetModified();
                s.bindRandomizer(randomizers);
            }

            if(this.settingsQue == null) this.settingsQue = new SettingsQue(application);
            this.settings.clear();
            this.settings.addAll(settings);
        }
    }

    @Override
    public long getItemId(int position) { return settings.get(position).hashCode(); }

    @Override
    public int getItemCount() { return settings.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hooksetting, parent, false)); }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(!settings.isEmpty()) {
            holder.unWire();
            final LuaSettingExtended setting = settings.get(position);
            setting.bindInputTextBox(holder.tiSettingValue);
            holder.tvSettingName.setText(SettingUtil.cleanSettingName(setting.getName()));
            holder.tvSettingNameFull.setText(setting.getName());
            holder.tvSettingDescription.setText(SettingUtil.generateDescription(setting));

            boolean enable = UiUtil.initRandomizer(holder.adapterRandomizer, holder.spRandomSelector, setting, randomizers);
            holder.spRandomSelector.setEnabled(enable);
            holder.btRandomize.setEnabled(enable);
            setting.setInputText();

            holder.updateExpanded();
            holder.wire();
        }
    }
}
