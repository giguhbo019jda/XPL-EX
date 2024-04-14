package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.faircode.xlua.api.hook.XLuaHook;
import eu.faircode.xlua.api.properties.MockPropGroupHolder;
import eu.faircode.xlua.logger.XLog;
import eu.faircode.xlua.ui.HookGroup;
import eu.faircode.xlua.utilities.StringUtil;
import eu.faircode.xlua.utilities.UiUtil;
import eu.faircode.xlua.utilities.ViewUtil;

public class AdapterHook extends RecyclerView.Adapter<AdapterHook.ViewHolder> {
    private static final String TAG = "XLua.AdapterGroupHooks";

    private List<XLuaHook> hooks = new ArrayList<>();

    private HookGroup group;

    private final HashMap<String, Boolean> expanded = new HashMap<>();
    private CharSequence query = null;
    private String query_lower = null;
    private AppGeneric application;
    private FragmentManager fragmentManager;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, View.OnLongClickListener {

        final View view;
        final TextView tvHookName;
        final CheckBox cbEnableHook;
        final RecyclerView rvHookSettings;
        final AdapterHookSettings adapterSettings;

        ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.tvHookName = view.findViewById(R.id.tvHookName);
            this.rvHookSettings = view.findViewById(R.id.rvHookSettings);
            this.cbEnableHook = view.findViewById(R.id.cbHook);

            adapterSettings = new AdapterHookSettings(fragmentManager, application);
            UiUtil.initRv(itemView.getContext(), rvHookSettings, adapterSettings);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void unWire() {
            this.view.setOnClickListener(null);
            this.tvHookName.setOnClickListener(null);
            this.cbEnableHook.setOnCheckedChangeListener(null);
            this.view.setOnLongClickListener(null);
            this.tvHookName.setOnLongClickListener(null);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void wire() {
            this.view.setOnClickListener(this);
            this.tvHookName.setOnClickListener(this);
            this.cbEnableHook.setOnCheckedChangeListener(this);
            this.view.setOnLongClickListener(this);
            this.tvHookName.setOnLongClickListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(final View view) {
            int code = view.getId();
            Log.i(TAG, "onClick=" + code);
            try {
                final XLuaHook hook = hooks.get(getAdapterPosition());
                for(Map.Entry<String, Boolean> e : expanded.entrySet()) {
                    XLog.w("(3) EXPANDEE=" + e.getKey() + " expnd=" + (e.getValue()));
                }
                switch (code) {
                    case R.id.itemViewHooks:
                    case R.id.tvHookName:
                        XLog.e("BEEN INVOKED FROM AdapaterHook: " + code + " is empty=" + hook.getManagedSettings().isEmpty() + " pos=" + getAdapterPosition() + " hook=" + hook.getId(), new Throwable(), true);
                        ViewUtil.internalUpdateExpanded(expanded, hook.getId());
                        updateExpanded();

                        /*if(!hook.getManagedSettings().isEmpty()) {
                            ViewUtil.internalUpdateExpanded(expanded, hook.getId());
                            for(Map.Entry<String, Boolean> e : expanded.entrySet()) {
                                XLog.w("(4) EXPANDEE=" + e.getKey() + " expnd=" + (e.getValue()));
                            }
                            updateExpanded();
                            for(Map.Entry<String, Boolean> e : expanded.entrySet()) {
                                XLog.w("(5) EXPANDEE=" + e.getKey() + " expnd=" + (e.getValue()));
                            }
                        }else {
                            Snackbar.make(view, R.string.error_no_settings_hook, Snackbar.LENGTH_LONG).show();
                        }*/
                        break;
                }
            }catch (Exception e) { XLog.e("onClick Failed: code=" + code, e); }
        }

        @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
        @Override
        public void onCheckedChanged(final CompoundButton cButton, final boolean isChecked) {

        }

        void updateExpanded() {
            XLuaHook hook = hooks.get(getAdapterPosition());
            String name = hook.getId();
            boolean isExpanded = expanded.containsKey(name) && Boolean.TRUE.equals(expanded.get(name));

            XLog.e("FROM HOOK: " + " isExpanded=" + isExpanded + " name=" + name + " contains key=" + (expanded.containsKey(name)), new Throwable(), true);

            ViewUtil.setViewsVisibility(null, isExpanded, rvHookSettings);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onLongClick(View v) {
            int code = view.getId();
            Log.i(TAG, "onLongClick=" + code);
            try {
                final XLuaHook hook = hooks.get(getAdapterPosition());
                switch (code) {
                    case R.id.itemViewHooks:
                    case R.id.tvHookName:
                        if(StringUtil.isValidAndNotWhitespaces(hook.getDescription())) Toast.makeText(view.getContext(), hook.getDescription(), Toast.LENGTH_SHORT).show();
                        else Toast.makeText(view.getContext(), R.string.error_no_description_hook, Toast.LENGTH_SHORT).show();
                        return true;
                }
            }catch (Exception e) { XLog.e("onLongClick Failed: code=" + code, e); }
            return false;
        }
    }

    AdapterHook() { setHasStableIds(true); }
    AdapterHook(FragmentManager manager, AppGeneric application) { this(); this.fragmentManager = manager; this.application = application;  }
    public void set(HookGroup group) {
        this.group = group;
        //this.hooks = new ArrayList<>(group.getHooks());


        for(Map.Entry<String, Boolean> e : expanded.entrySet()) {
            XLog.w("(2) EXPANDEE=" + e.getKey() + " expnd=" + (e.getValue()));
        }

        this.hooks.clear();
        this.hooks.addAll(group.getHooks());
    }

    //public void set(LuaHooksGroup group) {
    //    this.group = group;
    //    this.hooks = group.hooks;
    //}
    //public void set(List<XLuaHook> hooks, List<LuaAssignment> assignments) {
        //this.hooks.clear();
        //this.hooks.addAll(hooks);
    //}

    @SuppressLint("NotifyDataSetChanged")
    public void setQuery(CharSequence query) {
        boolean cleanInp = StringUtil.isValidAndNotWhitespaces(query);
        if(!cleanInp && this.query == null) return;
        if(cleanInp) {
            String s = query.toString();
            if(this.query != null && s.equalsIgnoreCase(this.query.toString())) return;
            this.query = query;
            this.query_lower = s.toLowerCase().trim();
            XLog.i("Query Has Changed: " + this.query_lower);
        }else {
            this.query = null;
            this.query_lower = null;
        }

        //notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) { return hooks.get(position).hashCode(); }

    @Override
    public int getItemCount() { return hooks.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hookgrouphook, parent, false)); }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.unWire();
        final XLuaHook hook = hooks.get(position);
        boolean lastWas = false;
        if(expanded.containsKey(hook.getId())) {
            lastWas = true;
            XLog.e("Is Expanded: " + hook.getId(), new Throwable(), true);
        }

        for(Map.Entry<String, Boolean> e : expanded.entrySet()) {
            XLog.w("(1) EXPANDEE=" + e.getKey() + " expnd=" + (e.getValue()));
        }

        holder.tvHookName.setText(hook.getName());
        holder.tvHookName.setSelected(true);
        if((this.query != null && this.query.length() > 0) && (hook.containsQuery(this.query_lower, true, true, true, true))) {
            holder.tvHookName.setTextColor(XUtil.resolveColor(holder.tvHookName.getContext(), R.attr.colorAccent));
        }else holder.tvHookName.setTextColor(XUtil.resolveColor(holder.tvHookName.getContext(), R.attr.colorTextOne));
        holder.adapterSettings.set(hook.getManagedSettings());
        holder.cbEnableHook.setChecked(this.group.containsAssignedHook(hook.getId()));

        if(expanded.containsKey(hook.getId()) || lastWas) {
            XLog.e("Is Expanded v2: " + hook.getId(), new Throwable(), true);
        }

        holder.updateExpanded();
        for(Map.Entry<String, Boolean> e : expanded.entrySet()) {
            XLog.w("(7) EXPANDEE=" + e.getKey() + " expnd=" + (e.getValue()));
        }

        holder.wire();
    }
}
