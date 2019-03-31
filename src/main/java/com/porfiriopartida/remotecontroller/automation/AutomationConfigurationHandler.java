package com.porfiriopartida.remotecontroller.automation;

import com.porfiriopartida.remotecontroller.ThreadHandler;
import com.porfiriopartida.remotecontroller.automation.config.AutomationConstants;
import com.porfiriopartida.remotecontroller.utils.image.RobotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

@Component
public class AutomationConfigurationHandler {
    private static final Logger logger = LogManager.getLogger(AutomationConfigurationHandler.class);
    private static final String IDENTIFIER_SPLITTER = ",";
    @Resource
    public Environment env;
    @Autowired
    private RobotUtils robotUtils;
    public static boolean IS_RUNNING = false;
    public static final String ALWAYS_CLICK_PATTERN = "automation.%s.test_cases.%s.always_click";
    public static final String IDENTIFY_PATTERN = "automation.%s.identify";
    public static final int SCAN_DELAY = 2000;
    public static final int SCAN_DELAY_RND = 1000;
    public static String DEFAULT_IDENTIFIER = "UNKNOWN";
    public static String IDENTIFIER = DEFAULT_IDENTIFIER;
    public String[] identifiersValues;
    //TODO: Move to config file class
    @Value("${automation.files.strictMode}")
    private boolean strictMode;
    @Value("${automation.files.external_resources_path}")
    private String externalResourcesDirectory;

    private Random rnd = new Random();

    public AutomationConfigurationHandler(){
    }

    private void buildIdentifiers() {
        if(identifiersValues != null){
            return;
        }
        String defaultIdentifier = env.getProperty("automation.identifiers.default");
        DEFAULT_IDENTIFIER = defaultIdentifier == null ? DEFAULT_IDENTIFIER:defaultIdentifier;
        String identifiersValuesStr = env.getProperty("automation.identifiers.values");
        if(identifiersValuesStr == null){
            identifiersValues = new String[]{};
        } else {
            identifiersValues = identifiersValuesStr.split(IDENTIFIER_SPLITTER);
        }
    }

    public String getAlwaysClickResource(String namespace, String testCase, String resource) throws FileNotFoundException {
        String filename = String.format("in/%s/test_cases/%s/always_click/%s", namespace, testCase, resource);
        return getResourceFilename(filename);
    }
    public String getIdentifiersResource(String namespace, String resource) throws FileNotFoundException {
        String filename = String.format("in/%s/identifiers/%s", namespace, resource);
        return getResourceFilename(filename);
    }
    public final String[] getTestStepResources(String namespace, String testName, String[] filenames) throws FileNotFoundException {
        String format = "in/%s/test_cases/%s/%s";
        String[] resources = new String[filenames.length];
        for (int i = 0; i < resources.length; i++) {
            resources[i] = getResourceFilename(String.format(format, namespace, testName, filenames[i]));
        }
        return resources;
    }
    public final String[] getResourceFilenames(String[] filenames) throws FileNotFoundException {
        String[] resources = new String[filenames.length];
        for (int i = 0; i < resources.length; i++) {
            resources[i] = getResourceFilename(filenames[i]);
        }
        return resources;
    }
    public final String getResourceFilename(String filename) throws FileNotFoundException {
        //This could be used to replace and force a directory near the executable file
        //AutomationConfigurationHandler.class.getProtectionDomain().getCodeSource().getLocation()     .toURI()
        //Class Loader and getResource can be used here but just for inside the resources.
        File parentDirectory = new File(externalResourcesDirectory);
        if(parentDirectory == null || !parentDirectory.isDirectory() ){
            throw new FileNotFoundException(String.format("Directory not found %s", externalResourcesDirectory));
        }
        String finalFilename = String.format("%s/%s", externalResourcesDirectory, filename);
        File resource = new File(finalFilename );

        if(resource == null || !resource.exists()){
            if(strictMode){
                throw new FileNotFoundException(String.format("File not found: %s", filename));
            }
            return filename;
        } else if(resource.isDirectory()){
            throw new InvalidFileNameException(finalFilename, String.format("File must not be a directory: %s", finalFilename));
        }

        return resource.getPath();
    }
    public Step[] getSteps(String namespace, String testCase){
        String stepsString = getStepsString(namespace, testCase);
        String[] stepsArray = stepsString.split(" "); //the resulting string from envs is always space.
        Step[] steps = Step.fromArray(namespace, testCase, stepsArray);
        return steps;
    }
    public String getStepsString(String namespace, String testCase){
        String propertyFormat = "automation.%s.test_cases.%s.steps";
        String propertyString = String.format(propertyFormat, namespace, testCase);
        return env.getProperty(propertyString);
    }
    public boolean executeTestSteps(Step[] steps){
        boolean result = false;
        for (int i = 0; i < steps.length; i++) {
            logger.debug(String.format("=== Running step %s:%s", i, steps[i].getFilenames()[0]));
            Step step = steps[i];
            try {
                result = robotUtils.clickOnScreen(step.isWait(), getTestStepResources(step.getNamespace(), step.getTestName(), step.getFilenames()));
                Thread.sleep(SCAN_DELAY + rnd.nextInt(SCAN_DELAY_RND));
            } catch (AWTException | InterruptedException | IOException e) {
                e.printStackTrace();
                result = false;
            }
            if(!result){
                logger.error(String.format("=======\nSomething went wrong with the step %s\n======", i));
            }
        }
        return result;
    }
    private void executeThreadTestCase(String namespace, String testCase) {
        final Step[] steps = getSteps(namespace, testCase);
        new Thread(){
            @Override
            public void run() {
                super.run();
                logger.debug(String.format("Running testcase: %s.%s", namespace, testCase));
                boolean result = executeTestSteps(steps);
                logger.debug(String.format("TEST EXECUTION FINISHED: %s.%s - %s", namespace, testCase, result ? AutomationConstants.RUN_SUCCESS : AutomationConstants.RUN_FAILURE));
            }
        }.start();
    }

    private void executeThreadIdentifier(String namespace, String[] identifiers) throws FileNotFoundException {
        for(int i=0;i<identifiers.length;i++){
            identifiers[i] = getIdentifiersResource(namespace, identifiers[i]);
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(identifiersValues == null){
                    buildIdentifiers();
                }

                while(IS_RUNNING){
                    try {
                        String newIdentity = getIdentityName(getIdentity(identifiers));
                        if(newIdentity != IDENTIFIER && newIdentity != null){
                            IDENTIFIER = newIdentity;
                            logger.debug("New identifier: " + IDENTIFIER);
                            //Thread.sleep(SCAN_DELAY * 10);
                        }
                        Thread.sleep(SCAN_DELAY + rnd.nextInt(SCAN_DELAY_RND));
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }.start();
    }

    private String getIdentityName(int identity) {
        if(identity < 0 || identity >= identifiersValues.length){
            return DEFAULT_IDENTIFIER;
        }

        return identifiersValues[identity];
    }

    private void executeThreadAlwaysClick(String namespace, String testCase, String[] alwaysClickArray) throws FileNotFoundException {
        for(int i = 0; i<alwaysClickArray.length;i++){
            alwaysClickArray[i] = getAlwaysClickResource(namespace, testCase, alwaysClickArray[i]);
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                while(IS_RUNNING){
                    try {
                        boolean clicked = robotUtils.clickOnScreen(true, alwaysClickArray);
                        if(clicked){
                            printIdentity();
                        }
                        Thread.sleep(SCAN_DELAY + rnd.nextInt(SCAN_DELAY_RND));
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }.start();
    }
    public void runTestCase(String namespace, String testCase) throws FileNotFoundException {
        IDENTIFIER = DEFAULT_IDENTIFIER;
        final Environment environment = this.env;
        String alwaysClickConfiguration = environment.getProperty(String.format(ALWAYS_CLICK_PATTERN, namespace, testCase));
        String identifyConfiguration = environment.getProperty(String.format(IDENTIFY_PATTERN, namespace));

        //This is just so these are put in context and run once (they are a while true thread)
        if(!IS_RUNNING){
            IS_RUNNING = true;
            ThreadHandler threadHandler = new ThreadHandler();

            logger.debug("Initializing threads for always/identifiers");
            if(alwaysClickConfiguration != null){
                final String[] alwaysClickArray = alwaysClickConfiguration.split(" ");
                executeThreadAlwaysClick(namespace, testCase, alwaysClickArray);
            }
            if(identifyConfiguration != null){
                final String[] identifiers = identifyConfiguration.split(" ");
                executeThreadIdentifier(namespace, identifiers);
            }
            if(testCase != null){
                executeThreadTestCase(namespace, testCase);
            }
        }
    }

    private void printIdentity() {
        logger.debug("Action by: " + IDENTIFIER);
    }

    private int getIdentity(String[] identifiers ) throws IOException, AWTException {
        while(true){
            for (int i = 0; i < identifiers.length; i++) {
                Point coords = robotUtils.findOnScreen(identifiers[i]);
                if(coords != null){
                    return i;
                }
            }
        }
    }
    //Mainly for test purposes.
    public void setRobotUtils(RobotUtils robotUtils) {
        this.robotUtils = robotUtils;
    }
}
