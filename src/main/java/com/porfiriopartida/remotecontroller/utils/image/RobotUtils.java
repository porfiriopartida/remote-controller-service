package com.porfiriopartida.remotecontroller.utils.image;

import com.porfiriopartida.remotecontroller.screen.config.MouseConfig;
import com.porfiriopartida.remotecontroller.screen.config.ScreenSizeConfig;
import com.porfiriopartida.remotecontroller.screen.config.capture.ScreenCaptureConfig;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

@Component
public class RobotUtils {
    private static final String OUT_DIR = "D:\\Java\\remote-controller\\remote-controller\\src\\main\\resources\\out\\";
    private static final String IN_DIR = "D:\\Java\\remote-controller\\remote-controller\\src\\main\\resources\\in\\";
    private static final int TRANSPARENT_PIXEL = -1;
    private Robot robot;
    private static final Logger logger = LogManager.getLogger(RobotUtils.class);

    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.err.println(e.getMessage());
        }
    }

//    public RobotUtils() throws AWTException {
//        robot = new Robot();
//    }

    @Autowired
    private ScreenCaptureConfig screenCaptureConfig;

    public ScreenCaptureConfig getScreenCaptureConfig() {
        return screenCaptureConfig;
    }

    public void setScreenCaptureConfig(ScreenCaptureConfig screenCaptureConfig) {
        this.screenCaptureConfig = screenCaptureConfig;
    }

    public ScreenSizeConfig getSizeConfig() {
        return sizeConfig;
    }

    public void setSizeConfig(ScreenSizeConfig sizeConfig) {
        this.sizeConfig = sizeConfig;
    }

    public MouseConfig getMouseConfig() {
        return mouseConfig;
    }

    public void setMouseConfig(MouseConfig mouseConfig) {
        this.mouseConfig = mouseConfig;
    }

    @Autowired
    private ScreenSizeConfig sizeConfig;

    @Autowired
    private MouseConfig mouseConfig;

    public BufferedImage getImage(int x, int y, int w, int h) throws AWTException {
        return this.getImage(x, y, w, h, false);
    }
    public BufferedImage getImage(int x, int y, int w, int h, boolean centered) throws AWTException {

        Rectangle rectangle;
        int realW =  w > 0 ? w: screenCaptureConfig.getSmall().getWidth();
        int realH =  h > 0 ? h: screenCaptureConfig.getSmall().getHeight();

//        if(centered){
//            x -= realW/2;
//            y -= realH/2;
//        }
//        bufferedImage.getGraphics().drawString("x", w/2, h/2);
        rectangle = new Rectangle(x, y, realW, realH);

        BufferedImage bufferedImage = robot.createScreenCapture(rectangle);


        return bufferedImage;
    }
    public byte[] getImageAsBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }
    public String getImageAsString(byte[] image){
        return Base64.encode(image);
    }

    public String getImageAsString(int x, int y, int w, int h) throws AWTException, IOException {
        return this.getImageAsString("%s", x, y, w, h);
    }
    /**
     *
     * @param imageWrapper IMG_SRC: <img src="data:image/png;base64,%s" />, this method will call String.format(imageWrapper, imageAsString).
     *                     Pass "%s" if you want the raw image byte as astring
     * @param x Coord X of the upper left corner
     * @param y Coord Y of the upper left corner
     * @param w Width of the image
     * @param h Height of the image
     * @return A base64 image string
     *
     * @throws IOException When Robot or image parsing fail.
     */
    public String getImageAsString(String imageWrapper, int x, int y, int w, int h) throws AWTException, IOException {
        BufferedImage bufferedImage = getImage(x, y, w > 0 ? w: screenCaptureConfig.getSmall().getWidth(), h>0 ? h: screenCaptureConfig.getSmall().getHeight());

        String encodedImage = getImageAsString(getImageAsBytes(bufferedImage));

        return String.format(imageWrapper, encodedImage);
    }

    public void triggerClick(int x, int y, int count) throws AWTException, InterruptedException {
        robot.mouseMove(x, y);
        Thread.sleep(500);
        for(int i = 0; i<count && i < mouseConfig.getMaxClicks(); i++){
            robot.mousePress(InputEvent.BUTTON1_MASK);
            Thread.sleep(mouseConfig.getPressDelay());
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            Thread.sleep(500);
        }
    }

    public int[][] getRgbFromImage(String filename) throws IOException {
        BufferedImage bufferedImage = getImage(filename);
        return getRgbFromImage(bufferedImage);
    }
    private HashMap<String, BufferedImage> cache = new HashMap<String, BufferedImage>();
    public BufferedImage getImage(String filename) throws IOException {
        if(cache.get(filename) != null){
            return cache.get(filename);
        }
        BufferedImage newImage = ImageIO.read(new File(getInputFilename(filename)));
        cache.put(filename, newImage);
        return newImage;
    }
    public String getInputFilename(String filename){
        return filename;
    }
    public int[][] getRgbFromImage(BufferedImage bufferedImage) throws IOException {
        int[][] data = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = bufferedImage.getRGB(x, y);
                if(isTransparent(rgb)){
                    data[x][y] = TRANSPARENT_PIXEL;
                } else {
                    data[x][y] = rgb;
                }
            }
        }

        return data;
    }
    public boolean isTransparent( int pixel) {
        if( (pixel>>24) == 0x00 ) {
            return true;
        }
        return false;
    }

    public boolean clickOnScreen(boolean waitUntilPresent, String ... filenames) throws AWTException, InterruptedException, IOException {
        if(!waitUntilPresent){
            for( String filename : filenames){
                boolean result = clickOnScreen(filename);
                if(result){
                    return true;
                }
            }
            return false;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        while(true){
            for( String filename : filenames){
                Point coords = findOnScreen(filename);
                if(coords != null){
                    triggerClick((int)coords.getX(), (int)coords.getY(), 1);
                    return true;
                }
            }
            //TODO: move to configs.
            Thread.sleep(100);
            if(Calendar.getInstance().getTimeInMillis() - now > 60000){
                System.err.println("Image not found after 60 seconds.");
                break;
            }
        }
        return false;
    }
    public boolean clickOnScreen(String filename) throws IOException, AWTException, InterruptedException {
        Point coords = findOnScreen(filename);
        if(coords != null){
            triggerClick((int)coords.getX(), (int)coords.getY(), 1);
            return true;
        }
        return false;
    }

    public Point findOnScreen(String filename) throws IOException, AWTException {
        int width = sizeConfig.getWidth();
        int height = sizeConfig.getHeight();

        BufferedImage fullScreen = getImage(0, 0, width, height);
        BufferedImage subImage = getImage(filename );

//        String uuid = UUID.randomUUID().toString();
//        saveImage(fullScreen, String.format("%s\\full.png", uuid));
//        saveImage(subImage, String.format("%s\\sub.png", uuid));

        int[][] subData = getRgbFromImage(subImage);
//        printPixels(subData, "X", "-");
        int[][] data = getRgbFromImage(fullScreen);

        Point topLeftPoint = matrixContains(data, subData);
        if(topLeftPoint == null){
            return null;
        }
        int centeredX = (int) topLeftPoint.getX() + subImage.getWidth()/2;
        int centeredY = (int) topLeftPoint.getY() + subImage.getHeight()/2;

        Point centeredPoint = new Point( centeredX, centeredY);

        return centeredPoint;
    }

    private void printPixels(int[][] subData, String dataPixel, String emptyPixel) {
        String str = "";
        for(int i=0;i<subData.length;i++){
            for(int j=0;j<subData[0].length;j++) {
                if(subData[i][j] == TRANSPARENT_PIXEL){
                    str += (emptyPixel);
                } else {
                    str += (dataPixel);
                }
            }
            str += "\n";
        }
        logger.debug(str);
    }

    public Point matrixContains(int[][] fullScreenData, int[][] subImageData) throws IOException, AWTException {
        outerRow:
        for (int or = 0; or <= fullScreenData.length - subImageData.length; or++) {
            outerCol:
            for (int oc = 0; oc <= fullScreenData[or].length - subImageData[0].length; oc++) {
                for (int ir = 0; ir < subImageData.length; ir++)
                    for (int ic = 0; ic < subImageData[ir].length; ic++)
                        if (fullScreenData[or + ir][oc + ic] != subImageData[ir][ic])
                        {
                            if(subImageData[ir][ic] != TRANSPARENT_PIXEL){
                                continue outerCol;
                            } else{
                                logger.debug("Matching as transparent pixel.");
                            }
                        }
                return new Point(or, oc);
            }
        }

        return null;
    }
    public void saveImage(BufferedImage bImage, String filename){
        String newFile = OUT_DIR + filename;
        try {
            File f = new File(newFile);
            f.getParentFile().mkdir();
            f.createNewFile();

            ImageIO.write(bImage, "jpg", f);
        } catch (IOException e) {
            logger.error("Exception occured: " + e.getMessage());
        }
        logger.debug("Images were written succesfully.");
    }

    private String getUser() throws IOException, AWTException {
        String[] files = new String[]{"fn_1.png", "fn_2.png", "eyeshield.png" };
        while(true){
            for (int i = 0; i < files.length; i++) {
                Point coords = findOnScreen(files[i]);
                if(coords != null){
                    return files[i];
                }
            }
        }
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }
}
