package com.joiaapp.joia.group;

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
import com.joiaapp.joia.dto.User;

import java.util.List;

/**
 * Created by arnell on 12/23/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class GroupMembersArrayAdapter extends ArrayAdapter<User> {
    private Context context;
    private ViewHolder viewHolder;
    private List<User> members;

    public GroupMembersArrayAdapter(Context context, List<User> members) {
        super(context, -1, members);
        this.context = context;
        this.members = members;
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.member_group_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvMemberName = (TextView) convertView.findViewById(R.id.tvMemberName);
            viewHolder.tvMemberRole = (TextView) convertView.findViewById(R.id.tvMemberRole);
            viewHolder.ivMemberIcon = (ImageView) convertView.findViewById(R.id.ivMemberIcon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User m = members.get(position);
        viewHolder.tvMemberName.setText(m.getName());
        viewHolder.tvMemberRole.setText(m.getRole());
        Bitmap userImage = GroupService.getInstance().getGroupMemberImageBitmap(m);
        viewHolder.ivMemberIcon.setImageBitmap(userImage);
        return convertView;
    }

    public void setMembers(List<User> newMembers) {
        members.clear();
        for (User member : newMembers) {
            members.add(member);
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView tvMemberName;
        TextView tvMemberRole;
        ImageView ivMemberIcon;
    }
}
