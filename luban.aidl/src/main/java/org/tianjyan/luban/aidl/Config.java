package org.tianjyan.luban.aidl;

public class Config {
    public final static int RES_CODE_NONE = -1;
    public final static int RES_CODE_OK = 200;
    public final static int RES_CODE_REFUSE = 403;
    public final static int RES_CODE_VERSION_INVALID = 406;

    public final static String TAG = "TAG";
    public final static String VERBOSE = "VERBOSE";
    public final static String DEBUG = "DEBUG";
    public final static String INFO = "INFO";
    public final static String WARNING = "WARNING";
    public final static String ERROR = "ERROR";
    public final static int LOG_VERBOSE = 0;
    public final static int LOG_DEBUG = 1;
    public final static int LOG_INFO = 2;
    public final static int LOG_WARNING = 3;
    public final static int LOG_ERROR = 4;

    public final static int INTERVAL_VID = 10000;

    public final static int MAX_CLIENT_SUPPORT = 5;
    public final static int MAX_OUT_PARA_SUPPORT = 20;
    public final static int MAX_HISTORIES_SUPPORT = 1500000;
    public final static int MAX_LOG_SUPPORT = 1000;
    public final static int MAX_IN_PARA_SUPPORT = 5;
    public final static int MAX_FLOATING_COUNT = 3;
    public final static int MAX_LOG_FILE_SIZE = 2 * 1024 * 1024;
    public final static int MAX_LOG_FILES = 5;
}
