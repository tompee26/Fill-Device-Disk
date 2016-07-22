package com.tompee.utilities.filldevicespace.controller;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.Vector;

/**
 * PauseableHandler
 *
 * Message Handler class that supports buffering up of messages when the activity is paused
 * (i.e. in the background)
 *
 * Based on: http://stackoverflow.com/questions/8040280/how-to-handle-handler-messages-when-activity-fragment-is-paused
 */
public class PauseableHandler extends Handler {

    // Message Queue Buffer
    private final Vector<Message> mMessageQueueBuffer = new Vector<>();

    // Flag indicating the paused state
    private boolean mPaused = false;

    // Callback reference
    private final WeakReference<PauseableHandlerCallback> mCallBack;

    /**
     * Creates a PausableHandler instance
     * @param callback Listener for PauseableHandler events
     */
    public PauseableHandler(PauseableHandlerCallback callback) {
        mCallBack = new WeakReference<>(callback);
    }

    /**
     * Resumes the handler. Enables processing of messages.
     * Stored messages will be executed.
     */
    final public void resume() {
        mPaused = false;

        while (mMessageQueueBuffer.size() > 0) {
            final Message msg = mMessageQueueBuffer.elementAt(0);
            mMessageQueueBuffer.removeElementAt(0);
            sendMessage(msg);
        }
    }

    /**
     * Pauses the handler. Messages processed during pause will be stored.
     */
    final public void pause() {
        mPaused = true;
    }

    @Override
    final public void handleMessage(Message msg) {
        if (mCallBack.get() != null) {
            if (mPaused) {
                if (mCallBack.get().storeMessage(msg)) {
                    Message msgCopy = new Message();
                    msgCopy.copyFrom(msg);
                    mMessageQueueBuffer.add(msgCopy);
                }
            } else {
                mCallBack.get().processMessage(msg);
            }
        }
    }

    public interface PauseableHandlerCallback {
        /**
         * @param message The message which optional can be handled
         * Notification that the message is about to be stored as the activity is paused. If not handled the message will be
         * saved and replayed when the activity resumes.
         * @return true if the message is to be stored, else false the message will be discarded
         */
        boolean storeMessage(Message message);

        /**
         * @param message The message to be handled
         * Notification message to be processed. This will either be directly from handleMessage or played back from a saved
         * message when the activity was paused.
         */
        void processMessage(Message message);
    }
}
