package com.example.alscon.brainalarm.preferences;

/**
 * Created by Alscon on 23-Nov-16.
 */

public class BrainPreference {
    private Key key;
    private String title;
    private String summary;
    private Object value;
    private String[] options;
    private Type type;
    public BrainPreference(Key key, Object value, Type type) {
        this(key,null,null,null, value, type);
    }
    public BrainPreference(Key key, String title, String summary, String[] options, Object value, Type type) {
        setTitle(title);
        setSummary(summary);
        setOptions(options);
        setKey(key);
        setValue(value);
        setType(type);
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public enum Key{
        BRAIN_NAME,
        BRAIN_ACTIVE,
        BRAIN_TIME,
        BRAIN_REPEAT,
        BRAIN_TONE,
        BRAIN_VIBRATE,
        BRAIN_DIFFICULTY,
        BRAIN_IMAGE;
    }

    public enum Type{
        BOOLEAN,
        INTEGER,
        STRING,
        LIST,
        MULTIPLE_LIST,
        TIME
    }
}
