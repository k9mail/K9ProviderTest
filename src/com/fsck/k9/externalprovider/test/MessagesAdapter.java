package com.fsck.k9.externalprovider.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fsck.k9.externalprovider.test.K9MailBridge.EmailMessage;

public class MessagesAdapter extends BaseAdapter {
    
    List<EmailMessage> messages;
    OnClickListener listener;
    final LayoutInflater inflater;
    
    SimpleDateFormat dateFormat;
    
    public MessagesAdapter(Context context, OnClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat();
    }
    
    public void setMessages(List<EmailMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }
    
    public int getCount() {
        if(messages != null)
            return messages.size();
        return 0;
    }

    public Object getItem(int position) {
        if(position < messages.size()) {
            return messages.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return messages.get(position).id;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.email_list_item, null);
            holder = new ViewHolder();
            holder.container = convertView.findViewById(R.id.email_item);
            holder.color = convertView.findViewById(R.id.email_color);
            holder.sender = (TextView) convertView.findViewById(R.id.email_item_sender);
            holder.subject = (TextView) convertView.findViewById(R.id.email_item_subject);
            holder.preview = (TextView) convertView.findViewById(R.id.email_item_summary);
            holder.date = (TextView) convertView.findViewById(R.id.email_item_date);
            holder.attachment = (ImageView) convertView.findViewById(R.id.email_item_attchment);
            holder.star = (ImageView) convertView.findViewById(R.id.email_item_star);
            holder.unread = (ImageView) convertView.findViewById(R.id.email_item_unread);
            convertView.setTag(holder);
            convertView.setOnClickListener(listener);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EmailMessage message = (EmailMessage) getItem(position);
        
        holder.color.setBackgroundColor(message.color);
        holder.sender.setText(message.fromName);
        holder.subject.setText(message.subject);
        holder.preview.setText(message.summary);
        holder.date.setText(dateFormat.format(new Date(message.timestamp)));
        holder.attachment.setVisibility((message.hasAttachment)?View.VISIBLE:View.GONE);
        holder.star.setVisibility((message.isFavorite)?View.VISIBLE:View.GONE);
        holder.unread.setVisibility((message.isRead)?View.GONE:View.VISIBLE);
        
        holder.position = position;
        
        return convertView;
    }
    
    static class ViewHolder {
        View container;
        View color;
        TextView sender;
        TextView subject;
        TextView preview;
        TextView date;
        ImageView attachment;
        ImageView star;
        ImageView unread;
        
        int position;
    }

}