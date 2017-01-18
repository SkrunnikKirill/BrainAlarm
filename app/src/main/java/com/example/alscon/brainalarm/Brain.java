package com.example.alscon.brainalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import com.example.alscon.brainalarm.alert.BrainBroadcastService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alscon on 23-Nov-16.
 */
public class Brain implements Serializable {
    public static final long serialVersionUID = 8699489847426803789L;
    private int id;
    private Boolean brainActive = true;
    private Calendar brainTime = Calendar.getInstance();
    private Day[] days = {Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY, Day.SATURDAY, Day.SUNDAY};
    private String brainTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
    private Boolean vibrate = true;
    private String brainName = "Brain Alarm";
    private Difficulty mDifficulty = Difficulty.EASY;
    public Brain() {

    }

    public Boolean getBrainActive() {
        return brainActive;
    }

    public void setBrainActive(Boolean brainActive) {
        this.brainActive = brainActive;
    }

    public Calendar getBrainTime() {
        if (brainTime.before(Calendar.getInstance())) {
            brainTime.add(Calendar.DAY_OF_MONTH, 1);
        }
        while (!Arrays.asList(getDays()).contains(Day.values()[brainTime.get(Calendar.DAY_OF_WEEK) - 1])) {
            brainTime.add(Calendar.DAY_OF_MONTH, 1);
        }
        return brainTime;
    }

    public void setBrainTime(String brainTime) {
        String[] timePices = brainTime.split(":");
        Calendar newBrainTime = Calendar.getInstance();
        newBrainTime.set(Calendar.HOUR_OF_DAY,
                Integer.parseInt(timePices[0]));
        newBrainTime.set(Calendar.MINUTE, Integer.parseInt(timePices[1]));
        newBrainTime.set(Calendar.SECOND, 0);
        setBrainTime(newBrainTime);
    }

    public String getBrainTimeString() {
        String time = "";
        if (brainTime.get(Calendar.HOUR_OF_DAY) <= 9) {
            time += "0";
        }
        time += String.valueOf(brainTime.get(Calendar.HOUR_OF_DAY));
        time += ":";
        if (brainTime.get(Calendar.MINUTE) <= 9) {
            time += "0";
        }
        time += String.valueOf(brainTime.get(Calendar.MINUTE));
        return time;
    }

    public void setBrainTime(Calendar brainTime) {
        this.brainTime = brainTime;
    }

    public Day[] getDays() {
        return days;
    }

    public void setDays(Day[] days) {
        this.days = days;
    }

    public void addDay(Day day) {
        boolean contains = false;
        for (Day d : getDays()) {
            if (d.equals(day)) {
                contains = true;
            }
        }

        if (!contains) {
            List<Day> result = new LinkedList<>();
            for (Day d : getDays()) {
                result.add(d);
            }
            result.add(day);
            setDays(result.toArray(new Day[result.size()]));

        }

    }

    public void removeDay(Day day) {
        List<Day> result = new LinkedList<>();
        for (Day d : getDays()) {
            if (!d.equals(day)) {
                result.add(d);
            }
        }
        setDays(result.toArray(new Day[result.size()]));

    }

    public String getBrainTonePath() {
        return brainTonePath;
    }

    public void setBrainTonePath(String brainTonePath) {
        this.brainTonePath = brainTonePath;
    }

    public Boolean getVibrate() {
        return vibrate;
    }

    public void setVibrate(Boolean vibrate) {
        this.vibrate = vibrate;
    }

    public String getBrainName() {
        return brainName;
    }

    public void setBrainName(String brainName) {
        this.brainName = brainName;
    }

    public Difficulty getDifficulty() {
        return mDifficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        mDifficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRepeadDaysString() {
        StringBuffer daysStringBuffer = new StringBuffer();
        if (getDays().length == Day.values().length) {
            daysStringBuffer.append("Every Day");
        } else {
            Arrays.sort(getDays(), new Comparator<Day>() {
                @Override
                public int compare(Day day, Day t1) {
                    return day.ordinal() - t1.ordinal();
                }
            });

            for (Day day : getDays()) {
                switch (day) {
                    case TUESDAY:
                    case THURSDAY:
                    default:
                        daysStringBuffer.append(day.toString().substring(0, 3));
                        break;
                }
                daysStringBuffer.append(',');
            }
            daysStringBuffer.setLength(daysStringBuffer.length() - 1);

        }
        return daysStringBuffer.toString();
    }

    public void schedule(Context context) {
        setBrainActive(true);
        Intent intent = new Intent(context, BrainBroadcastService.class);
        intent.putExtra("brain", this);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getBrainTime().getTimeInMillis(), pendingIntent);
    }

    public String getTimeUntilNextBrainMessage() {
        long timeDifference = getBrainTime().getTimeInMillis() - System.currentTimeMillis();
        long days = timeDifference / (1000 * 3600 * 24);
        long hours = timeDifference / (1000 * 3600) - (days * 24);
        long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
        long seconds = timeDifference / (1000) - (days * 24 * 3600) - (hours * 3600) - (minutes * 60);
        String alert = "Brain Alarm will sound in ";
        if (days > 0) {
            alert += String.format("%d days, %d hours, %d minutes and %d seconds", days,
                    hours, minutes, seconds);
        } else if (hours > 0) {
            alert += String.format("%d hours, %d minutes and %d seconds", hours,
                    minutes, seconds);
        }else if (minutes>0){
            alert+=String.format("%d minutes and %d seconds", minutes, seconds);
        }else{
            alert+=String.format("%d seconds", seconds);
        }
        return alert;
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD;


        @Override
        public String toString() {
            switch (this.ordinal()) {
                case 0:
                    return "Easy";
                case 1:
                    return "Medium";
                case 2:
                    return "Hard";
            }
            return super.toString();
        }
    }

    public enum Day {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY;


        @Override
        public String toString() {
            switch (this.ordinal()) {
                case 0:
                    return "Sunday";
                case 1:
                    return "Monday";
                case 2:
                    return "Tuesday";
                case 3:
                    return "Wednesday";
                case 4:
                    return "Thursday";
                case 5:
                    return "Friday";
                case 6:
                    return "Saturday";
            }
            return super.toString();
        }
    }
}
