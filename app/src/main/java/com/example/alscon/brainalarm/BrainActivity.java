package com.example.alscon.brainalarm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.ChangeBounds;
import android.support.transition.Transition;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.alscon.brainalarm.datebase.DataBase;
import com.example.alscon.brainalarm.preferences.BrainPreferencesActivity;

import java.util.List;

/**
 * Created by Alscon on 23-Nov-16.
 */

public class BrainActivity extends BaseActivity implements View.OnClickListener {
    ImageButton newButton;
    ListView mathBrainListView;
    BrainListAdapter brainListAdapter;
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.brain_activity);
        add =(FloatingActionButton)findViewById(R.id.add);
        add.setOnClickListener(this);



        mathBrainListView = (ListView) findViewById(R.id.list);
        mathBrainListView.setLongClickable(true);
        mathBrainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                final Brain brain = (Brain) brainListAdapter.getItem(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(BrainActivity.this);
                dialog.setTitle("Delete");
                dialog.setMessage("Delete this brain?");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DataBase.init(BrainActivity.this);
                        DataBase.deleteEntry(brain);
                        BrainActivity.this.callMathBrainScheduleService();

                        updateBrainList();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

                return true;
            }
        });

        callMathBrainScheduleService();

        brainListAdapter = new BrainListAdapter(this);
        this.mathBrainListView.setAdapter(brainListAdapter);
        mathBrainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Brain brain = (Brain) brainListAdapter.getItem(position);
                Intent intent = new Intent(BrainActivity.this, BrainPreferencesActivity.class);
                intent.putExtra("brain", brain);
                startActivity(intent);
            }

        });

    }



    @Override
    protected void onPause() {
        // setListAdapter(null);
        DataBase.deactivate();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBrainList();
    }

    public void updateBrainList(){
        DataBase.init(BrainActivity.this);
        final List<Brain> brains = DataBase.getAll();
        brainListAdapter.setMathBrains(brains);

        runOnUiThread(new Runnable() {
            public void run() {
                // reload content
                BrainActivity.this.brainListAdapter.notifyDataSetChanged();
                if(brains.size() > 0){
                    findViewById(R.id.empty_brain).setVisibility(View.INVISIBLE);
                }else{
                    findViewById(R.id.empty_brain).setVisibility(View.VISIBLE);
                }
            }
        });
    }




        @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.checkBox_brain_active:
                Switch aSwitch = (Switch) v;
                Brain brain = (Brain) brainListAdapter.getItem((Integer) aSwitch.getTag());
                brain.setBrainActive(aSwitch.isChecked());
                DataBase.update(brain);
                BrainActivity.this.callMathBrainScheduleService();
                if (aSwitch.isChecked()) {
                    MyToast.clock(BrainActivity.this,brain.getTimeUntilNextBrainMessage() );
                   // Toast.makeText(BrainActivity.this, alarm.getTimeUntilNextBrainMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.add:
                Intent newAlarmIntent = new Intent(BrainActivity.this, BrainPreferencesActivity.class);
                startActivity(newAlarmIntent);
                break;

        }
//        if (v.getId() == R.id.checkBox_brain_active) {
//
//        }

    }
}
