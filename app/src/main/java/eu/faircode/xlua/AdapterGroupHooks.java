package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.app.XLuaApp;
import eu.faircode.xlua.api.hook.LuaHooksGroup;
import eu.faircode.xlua.api.hook.XLuaHook;
import eu.faircode.xlua.api.hook.assignment.LuaAssignment;
import eu.faircode.xlua.api.settings.LuaSettingExtended;
import eu.faircode.xlua.api.xmock.XMockQuery;
import eu.faircode.xlua.logger.XLog;
import eu.faircode.xlua.ui.HookGroup;
import eu.faircode.xlua.ui.IHookTransaction;
import eu.faircode.xlua.utilities.SettingUtil;
import eu.faircode.xlua.utilities.StringUtil;
import eu.faircode.xlua.utilities.UiUtil;
import eu.faircode.xlua.utilities.ViewUtil;

public class AdapterGroupHooks extends RecyclerView.Adapter<AdapterGroupHooks.ViewHolder> implements Filterable {
    private static final String TAG = "XLua.AdapterGroupHooks";

    private AppGeneric application;
    private FragmentManager fragmentManager;

    private List<HookGroup> groups = new ArrayList<>();
    private final HashMap<String, Boolean> expanded = new HashMap<>();

    private List<HookGroup> filtered = new ArrayList<>();
    private boolean dataChanged = false;
    private CharSequence query = null;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, IHookTransaction {

        final View view;
        final TextView tvGroupName, tvHooksCount, tvHooksSelectedCount;
        final CheckBox cbGroup;
        final ImageView ivExpander;

        final RecyclerView rvHooks;
        final AdapterHook adapterHook;

        ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            this.tvGroupName = itemView.findViewById(R.id.tvHookGroupName);
            this.cbGroup = itemView.findViewById(R.id.cbHookGroup);
            this.rvHooks = itemView.findViewById(R.id.rvHookGroup);
            this.ivExpander = itemView.findViewById(R.id.ivExpanderGroup);
            this.tvHooksCount = itemView.findViewById(R.id.tvHookGroupsHooksCount);
            this.tvHooksSelectedCount = itemView.findViewById(R.id.tvHookGroupsHooksCountSelected);

            //init RV
            adapterHook = new AdapterHook(fragmentManager, application);
            UiUtil.initRv(itemView.getContext(), rvHooks, adapterHook);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void unWire() {
            this.ivExpander.setOnClickListener(null);
            this.tvGroupName.setOnClickListener(null);
            this.cbGroup.setOnCheckedChangeListener(null);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void wire() {
            this.ivExpander.setOnClickListener(this);
            this.tvGroupName.setOnClickListener(this);
            this.cbGroup.setOnCheckedChangeListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(final View view) {
            int code = view.getId();
            Log.i(TAG, "onClick=" + code);
            try {
                final HookGroup group = filtered.get(getAdapterPosition());
                final String name = group.name;
                switch (code) {
                    case R.id.itemViewGroupHooks:
                    case R.id.tvHookGroupName:
                    case R.id.ivExpanderGroup:
                        ViewUtil.internalUpdateExpanded(expanded, name);
                        updateExpanded();
                        break;
                }
            }catch (Exception e) { XLog.e("onClick Failed: code=" + code, e, true); }
        }

        @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
        @Override
        public void onCheckedChanged(final CompoundButton cButton, final boolean isChecked) {
            HookGroup group = groups.get(getAdapterPosition());
            group.sendAll(cButton.getContext(), getAdapterPosition(), isChecked, this);
        }

        void updateExpanded() {
            HookGroup group = filtered.get(getAdapterPosition());
            String name = group.name;
            boolean isExpanded = expanded.containsKey(name) && Boolean.TRUE.equals(expanded.get(name));
            ViewUtil.setViewsVisibility(ivExpander, isExpanded, rvHooks);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onGroupFinished(List<LuaAssignment> assignments, int position, boolean assign, XResult result) {
            try {
                if(position < 0) throw new Exception("Invalid Position: " + position);
                notifyItemChanged(position);
            }catch (Exception e) {
                XLog.e("Failed to Init Update for Hooks: position=" + position + " assign=" + assign + " assignments count=" + assignments.size(), e, true);
                notifyDataSetChanged();
            }
        }
    }

    AdapterGroupHooks() { setHasStableIds(true); }
    AdapterGroupHooks(FragmentManager manager, AppGeneric application) {  this(); this.fragmentManager = manager; this.application = application; }

    @Override
    public long getItemId(int position) { return filtered.get(position).hashCode(); }

    @Override
    public int getItemCount() { return filtered.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hookgroup, parent, false)); }

    public void set(List<HookGroup> groups) {
        this.dataChanged = true;
        //this.groups = groups;
        this.groups.clear();
        this.groups.addAll(groups);
        getFilter().filter(query);
        XLog.i("Groups size=" + this.groups.size());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            private boolean expanded1 = false;
            @Override
            protected FilterResults performFiltering(CharSequence query) {
                AdapterGroupHooks.this.query = query;
                List<HookGroup> visible = new ArrayList<>(groups);
                List<HookGroup> results = new ArrayList<>();
                try {
                    if (!StringUtil.isValidAndNotWhitespaces(query)) {
                        results.addAll(visible);
                    }
                    else {
                        String q = query.toString().toLowerCase().trim();
                        for(HookGroup g : visible) {
                            if(g.name.toLowerCase().contains(q) || g.title.toLowerCase().contains(q))
                                results.add(g);
                            else if(g.hasHooks()) {
                                for(XLuaHook h : g.getHooks()) {
                                    if(h.containsQuery(q, true, true, true, false)) {
                                        results.add(g);
                                        break;
                                    }
                                }
                            }else XLog.e("Group holding hooks has NULL or EMPTY array for Hooks ? " + g.name);
                        }
                    }

                    if (results.size() == 1) {
                        String settingName = results.get(0).name;
                        if (!expanded.containsKey(settingName)) {
                            expanded1 = true;
                            expanded.put(settingName, true);
                        }
                    }
                }catch (Exception e) {
                    XLog.e("Filtering settings failed", e);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = results;
                filterResults.count = results.size();
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence query, FilterResults result) {
                try {
                    final List<HookGroup> groups = (result.values == null ? new ArrayList<HookGroup>() : (List<HookGroup>) result.values);
                    Log.i(TAG, "Filtered settings size=" + groups.size());
                    if(dataChanged) {
                        dataChanged = false;
                        filtered = groups;
                        notifyDataSetChanged();
                    }else {
                        DiffUtil.DiffResult diff =
                                DiffUtil.calculateDiff(new AppDiffCallback(expanded1, filtered, groups));
                        filtered = groups;
                        diff.dispatchUpdatesTo(AdapterGroupHooks.this);
                        //notifyDataSetChanged();//here to enforce update to the sub elements
                    }
                }catch (Exception e) {
                    XLog.e("Failed to Publish Results for Adapter Settings", e);
                }
            }
        };
    }

    private static class AppDiffCallback extends DiffUtil.Callback {
        private final boolean refresh;
        private final List<HookGroup> prev;
        private final List<HookGroup> next;
        AppDiffCallback(boolean refresh, List<HookGroup> prev, List<HookGroup> next) {
            this.refresh = refresh;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public int getOldListSize() { return prev.size(); }

        @Override
        public int getNewListSize() { return next.size(); }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            HookGroup g1 = prev.get(oldItemPosition);
            HookGroup g2 = next.get(newItemPosition);
            return (!refresh && g1.name.equalsIgnoreCase(g2.name));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            HookGroup g1 = prev.get(oldItemPosition);
            HookGroup g2 = next.get(newItemPosition);
            return g1.name.equalsIgnoreCase(g2.name);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.unWire();
        HookGroup group = filtered.get(position);
        holder.tvGroupName.setText(SettingUtil.cleanSettingName(group.title));
        holder.adapterHook.set(group);
        holder.adapterHook.setQuery(this.query);
        holder.tvHooksCount.setText(new StringBuilder().append("[").append(group.hooksSize()).append("]"));

        holder.tvHooksSelectedCount.setText(new StringBuilder().append(group.getAssigned()));
        holder.tvHooksSelectedCount.setVisibility(group.hasAssigned() ? View.VISIBLE : View.GONE);

        Resources resources = holder.itemView.getContext().getResources();
        holder.cbGroup.setButtonTintList(ColorStateList.valueOf(resources.getColor(
                group.allAssigned() ? R.color.colorAccent : android.R.color.darker_gray, null)));

        holder.cbGroup.setChecked(group.hasAssigned());
        XLog.i("group=" + group.name + " assignments=" + group.getAssigned());
        holder.updateExpanded();
        holder.wire();
    }
}
