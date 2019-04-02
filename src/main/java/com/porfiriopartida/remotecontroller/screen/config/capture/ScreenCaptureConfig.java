package com.porfiriopartida.remotecontroller.screen.config.capture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 screen:
     capture:
         small:
             width: 100
             height: 50
 */
@Component
@ConfigurationProperties(prefix = "screen.capture")
public class ScreenCaptureConfig {
    @Autowired
    private ScreenCaptureSmallConfig small;
    private boolean centered;

    public ScreenCaptureSmallConfig getSmall() {
        return small;
    }

    public void setSmall(ScreenCaptureSmallConfig small) {
        this.small = small;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }
}
