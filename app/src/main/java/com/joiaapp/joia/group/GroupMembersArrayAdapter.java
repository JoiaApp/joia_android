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

import com.joiaapp.joia.R;
import com.joiaapp.joia.dto.User;
import com.joiaapp.joia.service.ServiceFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by arnell on 12/23/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class GroupMembersArrayAdapter extends ArrayAdapter<User> {
    private Context context;
    private ViewHolder viewHolder;
    private List<User> members;
    private boolean selectable;
    private Set<User> selectedMap = new HashSet<>();

    public GroupMembersArrayAdapter(Context context, List<User> members, boolean selectable) {
        super(context, -1, members);
        this.context = context;
        this.members = members;
        this.selectable = selectable;
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
            viewHolder.ivCheck = (ImageView) convertView.findViewById(R.id.ivCheck);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User m = members.get(position);
        viewHolder.tvMemberName.setText(m.getName());
        viewHolder.tvMemberRole.setText(m.getRole());
        Bitmap userImage = ServiceFactory.getGroupService().getGroupMemberImageBitmap(m);
        viewHolder.ivMemberIcon.setImageBitmap(userImage);
        if (selectable && selectedMap.contains(m)) {
            viewHolder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivCheck.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setMembers(List<User> newMembers) {
        members.clear();
        for (User member : newMembers) {
            members.add(member);
        }
        notifyDataSetChanged();
    }

    public void toggleSelected(User member) {
        if (selectedMap.contains(member)) {
            selectedMap.remove(member);
        } else {
            selectedMap.add(member);
        }
        notifyDataSetChanged();
    }

    public Set<User> getSelected() {
        return selectedMap;
    }

    private static class ViewHolder {
        TextView tvMemberName;
        TextView tvMemberRole;
        ImageView ivMemberIcon;
        ImageView ivCheck;
    }
}
