package com.fsck.k9.externalprovider.test;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

class K9MailBridge {    
    public class EmailMessage {
        public long id;
        public String fromName;
        public String fromAddress;
        public String subject;
        public String account;
        public String summary;
        public long timestamp;
        public boolean isRead;
        public boolean isFavorite;
        public boolean hasAttachment;
        public int color;
        public String messageUri;
        public String messageDeleteUri;
    }
    
    public static interface MessageColumns extends BaseColumns {
        /**
         * The number of milliseconds since Jan. 1, 1970, midnight GMT.
         *
         * <P>Type: INTEGER (long)</P>
         */
        String SEND_DATE = "date";

        /**
         * <P>Type: TEXT</P>
         */
        String SENDER = "sender";

        /**
         * <P>Type: TEXT</P>
         */
        String SENDER_ADDRESS = "senderAddress";

        /**
         * <P>Type: TEXT</P>
         */
        String SUBJECT = "subject";

        /**
         * <P>Type: TEXT</P>
         */
        String PREVIEW = "preview";

        /**
         * <P>Type: BOOLEAN</P>
         */
        String UNREAD = "unread";

        /**
         * <P>Type: TEXT</P>
         */
        String ACCOUNT = "account";

        /**
         * <P>Type: INTEGER</P>
         */
        String ACCOUNT_NUMBER = "accountNumber";

        /**
         * <P>Type: BOOLEAN</P>
         */
        String HAS_ATTACHMENTS = "hasAttachments";

        /**
         * <P>Type: BOOLEAN</P>
         */
        String HAS_STAR = "hasStar";

        /**
         * <P>Type: INTEGER</P>
         */
        String ACCOUNT_COLOR = "accountColor";

        String URI = "uri";
        String DELETE_URI = "delUri";

        /**
         * @deprecated the field value is misnamed/misleading - present for compatibility purpose only. To be removed.
         */
        @Deprecated
        String INCREMENT = "id";
    }

    private static final String[] DEFAULT_MESSAGE_PROJECTION = new String[] {
        MessageColumns._ID,
        MessageColumns.SEND_DATE,
        MessageColumns.SENDER,
        MessageColumns.SUBJECT,
        MessageColumns.PREVIEW,
        MessageColumns.ACCOUNT,
        MessageColumns.URI,
        MessageColumns.DELETE_URI,
        MessageColumns.SENDER_ADDRESS,
        MessageColumns.UNREAD,
        MessageColumns.HAS_STAR,
        MessageColumns.HAS_ATTACHMENTS,
        MessageColumns.ACCOUNT_COLOR,
    };

    protected Uri getMessagesUri() {
        return Uri.parse("content://com.fsck.k9.messageprovider/inbox_messages");
    }

    public List<EmailMessage> getMessages(Context context) {    
        // Query k-9 provider
        Cursor c = context.getContentResolver().query(getMessagesUri(),
                DEFAULT_MESSAGE_PROJECTION,
                null, null, null);
        
        int maxItems = 25;
        
        // Build messages list
        List<EmailMessage> messages = null;
        if (c != null){
            messages = new ArrayList<EmailMessage>(maxItems);
            
            /* Get columns indexes */
            int columnId = c.getColumnIndexOrThrow(MessageColumns._ID);
            int columnTime = c.getColumnIndexOrThrow(MessageColumns.SEND_DATE);
            int columnSender = c.getColumnIndexOrThrow(MessageColumns.SENDER);
            int columnSubject = c.getColumnIndexOrThrow(MessageColumns.SUBJECT);
            int columnPreview = c.getColumnIndexOrThrow(MessageColumns.PREVIEW);
            int columnAccount = c.getColumnIndexOrThrow(MessageColumns.ACCOUNT);
            int columnURI = c.getColumnIndexOrThrow(MessageColumns.URI);
            int columnDeleteURI = c.getColumnIndexOrThrow(MessageColumns.DELETE_URI);
            int columnSenderAddress = c.getColumnIndexOrThrow(MessageColumns.SENDER_ADDRESS);
            int columnUnread = c.getColumnIndexOrThrow(MessageColumns.UNREAD);
            int columnHasStar = c.getColumnIndex(MessageColumns.HAS_STAR);    // new column
            int columnHasAttachments = c.getColumnIndex(MessageColumns.HAS_ATTACHMENTS); // new column
            int columnAccountColor = c.getColumnIndex(MessageColumns.ACCOUNT_COLOR); // new column
            
            while (c.moveToNext() && c.getPosition() < maxItems) {
                EmailMessage message = new EmailMessage();
                                
                message.id = c.getLong(columnId);
                message.timestamp = c.getLong(columnTime);
                message.fromName = c.getString(columnSender);
                message.subject = c.getString(columnSubject);
                message.summary = c.getString(columnPreview);
                message.account = c.getString(columnAccount);
                message.messageUri = c.getString(columnURI);
                message.messageDeleteUri = c.getString(columnDeleteURI);
                message.fromAddress = c.getString(columnSenderAddress);
                message.isRead = Boolean.FALSE.equals(Boolean.parseBoolean(c.getString(columnUnread)));
                
                if(columnHasStar >= 0) {
                    message.isFavorite = Boolean.parseBoolean(c.getString(columnHasStar));                    
                }
                
                if(columnHasAttachments >= 0) {
                    message.hasAttachment = Boolean.parseBoolean(c.getString(columnHasAttachments));                    
                }
                
                if(columnAccountColor >= 0) {
                    message.color = c.getInt(columnAccountColor);                    
                }                
                
                messages.add(message);
            }
            
            c.close();
        }
        
        return messages;
    }

    /**
     * Open k-9 application
     * @param context
     */
    public void openMainApplication(Context context) {
        context.startActivity(context.getPackageManager().getLaunchIntentForPackage("com.fsck.k9")
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    /**
     * Open specific message
     * @param context
     * @param message
     */
    public void openMessage(Context context, EmailMessage message) {
        Intent action = new Intent(Intent.ACTION_VIEW, Uri.parse(message.messageUri));
        action.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(action);
    }
}