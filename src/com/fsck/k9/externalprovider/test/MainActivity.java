package com.fsck.k9.externalprovider.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fsck.k9.externalprovider.test.K9MailBridge.EmailMessage;
import com.fsck.k9.externalprovider.test.MessagesAdapter.ViewHolder;
import com.fsck.k9.externalprovider.test.receiver.ExternalBroadcastsReceiver;

public class MainActivity extends ListActivity implements OnClickListener {
    private static final String TAG = "K9ProviderTest.MainActivity"; 

    private K9MailBridge bridge = new K9MailBridge();
    
    private TextView footerTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main_activity);
        
        View headerView = findViewById(R.id.header);
        headerView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bridge.openMainApplication(MainActivity.this);                
            }
        });

        View footerView = findViewById(R.id.footer);
        footerTextView = (TextView) footerView.findViewById(R.id.footer_text);
        
        /* Set adapter */
        setListAdapter(new MessagesAdapter(this, this));
        
        /* Register local broadcast receiver */
        registerEmailReceiver();
        
        /* Manual refresh */
        onRefreshContent("Manual", "", "");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        /* Unregister local broadcast receiver */
        unregisterEmailReceiver();
    }
    
    /**
     * Unregister local broadcast receiver
     * 
     * Should be called from onPause on most applications
     * Currently called from onDestroy for better testing purposes
     */
    private void unregisterEmailReceiver() {
        unregisterReceiver(receiver);
    }

    /**
     * Register local broadcast receiver
     * 
     * Should be called from onResume on most applications
     * Currently called from onCreate for better testing purposes
     */
    private void registerEmailReceiver() {
        IntentFilter emailReceivedFilter = new IntentFilter(ExternalBroadcastsReceiver.K9_ACTION_EMAIL_RECEIVED);
        emailReceivedFilter.addDataScheme("email");
        registerReceiver(receiver, emailReceivedFilter);
        
        IntentFilter emailDeletedFilter = new IntentFilter(ExternalBroadcastsReceiver.K9_ACTION_EMAIL_DELETED);
        emailDeletedFilter.addDataScheme("email");
        registerReceiver(receiver, emailDeletedFilter);
        
        IntentFilter refreshObserverFilter = new IntentFilter(ExternalBroadcastsReceiver.K9_ACTION_REFRESH_OBSERVER);
        registerReceiver(receiver, refreshObserverFilter);
    }

    /**
     * Refresh list content
     * @param source
     * @param accountName
     * @param folderName
     */
    public void onRefreshContent(String source, String accountName, String folderName) {
        // TODO: we currently ignore anything other than INBOX
        if(!TextUtils.isEmpty(folderName) && !folderName.equals("INBOX")) {
            return;
        }
        
        /* Build message */
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = format.format(new Date());
        String message = time + ", Source = " + source;
        if(!TextUtils.isEmpty(accountName)) {
            message += ", Account = " + accountName;
        }
        if(!TextUtils.isEmpty(folderName)) {
            message += ", Folder = " + folderName;
        }

        Log.d(TAG, message);
        
        // Update adapter
        new RefreshContentTask(message).execute();
    }
    
    class RefreshContentTask extends AsyncTask<Void, Void, List<EmailMessage>> {
        String msg;
        
        public RefreshContentTask(String msg) {
            this.msg = msg;
        }
        
        @Override
        protected List<EmailMessage> doInBackground(Void... params) {
            return bridge.getMessages(getBaseContext());
        }
        
        @Override
        protected void onPostExecute(List<EmailMessage> result) {
            super.onPostExecute(result);
            footerTextView.setText(msg);
            ((MessagesAdapter) getListAdapter()).setMessages(result);
        }
        
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_inbox:
            onRefreshContent("Manual", "", "");
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        if(holder != null) {
            EmailMessage msg = (EmailMessage) getListAdapter().getItem(holder.position);
            if(msg != null) {
                bridge.openMessage(this, msg);                
            }
        }
    }

    BroadcastReceiver receiver = new ExternalBroadcastsReceiver() {
        protected void onEmailReceived(String accountName, String folderName) {
            onRefreshContent("onEmailReceived", accountName, folderName);
        };
        protected void onEmailDeleted(String accountName, String folderName) {
            onRefreshContent("onEmailDeleted", accountName, folderName);
        };
        protected void onRefreshObserver(String accountName, String folderName) {
            onRefreshContent("onRefreshObserver", accountName, folderName);
        };
    };

}
