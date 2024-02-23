package eu.faircode.xlua;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import eu.faircode.xlua.api.objects.xmock.prop.MockProp;

public class AdapterPropertiesGroup extends RecyclerView.Adapter<AdapterPropertiesGroup.ViewHolder> {


    public class ViewHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextWatcher {

        ViewHolder(View itemView) {
            super(itemView);



        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(final View view) {

        }

        @Override
        public void onCheckedChanged(final CompoundButton cButton, boolean isChecked) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    }

    @Override
    public long getItemId(int position) { return props.get(position).hashCode(); }

    @Override
    public int getItemCount() { return props.size(); }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.propelement, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

    }
}
