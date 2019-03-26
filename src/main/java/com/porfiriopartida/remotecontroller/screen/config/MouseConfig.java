package com.porfiriopartida.remotecontroller.screen.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mouse")
public class MouseConfig {
    private int pressDelay;
    private int maxClicks;

    public int getPressDelay() {
        return pressDelay;
    }

    public void setPressDelay(int pressDelay) {
        this.pressDelay = pressDelay;
    }

    public int getMaxClicks() {
        return maxClicks;
    }

    public void setMaxClicks(int maxClicks) {
        this.maxClicks = maxClicks;
    }
}
