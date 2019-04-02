package com.porfiriopartida.remotecontroller.swgoh;

import com.porfiriopartida.remotecontroller.utils.image.RobotUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;
@Deprecated
public class ClickBot {
    @Autowired
    private RobotUtils robotUtils;
    public static ClickBot self = null;
    private ClickBot(){

    }
    public static ClickBot getInstance(){
        if(self==null){
            self = new ClickBot();
        }
        return self;
    }
    public static boolean running = false;
    public void stop(){
        running = false;
    }
    public void resume(){
        running = true;
    }
    public void start() {
        Robot finalRobot = robotUtils.getRobot();
        final Random rnd = new Random();
        new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    if (!running){
                        break;
                    }
                    try {
//                        PointerInfo a = MouseInfo.getPointerInfo();
//                        Point point = a.getLocation();
//                        logger.debug(String.format("%s, %s", point.x, point.y));
                        finalRobot.mouseMove(rnd.nextInt(20) + 1600, 915);
                        finalRobot.mousePress(InputEvent.BUTTON1_MASK);
                        Thread.sleep(rnd.nextInt(100));
                        finalRobot.mouseRelease(InputEvent.BUTTON1_MASK);

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }.start();
    }
}
