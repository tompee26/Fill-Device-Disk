package com.tompee.utilities.filldevicespace.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.util.Log;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.task.ClearFillTask;

public class ClearFillDialog extends BaseDialog implements DialogInterface.OnClickListener,
        ClearFillTask.ClearFillListener {
    private static final String TAG = "ClearFillDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.ids_lbl_clear));
        builder.setMessage(getString(R.string.ids_message_clearing_space));
        builder.setPositiveButton(R.string.ids_lbl_ok, this);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog)getDialog()).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        ClearFillTask task = new ClearFillTask(getContext(), this);
        task.execute();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "" + which);
    }

    @Override
    public void onFinish(long clearedSpace) {
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.setMessage(String.format(getString(R.string.ids_message_cleared_space),
                Formatter.formatShortFileSize(getContext(), clearedSpace)));
        dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
    }
}
