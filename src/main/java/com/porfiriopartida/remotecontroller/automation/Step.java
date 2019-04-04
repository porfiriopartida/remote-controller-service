package com.porfiriopartida.remotecontroller.automation;

import java.util.List;

public class Step {
    private boolean wait;
    private String[] filenames;
    private String namespace;
    private String testName;
    private RunStatus runStatus = RunStatus.NOT_RUN;
    private String stepName;


    public static Step[] fromArray(String namespace, String testCase, List<String> list){
        return fromArray(namespace, testCase, list.toArray(new String[list.size()]));
    }
    public static Step[] fromArray(String namespace, String testCase, String[] array){
        Step[] steps = new Step[array.length];
        for (int i = 0; i < array.length; i++) {
            Step step = new Step();
            step.setNamespace(namespace);
            step.setTestName(testCase);
            String[] args = array[i].split("\\|");
            String name = "Step " + i;
            boolean wait = true;
            String filenames = null;
            if(args.length == 1){
                filenames = array[i];
                name = String.format("Step_%s_%s", i, array[i]);
            } else if(args.length == 2){
                filenames = args[1];
                name = String.format("Step_%s_%s", i, args[1]);
                wait = "1".equals(args[0]);
            } else{
                step = null;
            }

            if(step != null){
                step.setFilenames(filenames);
                step.setWait(wait);
                step.setStepName(name);
            }

            steps[i] = step;
        }
        return steps;
    }

    private void setStepName(String name) {
        this.stepName = name;
    }

    public String[] getFilenames() {
        return filenames;
    }

    public void setFilenames(String[] filenames) {
        this.filenames = filenames;
    }
    public void setFilenames(String filenames) {
        this.filenames = filenames.split(",");
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public RunStatus getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(RunStatus runStatus) {
        this.runStatus = runStatus;
    }

    public String getStepName() {
        return stepName;
    }
}
