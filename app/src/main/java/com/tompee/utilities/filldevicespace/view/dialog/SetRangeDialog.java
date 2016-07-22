package com.tompee.utilities.filldevicespace.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.R;

public class SetRangeDialog extends BaseDialog implements TextWatcher {
    private static final int MAX_VISIBLE_RANGE_VALUE = 50;

    private Button mPositiveButton;
    private EditText mEditText;
    private OnSetRangeListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_set_range, null);
        TextView guide = (TextView) view.findViewById(R.id.guide);
        guide.setText(String.format(getString(R.string.ids_lbl_value),
                MAX_VISIBLE_RANGE_VALUE));
        mEditText = (EditText) view.findViewById(R.id.value);
        mEditText.addTextChangedListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.ids_title_max_range));
        builder.setView(view);
        builder.setPositiveButton(R.string.ids_lbl_ok, this);
        builder.setNegativeButton(R.string.ids_lbl_cancel, this);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnSetRangeListener) activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        mPositiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        mPositiveButton.setEnabled(false);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onValueChanged(Integer.parseInt(mEditText.getText().toString()));
                dismiss();
            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            int value = Integer.parseInt(mEditText.getText().toString());
            if (value != 0 && value <= MAX_VISIBLE_RANGE_VALUE) {
                mPositiveButton.setEnabled(true);
            } else {
                mPositiveButton.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            mPositiveButton.setEnabled(false);
        }
    }

    public interface OnSetRangeListener {
        void onValueChanged(int value);
    }
}
