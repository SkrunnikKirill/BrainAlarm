package com.example.alscon.brainalarm;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alscon.brainalarm.datebase.DataBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alscon on 23-Nov-16.
 */

public class BrainListAdapter extends BaseAdapter {
    public static final String BRAIN_FIELDS[] = { DataBase.COLUMN_BRAIN_ACTIVE,
            DataBase.COLUMN_BRAIN_TIME, DataBase.COLUMN_BRAIN_DAYS };
    private BrainActivity brainActivity;
    private List<Brain> brains = new ArrayList<>();

    public BrainListAdapter(BrainActivity brainActivity) {
        this.brainActivity = brainActivity;
//		Database.init(alarmActivity);
//		alarms = Database.getAll();
    }

    @Override
    public int getCount() {
        return brains.size();
    }

    @Override
    public Object getItem(int position) {
        return brains.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (null == view)
            view = LayoutInflater.from(brainActivity).inflate(
                    R.layout.brain_list_element, null);

        Brain brain = (Brain) getItem(position);

        Switch aSwitch = (Switch) view.findViewById(R.id.checkBox_brain_active);
        aSwitch.setChecked(brain.getBrainActive());
        aSwitch.setTag(position);
        aSwitch.setOnClickListener(brainActivity);

        TextView brainTimeView = (TextView) view
                .findViewById(R.id.textView_brain_time);
        brainTimeView.setTextColor(Color.parseColor("#FFFFFF"));
        brainTimeView.setText(brain.getBrainTimeString());


        TextView brainDaysView = (TextView) view
                .findViewById(R.id.textView_brain_days);
        brainDaysView.setTextColor(Color.parseColor("#9bffffff"));
        brainDaysView.setText(brain.getRepeadDaysString());

        TextView brainComplexityView = (TextView)view.findViewById(R.id.textView_complexity);
        brainComplexityView.setTextColor(Color.parseColor("#9bffffff"));
        brainComplexityView.setText(brain.getDifficulty().toString());


        return view;
    }

    public List<Brain> getMathBrains() {
        return brains;
    }

    public void setMathBrains(List<Brain> brains) {
        this.brains = brains;
    }
}
