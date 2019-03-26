package com.porfiriopartida.remotecontroller.screen.config.capture;


import org.springframework.stereotype.Component;

@Component
public class ScreenCaptureSmallConfig {
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
