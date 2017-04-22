package com.joiaapp.joia.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joiaapp.joia.GroupService;
import com.joiaapp.joia.R;
import com.joiaapp.joia.dto.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by arnell on 12/20/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class MessageJournalArrayAdapter extends ArrayAdapter<Message> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, MMM d");
    private static final SimpleDateFormat DATE_FORMAT_WITH_YEAR = new SimpleDateFormat("E, MMM d, yyyy");

    private final Context context;
    private ViewHolder viewHolder;
    private List<Message> messages = new ArrayList<>();

    private List<Message> sectionHeaders = new ArrayList<>();
    private final LayoutInflater inflater;

    public MessageJournalArrayAdapter(Context context)
    {
        this(context, new ArrayList<Message>());
    }

    public MessageJournalArrayAdapter(Context context, List<Message> messages) {
        super(context, -1, messages);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setMessages(messages);
    }

    public void setMessages(List<Message> newMessages) {
        messages.clear();
        Message prev = null;
        for (Message m : newMessages) {
            if (prev == null || !prev.isSameDateAs(m)) {
                Message separator = new Message();
                separator.setCreatedAt(m.getCreatedAt());
                messages.add(separator);
                sectionHeaders.add(separator);
            }
            messages.add(m);
            prev = m;
        }
        notifyDataSetChanged();
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int rowType = getItemViewType(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (rowType == TYPE_ITEM) {
                convertView = inflater.inflate(R.layout.message_journal_list_item, parent, false);
                viewHolder.tvMessageAuthor = (TextView) convertView.findViewById(R.id.tvMessageAuthor);
                viewHolder.tvMessageText = (TextView) convertView.findViewById(R.id.tvMessageText);
                viewHolder.tvMessageMentions = (TextView) convertView.findViewById(R.id.tvMessageMentions);
                viewHolder.ivMessageAuthorIcon = (ImageView) convertView.findViewById(R.id.ivMessageAuthorIcon);
            } else {
                convertView = inflater.inflate(R.layout.message_journal_separator_item, parent, false);
                viewHolder.tvJournalMessageGroupSeparator = (TextView) convertView.findViewById(R.id.tvJournalMessageGroupSeparator);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message m = messages.get(position);
        if (rowType == TYPE_ITEM) {
            viewHolder.tvMessageAuthor.setText(String.valueOf(m.getUser().getName()));
            viewHolder.tvMessageText.setText(m.getText());
            if (m.getMentions().isEmpty()) {
                viewHolder.tvMessageMentions.setVisibility(View.GONE);
            } else {
                viewHolder.tvMessageMentions.setVisibility(View.VISIBLE);
                viewHolder.tvMessageMentions.setText(m.getMentionsStr());
            }
            Bitmap userImage = GroupService.getInstance().getGroupMemberImageBitmap(m.getUser());
            viewHolder.ivMessageAuthorIcon.setImageBitmap(userImage);
        } else {
            Date today = new Date();
            if (m.isSameDateAs(today)) {
                viewHolder.tvJournalMessageGroupSeparator.setText("Today");
            } else if (!m.isSameYearAs(today)) {
                viewHolder.tvJournalMessageGroupSeparator.setText(DATE_FORMAT_WITH_YEAR.format(m.getCreatedAt()));
            } else {
                viewHolder.tvJournalMessageGroupSeparator.setText(DATE_FORMAT.format(m.getCreatedAt()));
            }
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeaders.contains(messages.get(position)) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    public Message getItem(int position) {
        return messages.get(position);
    }

    private static class ViewHolder {
        TextView tvMessageAuthor;
        TextView tvMessageText;
        TextView tvMessageMentions;
        TextView tvJournalMessageGroupSeparator;
        ImageView ivMessageAuthorIcon;
    }
}
