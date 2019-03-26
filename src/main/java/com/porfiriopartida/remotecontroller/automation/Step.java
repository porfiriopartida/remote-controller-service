package com.porfiriopartida.remotecontroller.automation;

public class Step {
    private boolean wait;
    private String[] filenames;
    private String namespace;
    private String testName;

    public static Step[] fromArray(String namespace, String testCase, String[] array){
        Step[] steps = new Step[array.length];
        for (int i = 0; i < array.length; i++) {
            Step step = new Step();
            step.setNamespace(namespace);
            step.setTestName(testCase);
            String[] args = array[i].split("\\|");
            if(args.length == 1){
                step.setFilenames(array[i]);
                step.setWait(true);
            } else if(args.length == 2){
                step.setFilenames(args[1]);
                step.setWait("1".equals(args[0]));
            } else{
                step = null;
            }
            steps[i] = step;
        }
        return steps;
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
}
