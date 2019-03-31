package com.porfiriopartida.remotecontroller;

import com.porfiriopartida.remotecontroller.utils.image.RobotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

@Component
public class ThreadHandler {
    private boolean IS_RUNNING = false;
    private static final Logger logger = LogManager.getLogger(ThreadHandler.class);

    private ArrayList<Thread> threads;

    public ThreadHandler() {
        threads = new ArrayList<>();
    }

    private Thread alwaysRunThread;

    public Thread getAlwaysRunThread() {
        return alwaysRunThread;
    }

    public Thread getTestCaseThread() {
        return testCaseThread;
    }

    public Thread getIdentifyThread() {
        return identifyThread;
    }

    private Thread testCaseThread;
    private Thread identifyThread;

    public static final String TEST_CASE_RUNNING_EXCEPTION = "Test Case is already running";

    public void runTestCase(String namespace, String testCase) throws Exception {
        if(IS_RUNNING) {
            throw new Exception(TEST_CASE_RUNNING_EXCEPTION);
        }
        IS_RUNNING = true;

        executeThreadAlwaysClick();
        executeThreadIdentifier();
        executeThreadTestCase();
        for(Thread t : this.threads){
            t.start();
        }
    }

    private void executeThreadTestCase() {
        this.testCaseThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(IS_RUNNING){

                }
            }
        };
        this.threads.add(testCaseThread);
    }
    private void executeThreadIdentifier() {
        this.identifyThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(IS_RUNNING){

                }
            }
        };
        this.threads.add(identifyThread);
    }
    private void executeThreadAlwaysClick() {
        this.alwaysRunThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(IS_RUNNING){

                }
            }
        };
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
    public void stop() {
        IS_RUNNING = false;
        while(isAnyThreadRunning()){
            //Lock the current thread until all running threads are stopped.
        }
    }
}
