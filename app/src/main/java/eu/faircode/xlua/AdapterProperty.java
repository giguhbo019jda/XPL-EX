package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.faircode.xlua.api.properties.MockPropSetting;
import eu.faircode.xlua.api.props.XMockPropGroup;
import eu.faircode.xlua.api.settings.XMockMappedSetting;
import eu.faircode.xlua.randomizers.GlobalRandoms;
import eu.faircode.xlua.randomizers.IRandomizer;

public class AdapterProperty  extends RecyclerView.Adapter<AdapterProperty.ViewHolder> implements Filterable {
    private static final String TAG = "XLua.AdapterProperty";

    private List<MockPropSetting> properties = new ArrayList<>();
    private List<MockPropSetting> filtered = new ArrayList<>();

    private boolean dataChanged = false;
    private CharSequence query = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public class ViewHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener {

        final View itemView;
        final TextView tvPropName;
        final CheckBox cbHide;
        final CheckBox cbSkip;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            tvPropName = itemView.findViewById(R.id.tvPropPropertyName);
            cbHide = itemView.findViewById(R.id.cbPropHide);
            cbSkip = itemView.findViewById(R.id.cbPropSkip);
        }

        private void unWire() {
            cbHide.setOnCheckedChangeListener(null);
            cbSkip.setOnCheckedChangeListener(null);
        }

        private void wire() {
            cbHide.setOnCheckedChangeListener(this);
            cbSkip.setOnCheckedChangeListener(this);
        }

        @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
        @Override
        public void onCheckedChanged(final CompoundButton cButton, final boolean isChecked) {

        }
    }

    AdapterProperty() { setHasStableIds(true); }

    void set(List<MockPropSetting> properties) {
        this.dataChanged = true;
        this.properties.clear();
        this.properties.addAll(properties);

        if(DebugUtil.isDebug())
            Log.i(TAG, "Internal Count=" + this.properties.size());

        getFilter().filter(query);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            private boolean expanded1 = false;

            @Override
            protected FilterResults performFiltering(CharSequence query) {
                AdapterProperty.this.query = query;
                List<MockPropSetting> visible = new ArrayList<>(properties);
                List<MockPropSetting> results = new ArrayList<>();

                if (TextUtils.isEmpty(query))
                    results.addAll(visible);
                else {
                    String q = query.toString().toLowerCase().trim();
                    for(MockPropSetting prop : visible) {
                        if(prop.getName().toLowerCase().contains(q))
                            results.add(prop);
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
                final List<MockPropSetting> props = (result.values == null ? new ArrayList<MockPropSetting>() : (List<MockPropSetting>) result.values);
                Log.i(TAG, "Filtered props size=" + props.size());

                if(dataChanged) {
                    dataChanged = false;
                    filtered = props;
                    notifyDataSetChanged();
                }else {
                    DiffUtil.DiffResult diff =
                            DiffUtil.calculateDiff(new AppDiffCallback(expanded1, filtered, props));
                    filtered = props;
                    diff.dispatchUpdatesTo(AdapterProperty.this);
                }
            }
        };
    }

    private static class AppDiffCallback extends DiffUtil.Callback {
        private final boolean refresh;
        private final List<MockPropSetting> prev;
        private final List<MockPropSetting> next;

        AppDiffCallback(boolean refresh, List<MockPropSetting> prev, List<MockPropSetting> next) {
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
            MockPropSetting s1 = prev.get(oldItemPosition);
            MockPropSetting s2 = next.get(newItemPosition);
            return (!refresh && s1.getName().equalsIgnoreCase(s2.getName()));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            MockPropSetting s1 = prev.get(oldItemPosition);
            MockPropSetting s2 = next.get(newItemPosition);

            return s1.getName().equalsIgnoreCase(s2.getName());
        }
    }

    @Override
    public long getItemId(int position) { return filtered.get(position).hashCode(); }

    @Override
    public int getItemCount() { return filtered.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.propelement, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.unWire();
        MockPropSetting property = filtered.get(position);

        holder.tvPropName.setText(property.getName());
        holder.cbSkip.setChecked(property.isSkip());
        holder.cbHide.setChecked(property.isHide());

        holder.wire();
    }
}
