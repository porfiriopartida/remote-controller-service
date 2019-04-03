package com.porfiriopartida.remotecontroller.threads;

public class TestCaseThread extends Thread{
    private boolean isReady;

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

}
