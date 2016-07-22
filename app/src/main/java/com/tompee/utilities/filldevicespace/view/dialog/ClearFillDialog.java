package com.tompee.utilities.filldevicespace.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.PauseableHandler;
import com.tompee.utilities.filldevicespace.controller.task.ClearFillTask;

public class ClearFillDialog extends BaseDialog implements ClearFillTask.ClearFillListener,
        PauseableHandler.PauseableHandlerCallback {
    private static final String TAG_CLEARED_SPACE = "cleared_space";
    private static final int FINISH_MESSAGE = 1;
    private PauseableHandler mPauseableHandler;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPauseableHandler = new PauseableHandler(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.ids_lbl_clear));
        builder.setMessage(getString(R.string.ids_lbl_clearing_space));
        builder.setPositiveButton(R.string.ids_lbl_ok, this);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        ClearFillTask task = new ClearFillTask(getContext(), this);
        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPauseableHandler.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPauseableHandler.pause();
    }

    @Override
    public void onFinish(long clearedSpace) {
        Message newMessage = Message.obtain(mPauseableHandler, FINISH_MESSAGE);
        Bundle bundle = new Bundle();
        bundle.putLong(TAG_CLEARED_SPACE, clearedSpace);
        newMessage.setData(bundle);
        mPauseableHandler.sendMessage(newMessage);
    }

    @Override
    public boolean storeMessage(Message message) {
        return message.what == FINISH_MESSAGE;
    }

    @Override
    public void processMessage(Message message) {
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.setMessage(String.format(getString(R.string.ids_lbl_cleared_space),
                Formatter.formatShortFileSize(getContext(),
                        message.getData().getLong(TAG_CLEARED_SPACE))));
        dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
    }
}
