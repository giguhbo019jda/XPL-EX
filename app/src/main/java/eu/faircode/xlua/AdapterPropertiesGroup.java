package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.os.Process;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.faircode.xlua.api.app.XLuaApp;
import eu.faircode.xlua.api.config.XMockConfigSetting;
import eu.faircode.xlua.api.cpu.XMockCpu;
import eu.faircode.xlua.api.hook.assignment.XLuaAssignment;
import eu.faircode.xlua.api.props.XMockPropGroup;
import eu.faircode.xlua.api.props.XMockPropSetting;
import eu.faircode.xlua.api.xmock.XMockCall;
import eu.faircode.xlua.utilities.ViewUtil;

public class AdapterPropertiesGroup extends RecyclerView.Adapter<AdapterPropertiesGroup.ViewHolder> implements Filterable {
    private static final String TAG = "XLua.AdapterPropertiesGroup";

    private List<XMockPropGroup> groups = new ArrayList<>();
    private List<XMockPropGroup> filtered = new ArrayList<>();
    private HashMap<String, Boolean> expanded = new HashMap<>();


    private boolean dataChanged = false;
    private CharSequence query = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public class ViewHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextWatcher, View.OnTouchListener {

        final View itemView;

        final ImageView ivPropertiesDrop;
        final TextView tvSettingName;
        final CheckBox cbEnableSetting;
        final TextInputEditText tiPropertyValue;

        final ArrayList<String> propArrayAdapter;
        final ListView propListView;

        ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            ivPropertiesDrop = itemView.findViewById(R.id.ivSettingDropDown);
            tvSettingName = itemView.findViewById(R.id.tvSettingName);
            cbEnableSetting = itemView.findViewById(R.id.cbEnableSetting);
            tiPropertyValue = itemView.findViewById(R.id.tiSettingValue);
            propListView = itemView.findViewById(R.id.lvPropertiesList);

            propArrayAdapter = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_list_item_1, propArrayAdapter);
            propListView.setAdapter(adapter);
        }


        @SuppressLint("ClickableViewAccessibility")
        private void unWire() {
            itemView.setOnClickListener(null);
            cbEnableSetting.setOnCheckedChangeListener(null);
            tiPropertyValue.removeTextChangedListener(this);
            ivPropertiesDrop.setOnClickListener(null);
            propListView.setOnTouchListener(null);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void wire() {
            itemView.setOnClickListener(this);
            cbEnableSetting.setOnCheckedChangeListener(this);
            tiPropertyValue.addTextChangedListener(this);
            ivPropertiesDrop.setOnClickListener(this);
            propListView.setOnTouchListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(final View view) {
            int id = view.getId();
            if(DebugUtil.isDebug())
                Log.i(TAG, "onClick id=" + id);

            final XMockPropGroup group = filtered.get(getAdapterPosition());
            String name = group.getSettingName();

            Log.i(TAG, "selected=" + group);

            switch (id) {
                case R.id.ivSettingDropDown:
                case R.id.itemViewPropGroup:
                    ViewUtil.internalUpdateExpanded(expanded, name);
                    updateExpanded();
                    break;
            }
        }

        @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
        @Override
        public void onCheckedChanged(final CompoundButton cButton, final boolean isChecked) {
            Log.i(TAG, "onCheckedChanged");
            final XMockPropGroup group = filtered.get(getAdapterPosition());
            final String name = group.getSettingName();
            final int id = cButton.getId();

            Log.i(TAG, "Item Checked=" + id + "==" + name);

            switch (id) {
                case R.id.cbEnableSetting:
                    for(XMockPropSetting setting : group.getProperties())
                        setting.setIsEnabled(isChecked);

                    notifyDataSetChanged();
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "put group result=" + XMockCall.setPropGroupState(cButton.getContext(), name, isChecked));
                        }
                    });

                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        void updateExpanded() {
            XMockPropGroup group = filtered.get(getAdapterPosition());
            String name = group.getSettingName();
            boolean isExpanded = expanded.containsKey(name) && Boolean.TRUE.equals(expanded.get(name));
            ViewUtil.setViewsVisibility(ivPropertiesDrop, isExpanded, tiPropertyValue, propListView);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            int action = event.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            return false;
        }
    }

    AdapterPropertiesGroup() { setHasStableIds(true); }
    void set(List<XMockPropGroup> groups) {
        this.dataChanged = true;
        this.groups.clear();
        this.groups.addAll(groups);

        if(DebugUtil.isDebug())
            Log.i(TAG, "Internal Count=" + this.groups.size());

        //notifyDataSetChanged();
        getFilter().filter(query);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            private boolean expanded1 = false;

            @Override
            protected FilterResults performFiltering(CharSequence query) {
                AdapterPropertiesGroup.this.query = query;
                List<XMockPropGroup> visible = new ArrayList<>(groups);
                List<XMockPropGroup> results = new ArrayList<>();

                if (TextUtils.isEmpty(query))
                    results.addAll(visible);
                else {
                    String q = query.toString().toLowerCase().trim();
                    for(XMockPropGroup p : visible) {
                        if(p.getSettingName().toLowerCase().contains(q))
                            results.add(p);
                        else if(p.getValue() != null && p.getValue().toLowerCase().contains(q))
                            results.add(p);
                        else {
                            for(XMockPropSetting setting : p.getProperties()) {
                                if(setting.getPropertyName().toLowerCase().contains(q))
                                    results.add(p);
                            }
                        }
                    }
                }

                if (results.size() == 1) {
                    String settingName = results.get(0).getSettingName();
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

            @Override
            protected void publishResults(CharSequence query, FilterResults result) {
                final List<XMockPropGroup> groups = (result.values == null ? new ArrayList<XMockPropGroup>() : (List<XMockPropGroup>) result.values);
                Log.i(TAG, "Filtered groups size=" + groups.size());

                if(dataChanged) {
                    dataChanged = false;
                    filtered = groups;
                    notifyDataSetChanged();
                }else {
                    DiffUtil.DiffResult diff =
                            DiffUtil.calculateDiff(new AdapterPropertiesGroup.AppDiffCallback(expanded1, filtered, groups));
                    filtered = groups;
                    diff.dispatchUpdatesTo(AdapterPropertiesGroup.this);
                }
            }
        };
    }


    private class AppDiffCallback extends DiffUtil.Callback {
        private final boolean refresh;
        private final List<XMockPropGroup> prev;
        private final List<XMockPropGroup> next;

        AppDiffCallback(boolean refresh, List<XMockPropGroup> prev, List<XMockPropGroup> next) {
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
            XMockPropGroup g1 = prev.get(oldItemPosition);
            XMockPropGroup g2 = next.get(newItemPosition);
            return (!refresh && g1.getSettingName().equalsIgnoreCase(g2.getSettingName()));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            XMockPropGroup g1 = prev.get(oldItemPosition);
            XMockPropGroup g2 = next.get(newItemPosition);

            if(!g1.getSettingName().equalsIgnoreCase(g2.getSettingName()))
                return false;

            for(XMockPropSetting setting : g1.getProperties()) {
                if(!g2.containsProperty(setting))
                    return false;
            }

            return true;
        }
    }

    @Override
    public long getItemId(int position) { return filtered.get(position).hashCode(); }

    @Override
    public int getItemCount() { return filtered.size(); }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.propgroup, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.unWire();
        XMockPropGroup group = filtered.get(position);
        holder.tvSettingName.setText(group.getSettingName());
        holder.tiPropertyValue.setText(group.getValue());

        if(!holder.propArrayAdapter.isEmpty())
            holder.propArrayAdapter.clear();

        for(XMockPropSetting setting : group.getProperties())
            holder.propArrayAdapter.add(setting.getPropertyName());


        holder.cbEnableSetting.setChecked(group.getProperties().get(0).isEnabled());

        holder.updateExpanded();
        holder.wire();
    }
}
