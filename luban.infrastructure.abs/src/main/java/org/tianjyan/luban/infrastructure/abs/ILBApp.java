package org.tianjyan.luban.infrastructure.abs;

import android.content.Context;

public interface ILBApp {
    Context getContext();
    String getSetting(SettingKey key, String defaultValue);
}
