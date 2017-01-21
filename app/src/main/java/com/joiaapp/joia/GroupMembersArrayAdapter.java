package com.joiaapp.joia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by arnell on 12/23/2016.
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User m = members.get(position);
        viewHolder.tvMemberName.setText(m.getName());
        return convertView;
    }

    private static class ViewHolder {
        TextView tvMemberName;
    }
}
