package com.porfiriopartida.remotecontroller.automation.config;

public class AutomationConstants {
    public static final boolean DEFAULT_WAIT_BEHAVIOR = true;
    public static final String RUN_SUCCESS = "SUCCESS";
    public static final String RUN_FAILURE = "FAILURE";


    public static final int SCAN_DELAY = 2000;
    public static final int SCAN_DELAY_RND = 1000;


    public static final String ALWAYS_CLICK_PATTERN = "automation.%s.test_cases.%s.always_click";
    public static final String IDENTIFY_PATTERN = "automation.%s.identify";
    public static String DEFAULT_IDENTIFIER = "UNKNOWN";
    public static String IDENTIFIER = DEFAULT_IDENTIFIER;
    public static final String IDENTIFIER_SPLITTER = ",";
}
