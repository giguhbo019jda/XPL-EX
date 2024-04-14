package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.properties.MockPropPacket;
import eu.faircode.xlua.api.properties.MockPropSetting;

import eu.faircode.xlua.api.xmock.XMockCall;
import eu.faircode.xlua.logger.XLog;
import eu.faircode.xlua.ui.dialogs.PropertyDeleteDialog;

public class AdapterProperty  extends RecyclerView.Adapter<AdapterProperty.ViewHolder> implements Filterable {
    private final List<MockPropSetting> properties = new ArrayList<>();
    private List<MockPropSetting> filtered = new ArrayList<>();

    private boolean dataChanged = false;
    private CharSequence query = null;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Object lock = new Object();

    private FragmentManager fragmentManager;
    private AppGeneric application;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener, View.OnLongClickListener, View.OnClickListener {

        final View itemView;
        final TextView tvPropName;
        final CheckBox cbHide, cbSkip, cbForce;
        final ImageView ivDelete;
        //final ConstraintLayout constraintLayout;
        //final CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvPropName = itemView.findViewById(R.id.tvPropPropertyName);
            cbHide = itemView.findViewById(R.id.cbPropHide);
            cbSkip = itemView.findViewById(R.id.cbPropSkip);
            cbForce = itemView.findViewById(R.id.cbPropForce);
            ivDelete = itemView.findViewById(R.id.ivBtPropSettingDelete);
        }

        private void unWire() {
            cbHide.setOnCheckedChangeListener(null);
            cbHide.setOnLongClickListener(null);
            cbSkip.setOnCheckedChangeListener(null);
            cbSkip.setOnLongClickListener(null);
            cbForce.setOnCheckedChangeListener(null);
            cbForce.setOnLongClickListener(null);
            ivDelete.setOnClickListener(null);
        }

        private void wire() {
            cbHide.setOnCheckedChangeListener(this);
            cbHide.setOnLongClickListener(this);
            cbSkip.setOnCheckedChangeListener(this);
            cbSkip.setOnLongClickListener(this);
            cbForce.setOnCheckedChangeListener(this);
            cbForce.setOnLongClickListener(this);
            ivDelete.setOnClickListener(this);
        }

        @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
        @Override
        public void onCheckedChanged(final CompoundButton cButton, final boolean isChecked) {
            int code = cButton.getId();
            XLog.i("onCheckedChanged: code=" + code + " isChecked=" + isChecked);
            try {
                final MockPropSetting setting = filtered.get(getAdapterPosition());
                int valueNeeded = 0;
                if(isChecked) {
                    switch (code) {
                        case R.id.cbPropSkip: valueNeeded = MockPropPacket.PROP_SKIP; break;
                        case R.id.cbPropHide: valueNeeded = MockPropPacket.PROP_HIDE; break;
                        case R.id.cbPropForce: valueNeeded = MockPropPacket.PROP_FORCE; break;
                        default: valueNeeded = MockPropPacket.PROP_NULL; break;
                    }
                }

                final int packetCode = isChecked && valueNeeded != MockPropPacket.PROP_NULL ? MockPropPacket.CODE_INSERT_UPDATE_PROP_SETTING : MockPropPacket.CODE_DELETE_PROP_SETTING;
                final MockPropPacket packet =
                        MockPropPacket.create(application, setting.getName(), setting.getSettingName(), valueNeeded, packetCode);

                final Context context = cButton.getContext();
                XLog.i("Packed created: Property packet=" + packet);
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            final XResult ret = XMockCall.putMockProp(context, packet);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void run() {
                                    if(ret.succeeded())
                                        setting.setValue(packet.getValue());

                                    Toast.makeText(context, ret.getResultMessage(), Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }catch (Exception e) { XLog.e("onCheckedChanged Failed: code=" + code + " isChecked" + isChecked, e, true); }
            notifyDataSetChanged();
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onLongClick(View v) {
            int code = v.getId();
            XLog.i("onLongClick: code=" + code);
            try {
                switch (code) {
                    case R.id.cbPropSkip:
                        Snackbar.make(v, R.string.check_prop_skip_hint, Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.cbPropHide:
                        Snackbar.make(v, R.string.check_prop_hide_hint, Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.cbPropForce:
                        Snackbar.make(v, R.string.check_prop_force_hint, Snackbar.LENGTH_LONG).show();
                        break;
                }
            }catch (Exception e) { XLog.e("onLongClick Failed: code=" + code, e, true); }
            return true;
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            int code = v.getId();
            XLog.i("onClick: code=" + code);
            try {
                MockPropSetting setting = filtered.get(getAdapterPosition());
                switch (code) {
                    case R.id.ivBtPropSettingDelete:
                        PropertyDeleteDialog setDialog = new PropertyDeleteDialog();
                        assert fragmentManager != null;
                        setDialog.addApplication(application);
                        setDialog.addSetting(setting);
                        setDialog.show(fragmentManager, "Delete Property");
                        break;
                }
            }catch (Exception e) { XLog.e("Failed to Invoke onClick: code=" + code, e, true);  }
        }
    }

    AdapterProperty() { setHasStableIds(true); }
    AdapterProperty(FragmentManager manager, AppGeneric application) { this(); this.fragmentManager = manager; this.application = application; }

    void set(List<MockPropSetting> properties) {
        this.dataChanged = true;
        this.properties.clear();
        this.properties.addAll(properties);
        XLog.i("Properties Settings Count=" + properties.size());
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.propelement, parent, false)); }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.unWire();
        MockPropSetting property = filtered.get(position);
        holder.tvPropName.setText(property.getName());
        holder.cbSkip.setChecked(property.isSkip());
        holder.cbHide.setChecked(property.isHide());
        holder.cbForce.setChecked(property.isForce());
        holder.wire();
    }
}
