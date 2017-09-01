package org.tianjyan.luban.plugin.log;

import org.tianjyan.luban.infrastructure.abs.ILBApp;
import org.tianjyan.luban.infrastructure.abs.plugin.ILogPlugin;
import org.tianjyan.luban.infrastructure.abs.inject.PreFragment;
import org.tianjyan.luban.infrastructure.common.consts.AliasName;
import org.tianjyan.luban.plugin.log.activity.LogFragment;
import org.tianjyan.luban.plugin.log.bridge.UILogBridge;
import org.tianjyan.luban.plugin.log.manager.LogManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class LogModule {
    @Provides
    @Named(AliasName.LOG_PLUGIN)
    @Singleton
    public static ILogPlugin provideILogPlugin(ILBApp app,
                                               @Named(AliasName.LOG_MANAGER) Lazy<LogManager> logManagerLazy) {
        return new LogPlugin(app, logManagerLazy);
    }

    @Provides
    @Named(AliasName.LOG_MANAGER)
    @Singleton
    public static LogManager provideLogManager(ILBApp app,
                                               @Named(AliasName.LOG_BRIDGE)UILogBridge logBridge) {
        return new LogManager(app, logBridge);
    }


    @Provides
    @Named(AliasName.LOG_BRIDGE)
    @Singleton
    public static UILogBridge provideLogBridge() {
        return new UILogBridge();
    }

    @PreFragment
    @ContributesAndroidInjector
    abstract LogFragment logFragmentInjector();
}