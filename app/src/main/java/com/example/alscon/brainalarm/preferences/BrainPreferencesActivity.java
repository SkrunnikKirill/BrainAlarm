package com.example.alscon.brainalarm.preferences;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.alscon.brainalarm.BaseActivity;
import com.example.alscon.brainalarm.Brain;
import com.example.alscon.brainalarm.MyToast;
import com.example.alscon.brainalarm.R;
import com.example.alscon.brainalarm.datebase.DataBase;

import java.util.Calendar;

/**
 * Created by Alscon on 23-Nov-16.
 */

public class BrainPreferencesActivity extends BaseActivity {
    ImageButton deleteButton;
    TextView okButton;
    TextView cancelButton;
    private Brain brain;
    private MediaPlayer mediaPlayer;
    private Button save,cancel;
    private ListAdapter listAdapter;
    private ListView listView;
    private CountDownTimer brainToneTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
  //      ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.brain_preferences);
        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(this);
        cancel = (Button)findViewById(R.id.delete);
        cancel.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("brain")) {
            setMathBrain((Brain) bundle.getSerializable("brain"));
        } else {
            setMathBrain(new Brain());
        }
        if (bundle != null && bundle.containsKey("adapter")) {
            setListAdapter((BrainPreferenceListAdapter) bundle.getSerializable("adapter"));
        } else {
            setListAdapter(new BrainPreferenceListAdapter(this, getMathBrain()));
        }

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                final BrainPreferenceListAdapter brainPreferenceListAdapter = (BrainPreferenceListAdapter) getListAdapter();
                final BrainPreference brainPreference = (BrainPreference) brainPreferenceListAdapter.getItem(position);

                AlertDialog.Builder alert;
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                switch (brainPreference.getType()) {
                    case BOOLEAN:
                        CheckedTextView checkedTextView = (CheckedTextView) v;
                        boolean checked = !checkedTextView.isChecked();
                        ((CheckedTextView) v).setChecked(checked);
                        switch (brainPreference.getKey()) {
                            case BRAIN_ACTIVE:
                                brain.setBrainActive(checked);
                                break;
                            case BRAIN_VIBRATE:
                                brain.setVibrate(checked);
                                if (checked) {
                                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    vibrator.vibrate(1000);
                                }
                                break;
                        }
                        brainPreference.setValue(checked);
                        break;
                    case STRING:

                        alert = new AlertDialog.Builder(BrainPreferencesActivity.this,AlertDialog.THEME_HOLO_DARK);

                        alert.setTitle(brainPreference.getTitle());
                        // alert.setMessage(message);

                        // Set an EditText view to get user input
                        final EditText input = new EditText(BrainPreferencesActivity.this);
                        input.setTextColor(Color.parseColor("#FFFFF"));
                        input.setText(brainPreference.getValue().toString());

                        alert.setView(input);
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                brainPreference.setValue(input.getText().toString());

                                if (brainPreference.getKey() == BrainPreference.Key.BRAIN_NAME) {
                                    brain.setBrainName(brainPreference.getValue().toString());
                                }

                                brainPreferenceListAdapter.setMathBrain(getMathBrain());
                                brainPreferenceListAdapter.notifyDataSetChanged();
                            }
                        });
                        alert.show();
                        break;
                    case LIST:
                        alert = new AlertDialog.Builder(BrainPreferencesActivity.this,AlertDialog.THEME_HOLO_DARK);

                        alert.setTitle(brainPreference.getTitle());
                        // alert.setMessage(message);

                        CharSequence[] items = new CharSequence[brainPreference.getOptions().length];
                        for (int i = 0; i < items.length; i++)
                            items[i] = brainPreference.getOptions()[i];

                        alert.setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (brainPreference.getKey()) {
                                    case BRAIN_DIFFICULTY:
                                        Brain.Difficulty d = Brain.Difficulty.values()[which];
                                        brain.setDifficulty(d);
                                        break;
                                    case BRAIN_TONE:
                                        brain.setBrainTonePath(brainPreferenceListAdapter.getBrainTonePaths()[which]);
                                        if (brain.getBrainTonePath() != null) {
                                            if (mediaPlayer == null) {
                                                mediaPlayer = new MediaPlayer();
                                            } else {
                                                if (mediaPlayer.isPlaying())
                                                    mediaPlayer.stop();
                                                mediaPlayer.reset();
                                            }
                                            try {
                                                // mediaPlayer.setVolume(1.0f, 1.0f);
                                                mediaPlayer.setVolume(0.2f, 0.2f);
                                                mediaPlayer.setDataSource(BrainPreferencesActivity.this, Uri.parse(brain.getBrainTonePath()));
                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                                                mediaPlayer.setLooping(false);
                                                mediaPlayer.prepare();
                                                mediaPlayer.start();

                                                // Force the mediaPlayer to stop after 3
                                                // seconds...
                                                if (brainToneTimer != null)
                                                    brainToneTimer.cancel();
                                                brainToneTimer = new CountDownTimer(3000, 3000) {
                                                    @Override
                                                    public void onTick(long millisUntilFinished) {

                                                    }

                                                    @Override
                                                    public void onFinish() {
                                                        try {
                                                            if (mediaPlayer.isPlaying())
                                                                mediaPlayer.stop();
                                                        } catch (Exception e) {

                                                        }
                                                    }
                                                };
                                                brainToneTimer.start();
                                            } catch (Exception e) {
                                                try {
                                                    if (mediaPlayer.isPlaying())
                                                        mediaPlayer.stop();
                                                } catch (Exception e2) {

                                                }
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                brainPreferenceListAdapter.setMathBrain(getMathBrain());
                                brainPreferenceListAdapter.notifyDataSetChanged();
                            }

                        });

                        alert.show();
                        break;
                    case MULTIPLE_LIST:
                        alert = new AlertDialog.Builder(BrainPreferencesActivity.this,AlertDialog.THEME_HOLO_DARK);

                        alert.setTitle(brainPreference.getTitle());
                        // alert.setMessage(message);

                        CharSequence[] multiListItems = new CharSequence[brainPreference.getOptions().length];
                        for (int i = 0; i < multiListItems.length; i++)
                            multiListItems[i] = brainPreference.getOptions()[i];

                        boolean[] checkedItems = new boolean[multiListItems.length];
                        for (Brain.Day day : getMathBrain().getDays()) {
                            checkedItems[day.ordinal()] = true;
                        }
                        alert.setMultiChoiceItems(multiListItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, int which, boolean isChecked) {

                                Brain.Day thisDay = Brain.Day.values()[which];

                                if (isChecked) {
                                    brain.addDay(thisDay);
                                } else {
                                    // Only remove the day if there are more than 1
                                    // selected
                                    if (brain.getDays().length > 1) {
                                        brain.removeDay(thisDay);
                                    } else {
                                        // If the last day was unchecked, re-check
                                        // it
                                        ((AlertDialog) dialog).getListView().setItemChecked(which, true);
                                    }
                                }

                            }
                        });
                        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                brainPreferenceListAdapter.setMathBrain(getMathBrain());
                                brainPreferenceListAdapter.notifyDataSetChanged();

                            }
                        });
                        alert.show();
                        break;
                    case TIME:
                        TimePickerDialog timePickerDialog = new TimePickerDialog(BrainPreferencesActivity.this,AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                                Calendar newBrainTime = Calendar.getInstance();
                                newBrainTime.set(Calendar.HOUR_OF_DAY, hours);
                                newBrainTime.set(Calendar.MINUTE, minutes);
                                newBrainTime.set(Calendar.SECOND, 0);
                                brain.setBrainTime(newBrainTime);
                                brainPreferenceListAdapter.setMathBrain(getMathBrain());
                                brainPreferenceListAdapter.notifyDataSetChanged();
                            }
                        }, brain.getBrainTime().get(Calendar.HOUR_OF_DAY), brain.getBrainTime().get(Calendar.MINUTE), true);
                        timePickerDialog.setTitle(brainPreference.getTitle());
                        timePickerDialog.show();
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("brain", getMathBrain());
        //outState.putSerializable("adapter", (BrainPreferenceListAdapter) getListAdapter());
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mediaPlayer != null)
                mediaPlayer.release();
        } catch (Exception e) {
        }
        // setListAdapter(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public Brain getMathBrain() {
        return brain;
    }

    public void setMathBrain(Brain brain) {
        this.brain = brain;
    }

    public ListAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
        getListView().setAdapter(listAdapter);

    }

    public ListView getListView() {
        if (listView == null)
            listView = (ListView) findViewById(android.R.id.list);
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                DataBase.init(getApplicationContext());
                if (getMathBrain().getId() < 1) {
                    DataBase.create(getMathBrain());
                } else {
                    DataBase.update(getMathBrain());
                }
                callMathBrainScheduleService();
                MyToast.clock(BrainPreferencesActivity.this, getMathBrain().getTimeUntilNextBrainMessage());
               // Toast.makeText(BrainPreferencesActivity.this, getMathBrain().getTimeUntilNextBrainMessage(), Toast.LENGTH_LONG).show();
                finish();
                break;

            case R.id.delete:
                DataBase.init(getApplicationContext());
                if (getMathBrain().getId() < 1) {
                    // Alarm not saved
                } else {
                    DataBase.deleteEntry(brain);
                    callMathBrainScheduleService();
                }
                finish();
                break;
        }
    }


}


