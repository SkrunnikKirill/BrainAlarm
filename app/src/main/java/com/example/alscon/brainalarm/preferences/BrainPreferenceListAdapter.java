package com.example.alscon.brainalarm.preferences;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.alscon.brainalarm.Brain;
import com.example.alscon.brainalarm.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by Alscon on 23-Nov-16.
 */

public class BrainPreferenceListAdapter extends BaseAdapter  {

    private final String[] repeatDays = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    private final String[] brainDifficulties = {"Easy","Medium","Hard"};
    private Context context;
    private Brain brain;
    private List<BrainPreference> preferences = new ArrayList<>();
    private String[] brainTones;
    private String[] brainTonePaths;

    public BrainPreferenceListAdapter(Context context, Brain brain) {
        setContext(context);


		(new Runnable(){

			@Override
			public void run() {
//				Log.d("BrainPreferenceListAdapter", "Loading Ringtones...");

        RingtoneManager ringtoneMgr = new RingtoneManager(getContext());

        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);

        Cursor brainsCursor = ringtoneMgr.getCursor();

        brainTones = new String[brainsCursor.getCount()+1];
        brainTones[0] = "Silent";
        brainTonePaths = new String[brainsCursor.getCount()+1];
        brainTonePaths[0] = "";

//        if (brainsCursor.moveToFirst()) {
//            do {
//                brainTones[brainsCursor.getPosition()+1] = ringtoneMgr.getRingtone(brainsCursor.getPosition()).getTitle(getContext());
//                brainTonePaths[brainsCursor.getPosition()+1] = ringtoneMgr.getRingtoneUri(brainsCursor.getPosition()).toString();
//            }while(brainsCursor.moveToNext());
//        }
//				Log.d("AlarmPreferenceListAdapter", "Finished Loading " + alarmTones.length + " Ringtones.");

        brainsCursor.close();

			}

		}).run();
//
        setMathBrain(brain);
    }

    @Override
    public int getCount() {
        return preferences.size();
    }

    @Override
    public Object getItem(int position) {
        return preferences.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BrainPreference brainPreference = (BrainPreference) getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        switch (brainPreference.getType()) {
            case BOOLEAN:
                if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_checked)
                    convertView = layoutInflater.inflate(android.R.layout.simple_list_item_checked, null);

                CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
                checkedTextView.setText(brainPreference.getTitle());
                checkedTextView.setTextColor(Color.parseColor("#FFFFFF"));
                checkedTextView.setChecked((Boolean) brainPreference.getValue());
                break;
            case INTEGER:
            case STRING:
            case LIST:
            case MULTIPLE_LIST:
            case TIME:
            default:
                if(null == convertView || convertView.getId() !=  android.R.layout.simple_list_item_2)
                    convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);

                TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
                text1.setTextSize(18);
                text1.setTextColor(Color.parseColor("#FFFFFF"));
                text1.setText(brainPreference.getTitle());

                TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
                text2.setTextColor(Color.parseColor("#FFFFFF"));
                text2.setText(brainPreference.getSummary());
                break;
        }

        return convertView;
    }

    public Brain getMathBrain() {
        for(BrainPreference preference : preferences){
            switch(preference.getKey()){
                case BRAIN_ACTIVE:
                    brain.setBrainActive((Boolean) preference.getValue());
                    break;
                case BRAIN_NAME:
                    brain.setBrainName((String) preference.getValue());
                    break;
                case BRAIN_TIME:
                    brain.setBrainTime((String) preference.getValue());
                    break;
                case BRAIN_DIFFICULTY:
                    brain.setDifficulty(Brain.Difficulty.valueOf((String)preference.getValue()));
                    break;
                case BRAIN_TONE:
                    brain.setBrainTonePath((String) preference.getValue());
                    break;
                case BRAIN_VIBRATE:
                    brain.setVibrate((Boolean) preference.getValue());
                    break;
                case BRAIN_REPEAT:
                    brain.setDays((Brain.Day[]) preference.getValue());
                    break;
                case BRAIN_IMAGE:
                    break;

            }
        }

        return brain;
    }

    public void setMathBrain(Brain brain) {
        this.brain = brain;
        preferences.clear();
        preferences.add(new BrainPreference(BrainPreference.Key.BRAIN_ACTIVE,"Active", null, null, brain.getBrainActive(), BrainPreference.Type.BOOLEAN));
        preferences.add(new BrainPreference(BrainPreference.Key.BRAIN_NAME, "Label",brain.getBrainName(), null, brain.getBrainName(), BrainPreference.Type.STRING));
        preferences.add(new BrainPreference(BrainPreference.Key.BRAIN_TIME, "Set time",brain.getBrainTimeString(), null, brain.getBrainTime(), BrainPreference.Type.TIME));
        preferences.add(new BrainPreference(BrainPreference.Key.BRAIN_REPEAT, "Repeat",brain.getRepeadDaysString(), repeatDays, brain.getDays(), BrainPreference.Type.MULTIPLE_LIST));
        preferences.add(new BrainPreference(BrainPreference.Key.BRAIN_DIFFICULTY,"Difficulty", brain.getDifficulty().toString(), brainDifficulties, brain.getDifficulty(), BrainPreference.Type.LIST));

        Uri brainToneUri = Uri.parse(brain.getBrainTonePath());
        Ringtone brainTone = RingtoneManager.getRingtone(getContext(), brainToneUri);
//
//        if(brainTone instanceof Ringtone && !brain.getBrainTonePath().equalsIgnoreCase("")){
//            preferences.add(new BrainPreference(BrainPreference.Key.BRAIN_TONE, "Ringtone", brainTone.getTitle(getContext()),brainTones, brain.getBrainTonePath(), BrainPreference.Type.LIST));
//        }else{
//            preferences.add(new BrainPreference(BrainPreference.Key.BRAIN_TONE, "Ringtone", getBrainTones()[0],brainTones, null, BrainPreference.Type.LIST));
//        }

        preferences.add(new BrainPreference(BrainPreference.Key.BRAIN_VIBRATE, "Vibrate",null, null, brain.getVibrate(), BrainPreference.Type.BOOLEAN));
    }
//

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String[] getRepeatDays() {
        return repeatDays;
    }

    public String[] getBrainDifficulties() {
        return brainDifficulties;
    }

    public String[] getBrainTones() {
        return brainTones;
    }

    public String[] getBrainTonePaths() {
        return brainTonePaths;
    }

}

