package eu.faircode.xlua;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SettingDialog extends AppCompatDialogFragment {
    private EditText edSettingName;
    private EditText edSettingDescription;
    private EditText edSettingValue;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settingadd, null);

        builder.setView(view)
                .setTitle("Setting")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        //View inflate = LayoutInflater.from(this).inflate(R.layout.define, (ViewGroup) null);
        edSettingName = view.findViewById(R.id.etSettingName);
        edSettingDescription = view.findViewById(R.id.etSettingDescription);
        edSettingValue = view.findViewById(R.id.etSettingValue);

        return builder.create();
    }
}
