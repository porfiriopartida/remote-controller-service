package com.porfiriopartida.remotecontroller.automation;

import com.porfiriopartida.remotecontroller.threads.ThreadHandler;
import com.porfiriopartida.remotecontroller.threads.ThreadHandlerCallback;
import com.porfiriopartida.remotecontroller.utils.image.RobotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static com.porfiriopartida.remotecontroller.automation.config.AutomationConstants.*;

@Component
public class AutomationConfigurationHandler implements ThreadHandlerCallback {
    private static final Logger logger = LogManager.getLogger(AutomationConfigurationHandler.class);

    @Autowired
    AutomationConfiguration configuration;

//    public String[] identifiersValues;
    //TODO: Move to config file class
    @Value("${automation.files.strictMode}")
    private boolean strictMode;
    @Value("${automation.files.external_resources_path}")
    private String externalResourcesDirectory;
    @Autowired
    private RobotUtils robotUtils;

    private String getAlwaysClickResource(String namespace, String testCase, String resource) throws FileNotFoundException {
        String filename = String.format("in/%s/test_cases/%s/always_click/%s", namespace, testCase, resource);
        return getResourceFilename(filename);
    }

    private final String[] getTestStepResources(String namespace, String testName, String[] filenames) throws FileNotFoundException {
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
        if(!parentDirectory.isDirectory() ){
            if(strictMode){
                throw new FileNotFoundException(String.format("Directory not found %s", externalResourcesDirectory));
            }
        }
        String finalFilename = String.format("%s" + FILE_SEPARATOR + "%s", externalResourcesDirectory, filename);
        File resource = new File(finalFilename );
        if(!resource.exists()){
            if(strictMode){
                throw new FileNotFoundException(String.format("File not found: %s", finalFilename));
            }
            return finalFilename;
        } else if(resource.isDirectory()){
            throw new InvalidFileNameException(finalFilename, String.format("File must not be a directory: %s", finalFilename));
        }

        return resource.getPath();
    }
    public Step[] getSteps(String namespace, String testCase){
        List<String> stepsArray = getStepsStringList(namespace, testCase);
        return Step.fromArray(namespace, testCase, stepsArray);
    }
    public List<String> getStepsStringList(String namespace, String testCase){
        String propertyFormat = "automation.%s.test_cases.%s.steps";
        String propertyString = String.format(propertyFormat, namespace, testCase);
        return (List<String>) configuration.getProperty(propertyString);
    }

    public void runTestCase(String namespace, String testCase) throws Exception {
        IDENTIFIER = DEFAULT_IDENTIFIER;
        List<String> list = (List<String>) configuration.getProperty(String.format(ALWAYS_CLICK_PATTERN, namespace, testCase));
        String[] alwaysClickArray = new String[list.size()];
        alwaysClickArray = list.toArray(alwaysClickArray);
//        String identifyConfiguration = environment.getProperty(String.format(IDENTIFY_PATTERN, namespace));

        //This is just so these are put in context and run once (they are a while true thread)
        ThreadHandler threadHandler = new ThreadHandler(this);
        threadHandler.setRobotUtils(this.robotUtils);
        threadHandler.setName(String.format("%s.%s", namespace, testCase));
        Step[] steps = null;

        logger.debug("Initializing threads for always/identifiers");
        if(alwaysClickArray.length > 0){
            for(int i = 0; i<alwaysClickArray.length;i++){
                alwaysClickArray[i] = getAlwaysClickResource(namespace, testCase, alwaysClickArray[i]);
            }
        }

        if(testCase != null){
            steps = getSteps(namespace, testCase);

            for (Step step : steps) {
                step.setFilenames(getTestStepResources(namespace, testCase, step.getFilenames()));
            }
        }

        threadHandler.setup(alwaysClickArray, steps);
        threadHandler.runTestCase();
    }

    @Override
    public void testExecutionCompleted(ThreadHandler handler) {
        Step[] steps = handler.getSteps();
        logger.info(String.format("Test results for: %s [%s]", handler.getName(), handler.getRunStatus()));
        for(Step step : steps){
            logger.info(String.format("Test run: %s [%s]", step.getStepName(), step.getRunStatus()));
        }
    }
}
