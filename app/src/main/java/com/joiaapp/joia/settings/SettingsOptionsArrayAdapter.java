package com.joiaapp.joia.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joiaapp.joia.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnell on 12/30/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class SettingsOptionsArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> options;
    private ViewHolder viewHolder;

    public SettingsOptionsArrayAdapter(Context context, List<String> options) {
        super(context, -1, options);
        this.context = context;
        this.options = new ArrayList<>(options);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.settings_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvSettingName = (TextView) convertView.findViewById(R.id.tvSettingName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String s = options.get(position);
        viewHolder.tvSettingName.setText(s);
        return convertView;
    }

    private static class ViewHolder {
        TextView tvSettingName;
    }
}
