package org.tianjyan.luban.bridge;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.tianjyan.luban.activity.LogDataAdapter;
import org.tianjyan.luban.aidl.Config;
import org.tianjyan.luban.event.LogEvent;
import org.tianjyan.luban.model.LogEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UILogBridge {
    private static UILogBridge INSTANCE = new UILogBridge();
    public static UILogBridge getInstance() {
        return INSTANCE;
    }

    private ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private List<LogEntry> logEntries = Collections.synchronizedList(new ArrayList<>());
    private Map<String, Integer> tagMap = new ConcurrentHashMap<>();
    private List<String> levels;
    private LogDataAdapter logDataAdapter;

    public UILogBridge() {
        EventBus.getDefault().register(this);
    }

    public LogDataAdapter getLogDataAdapter(Context context) {
        if (logDataAdapter == null) {
            logDataAdapter = new LogDataAdapter(context, logEntries, lock);
        }
        return logDataAdapter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogEvent(LogEvent event) {
        lock.writeLock().lock();
        logEntries.addAll(event.getEntries());
        for (LogEntry log : event.getEntries()) {
            Integer count = tagMap.get(log.getTag());
            if (count == null) {
                count = 1;
                tagMap.put(log.getTag(), count);
            } else {
                count++;
            }
        }
        lock.writeLock().unlock();
        logDataAdapter.onNewEntries(event.getEntries());
    }

    public List<String> getTags() {
        Set<String> tags = tagMap.keySet();
        List<String> allTags = new ArrayList<>();
        allTags.add(Config.TAG);
        allTags.addAll(tags);
        return allTags;
    }

    public List<String> getLevels() {
        if (levels == null) {
            levels = new ArrayList<>();
            levels.add(Config.VERBOSE);
            levels.add(Config.DEBUG);
            levels.add(Config.INFO);
            levels.add(Config.WARNING);
            levels.add(Config.ERROR);
        }
        return levels;
    }

    public void setTag(String tag) {
        logDataAdapter.setTag(tag);
    }

    public void setLevel(String strlevel) {
        int level = Config.LOG_VERBOSE;
        switch (strlevel) {
            case Config.DEBUG:
                level = Config.LOG_DEBUG;
                break;
            case Config.INFO:
                level = Config.LOG_INFO;
                break;
            case Config.WARNING:
                level = Config.LOG_WARNING;
                break;
            case Config.ERROR:
                level = Config.LOG_ERROR;
                break;
            default:
                break;
        }
        logDataAdapter.setLevel(level);
    }

    public void setMsg(String msg) {
        logDataAdapter.setMsg(msg);
    }
}
