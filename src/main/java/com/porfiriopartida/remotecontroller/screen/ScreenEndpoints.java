package com.porfiriopartida.remotecontroller.screen;

import com.porfiriopartida.remotecontroller.screen.config.ScreenSizeConfig;
import com.porfiriopartida.remotecontroller.screen.config.capture.ScreenCaptureConfig;
import com.porfiriopartida.remotecontroller.utils.image.RobotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

@RestController
@RequestMapping(value = "/screen")
public class ScreenEndpoints {
    @Autowired
    private ScreenCaptureConfig screenCaptureConfig;
    @Autowired
    private ScreenSizeConfig screenSizeConfig;

    @Autowired
    private RobotUtils robotUtils;

    public static final String IMG_SRC = "<img src=\"data:image/png;base64,%s\" />";

    @RequestMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public Dimension getScreenSize(){
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    @RequestMapping(value = "/capture/small")
    @ResponseStatus(HttpStatus.OK)
    public  String smallCapture(@RequestParam int x, @RequestParam int y, @RequestParam(required = false, defaultValue = "0") int w, @RequestParam(required = false, defaultValue = "0") int h, HttpServletRequest request, HttpServletResponse response) throws IOException, AWTException {
        return robotUtils.getImageAsString(IMG_SRC, x, y, w > 0 ? w: screenCaptureConfig.getSmall().getWidth(), h>0 ? h: screenCaptureConfig.getSmall().getHeight());
    }

    @RequestMapping(value = "/capture")
    @ResponseStatus(HttpStatus.OK)
    public String fullScreenshot(HttpServletRequest request, HttpServletResponse response) throws IOException, AWTException {
        return robotUtils.getImageAsString(IMG_SRC, 0, 0, screenSizeConfig.getWidth(), screenSizeConfig.getHeight());
    }


    @RequestMapping(value = "/click")
    @ResponseStatus(HttpStatus.OK)
    public  String click(@RequestParam int x, @RequestParam int y, @RequestParam(defaultValue = "1") int count) throws IOException, AWTException, InterruptedException {
        robotUtils.triggerClick(x, y, count);

        return robotUtils.getImageAsString(IMG_SRC, 0, 0, screenSizeConfig.getWidth(), screenSizeConfig.getHeight());
    }

}
