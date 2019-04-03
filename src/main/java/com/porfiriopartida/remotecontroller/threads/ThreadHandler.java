package com.porfiriopartida.remotecontroller.threads;

import static com.porfiriopartida.remotecontroller.automation.config.AutomationConstants.*;
import com.porfiriopartida.remotecontroller.automation.Step;
import com.porfiriopartida.remotecontroller.automation.RunStatus;
import com.porfiriopartida.remotecontroller.utils.image.RobotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

@Component
public class ThreadHandler {
    private ThreadHandlerCallback callback;
    private boolean isRunning = false;
    private static final Logger logger = LogManager.getLogger(ThreadHandler.class);

    private Random rnd = new Random();
    private ArrayList<TestCaseThread> threads;

    @Autowired
    private RobotUtils robotUtils;

    RunStatus runStatus = RunStatus.NOT_RUN;

    public RunStatus getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(RunStatus runStatus) {
        this.runStatus = runStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public ThreadHandler() {
        this(null);
    }
    public ThreadHandler(ThreadHandlerCallback callback) {
        this.callback = callback;
        threads = new ArrayList<TestCaseThread>();
        name = "threadHandler";
    }

    private TestCaseThread alwaysRunThread;

    public TestCaseThread getAlwaysRunThread() {
        return alwaysRunThread;
    }

    public TestCaseThread getTestCaseThread() {
        return testCaseThread;
    }

    private TestCaseThread testCaseThread;

    public static final String TEST_CASE_RUNNING_EXCEPTION = "Test Case is already running";
    public void setup(String[] alwaysClick, Step[] steps) throws Exception {
        if(isRunning) {
            throw new Exception(TEST_CASE_RUNNING_EXCEPTION);
        }
        this.steps = steps;

        addThreads(alwaysClick, steps);
    }
    public void runTestCase() throws Exception {
        if(isRunning) {
            throw new Exception(TEST_CASE_RUNNING_EXCEPTION);
        }

        try{
            startThreads();
        }catch (TestCaseValidationException e){
            isRunning = false;
            logger.error(e);
            throw e;
        }

    }

    private void startThreads() throws TestCaseValidationException {
        validate();
        isRunning = true;
        runStatus = RunStatus.FAILED;
        //Validate must run before this.
        for(TestCaseThread t : threads){
            t.start();
        }
    }

    private void addThreads(String[] alwaysClickArray, Step[] steps) {
        buildAlwaysClickThread(alwaysClickArray);
        buildTestCaseThread(steps);
    }

    Step[] steps;
    private void buildTestCaseThread(Step[] steps) {
        final ThreadHandler that = this;
        this.testCaseThread = new TestCaseThread(){
            @Override
            public void run() {
                super.run();
                try {
                    boolean result = executeTestSteps(steps);
                } catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    isRunning = false;
                    verifySteps();
                    if(callback != null){
                        callback.testExecutionCompleted(that);
                    }
                }
            }

            private boolean executeTestSteps(Step[] steps){
                boolean result = false;
                for (int i = 0; i < steps.length && isRunning ; i++) {
                    logger.debug(String.format("=== Running step %s:%s", i, steps[i].getFilenames()[0]));
                    Step step = steps[i];
                    try {
                        result = robotUtils.clickOnScreen(step.isWait(), step.getFilenames());
                        if(step.isWait()){
                            step.setRunStatus(result ? RunStatus.SUCCESS : RunStatus.FAILED);
                        } else {
                            step.setRunStatus(RunStatus.SUCCESS);
                        }
                        Thread.sleep(SCAN_DELAY + rnd.nextInt(SCAN_DELAY_RND));
                    } catch (AWTException | InterruptedException | IOException e) {
                        e.printStackTrace();
                        result = false;
                    }
                    if(!result){
                        logger.error(String.format("=======\nSomething went wrong with the step %s\n======", i));
                        if(step.isWait()){
                            isRunning = false;
                            break;
                        }
                    }
                }
                return result;
            }
        };

        testCaseThread.setReady(steps != null && steps.length > 0);
        this.testCaseThread.setName("testCaseThread");
        this.threads.add(testCaseThread);
    }

    private void verifySteps() {
        RunStatus finalStatus = RunStatus.SUCCESS;
        for(Step step : steps){
            if(RunStatus.FAILED.equals(step.getRunStatus())){
                finalStatus = RunStatus.FAILED;
                break;
            }
        }
        runStatus = finalStatus;
    }

    private void buildAlwaysClickThread(final String[] alwaysClickArray) {
        this.alwaysRunThread = new TestCaseThread(){
            @Override
            public void run() {
                super.run();
                if(!isReady()){
                    return;
                }
                //TODO: Add test for empty / null array
                if(alwaysClickArray == null || alwaysClickArray.length == 0){
                    return;
                }
                while(isRunning){
                    try {
                        boolean clicked = robotUtils.clickOnScreen(false, alwaysClickArray);
                        Thread.sleep(SCAN_DELAY + rnd.nextInt(SCAN_DELAY_RND));
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        };
        alwaysRunThread.setReady(true);
        this.alwaysRunThread.setName("alwaysRunThread");
        this.threads.add(alwaysRunThread);
    }
    public int getThreadsCount() {
        return threads.size();
    }
    public boolean isAnyThreadRunning(){
        for(Thread t : this.threads){
            if(t.isAlive()){
                return true;
            }
        }
        return false;
    }
    public int getRunningThreadsCount(){
        int c = 0;
        for(Thread t : this.threads){
            if(t.isAlive()){
                c++;
            }
        }
        return c;
    }
    public void stopRun() {
        isRunning = false;
        while(isAnyThreadRunning()){
            //Lock the current thread until all running threads are stopped.
        }
    }

    public void validate() throws TestCaseValidationException {
        if(this.threads.size() == 0){
            throw new TestCaseValidationException("Empty threads list.");
        }
        for(TestCaseThread t : threads){
            if(!t.isReady()){
                throw new TestCaseValidationException(String.format("Thread {%s} is not ready", t.getName()));
            }
        }
    }

    public RobotUtils getRobotUtils() {
        return robotUtils;
    }

    public void setRobotUtils(RobotUtils robotUtils) {
        this.robotUtils = robotUtils;
    }

    public Step[] getSteps() {
        return this.steps;
    }

    public String getName() {
        return this.name;
    }
}
