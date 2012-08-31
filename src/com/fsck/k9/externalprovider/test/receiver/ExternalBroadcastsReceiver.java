package com.fsck.k9.externalprovider.test.receiver;

import com.fsck.k9.externalprovider.test.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * BroadcastReceiver to catch k-9 broadcasts.
 * 
 * This receiver is manifest registered and will log any k-9
 * broadcast at any time for testing purposes.
 * Note that MainActivity must run at least once after installation for
 * the receiver to be registered by the Android system.
 * 
 * A second instance of this receiver exists in {@link MainActivity}
 * for sample usage in activities.
 */
public class ExternalBroadcastsReceiver extends BroadcastReceiver {
    private static final String TAG = "K9ProviderTest.ExternalBroadcastsReceiver";
    
    public static final String K9_ACTION_EMAIL_RECEIVED = "com.fsck.k9.intent.action.EMAIL_RECEIVED";
    public static final String K9_ACTION_EMAIL_DELETED = "com.fsck.k9.intent.action.EMAIL_DELETED";
    public static final String K9_ACTION_REFRESH_OBSERVER = "com.fsck.k9.intent.action.REFRESH_OBSERVER";
    public static final String K9_EXTRA_ACCOUNT = "com.fsck.k9.intent.extra.ACCOUNT";
    public static final String K9_EXTRA_FOLDER = "com.fsck.k9.intent.extra.FOLDER";
    
    @Override
    public final void onReceive(Context context, Intent intent) {
        
        String accountName = intent.getStringExtra("com.fsck.k9.intent.extra.ACCOUNT");
        if(accountName == null)
            accountName = "";

        String folderName = intent.getStringExtra("com.fsck.k9.intent.extra.FOLDER");
        if(folderName == null)
            folderName = "";

        
        if(TextUtils.equals(intent.getAction(), K9_ACTION_EMAIL_RECEIVED)) {
            onEmailReceived(accountName, folderName);
        } else if(TextUtils.equals(intent.getAction(), K9_ACTION_EMAIL_DELETED)) {
            onEmailDeleted(accountName, folderName);
        } else if(TextUtils.equals(intent.getAction(), K9_ACTION_REFRESH_OBSERVER)) {
            onRefreshObserver(accountName, folderName);
        } else {
            Log.d(TAG, "Received unknown " + intent.getAction() + ", accountName=" + accountName + ", folderName=" + folderName);
        }
        
    }

    /**
     * Called on incoming email
     * @param accountName
     * @param folderName
     */
    protected void onEmailReceived(String accountName, String folderName) {
        Log.d(TAG, "Received " + K9_ACTION_EMAIL_RECEIVED + ", accountName=" + accountName + ", folderName=" + folderName);
    }

    /**
     * Called on email deletion
     * @param accountName
     * @param folderName
     */
    protected void onEmailDeleted(String accountName, String folderName) {
        Log.d(TAG, "Received " + K9_ACTION_EMAIL_DELETED + ", accountName=" + accountName + ", folderName=" + folderName);
    }

    /**
     * Called on folder status change
     * @param accountName
     * @param folderName
     */
    protected void onRefreshObserver(String accountName, String folderName) {
        Log.d(TAG, "Received " + K9_ACTION_REFRESH_OBSERVER + ", accountName=" + accountName + ", folderName=" + folderName);
    }
    
}
