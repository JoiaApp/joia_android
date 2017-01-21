package com.joiaapp.joia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnell on 11/30/2016.
 */

public class MessageReviewArrayAdapter extends ArrayAdapter<Message> {
    private final Context context;
    private ViewHolder viewHolder;

    public MessageReviewArrayAdapter(Context context, List<Message> messages) {
        super(context, -1,  new ArrayList<>(messages));
        this.context = context;
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_review_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvMessageNumber = (TextView) convertView.findViewById(R.id.tvMessageNumber);
            viewHolder.tvMessageText = (TextView) convertView.findViewById(R.id.tvMessageText);
            viewHolder.llMessageMentions = (LinearLayout) convertView.findViewById(R.id.llMessageMentions);
            viewHolder.tvMessageMentions = (TextView) convertView.findViewById(R.id.tvMessageMentions);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message m = getItem(position);
        viewHolder.tvMessageNumber.setText(String.valueOf(position+1));
        viewHolder.tvMessageText.setText(m.getFullText());
        if (m.getMentions().isEmpty()) {
            viewHolder.llMessageMentions.setVisibility(View.GONE);
        } else {
            viewHolder.llMessageMentions.setVisibility(View.VISIBLE);
            viewHolder.tvMessageMentions.setText(m.getMentionsStr());
        }
        return convertView;
    }

    public List<Message> getMessages() {
        List<Message> messages = new ArrayList<>(getCount());
        for (int i = 0; i < getCount(); i++) {
            messages.add(getItem(i));
        }
        return messages;
    }

    private static class ViewHolder {
        TextView tvMessageNumber;
        TextView tvMessageText;
        LinearLayout llMessageMentions;
        TextView tvMessageMentions;
    }
}
