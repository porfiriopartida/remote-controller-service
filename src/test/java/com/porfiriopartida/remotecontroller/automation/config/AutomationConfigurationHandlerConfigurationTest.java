package com.porfiriopartida.remotecontroller.automation.config;

import com.porfiriopartida.remotecontroller.automation.AutomationConfigurationHandler;
import com.porfiriopartida.remotecontroller.automation.Step;
import com.porfiriopartida.remotecontroller.utils.image.RobotUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class AutomationConfigurationHandlerConfigurationTest {
    @Autowired
    private AutomationConfigurationHandler handler;
    private RobotUtils robotUtilsMock;
    @Before
    public void init(){
        robotUtilsMock = EasyMock.createStrictMock(RobotUtils.class);
    }
    @Test
    public void testGetStepsNames(){
        String[] expectedArray = new String[]{"open_directory.png", "application_exe.png", "random_popup_close_button.png"};
        Step[] steps = handler.getSteps("my_automated_application", "start_application_test_case");

        assertNotNull("Steps array object must not be null", steps);
        assertEquals(steps.length, expectedArray.length);

        for (int i = 0; i < steps.length; i++) {
            assertTrue(steps[i].isWait());
            assertNotNull(steps[i].getFilenames());
            assertNotNull(steps[i].getFilenames()[0]);
            assertEquals(steps[i].getFilenames()[0], expectedArray[i]);
        }
    }
    @Test
    public void testGetStepsNamesMultipleImages(){
        String[] expectedArray = new String[]{"open_directory.png,open_directory2.png", "application_exe.png", "random_popup_close_button.png,random_popup_close_button2.png"};
        Step[] steps = handler.getSteps("my_automated_application", "multi_image_test");

        assertNotNull("Steps array object must not be null", steps);
        assertEquals(steps.length, expectedArray.length);

        for (int i = 0; i < steps.length; i++) {
            assertTrue(steps[i].isWait());
            String[] multipleImagesStep =  expectedArray[i].split(",");
            assertNotNull(steps[i].getFilenames());
            assertNotNull(steps[i].getFilenames()[0]);
            assertEquals(steps[i].getFilenames()[0], multipleImagesStep[0]);
            if(steps[i].getFilenames().length == 2){
                assertEquals(steps[i].getFilenames()[1], multipleImagesStep[1]);
            }
        }
    }
    @Test
    public void testGetStepsAlwaysClick(){
        String[] expectedArray = new String[]{"open_directory.png", "application_exe.png", "random_popup_close_button.png", "true_again.png"};
        boolean[] expectedBooleans = new boolean[]{true, true, false, true};

        Step[] steps = handler.getSteps("my_automated_application", "always_click_test");

        for (int i = 0; i < steps.length; i++) {
            assertNotNull(String.format("Step %s got a null filename array.", i), steps[i].getFilenames());
            assertNotNull(String.format("Step %s got a null filename.", i), steps[i].getFilenames()[0]);
            assertEquals(String.format("Step %s failed to compare booleans.", i), steps[i].isWait(), expectedBooleans[i]);
            assertEquals(String.format("Step %s failed to compare filenames.", i), steps[i].getFilenames()[0], expectedArray[i]);
        }
    }
    @Test
    public void testGetStepsNamesMultipleImagesWithAlwaysClick(){
        String[] expectedArray = new String[]{"open_directory.png,open_directory2.png", "application_exe.png", "random_popup_close_button.png,random_popup_close_button2.png"};
        Step[] steps = handler.getSteps("my_automated_application", "multi_image_always_click_test");
        boolean[] expectedBooleans = new boolean[]{true, false, true};

        assertNotNull("Steps array object must not be null", steps);
        assertEquals(steps.length, expectedArray.length);

        for (int i = 0; i < steps.length; i++) {
            String[] multipleImagesStep =  expectedArray[i].split(",");
            assertNotNull(String.format("Step %s got a null filename array.", i), steps[i].getFilenames());
            assertNotNull(String.format("Step %s got a null filename.", i), steps[i].getFilenames()[0]);
            assertEquals(steps[i].getFilenames()[0], multipleImagesStep[0]);
            if(steps[i].getFilenames().length == 2){
                assertEquals(String.format("Step %s failed to compare the second filename.", i),steps[i].getFilenames()[1], multipleImagesStep[1]);
            }

            assertEquals(String.format("Step %s failed to compare booleans.", i), steps[i].isWait(), expectedBooleans[i]);
        }
    }
    @Test
    public void testGetStepsNamesManyMultipleImagesWithAlwaysClick(){
        String[] expectedArray = new String[]{"open_directory.png,open_directory2.png,open_directory3.png", "application_exe.png", "random_popup_close_button.png,random_popup_close_button2.png", "any1.png,any2.png,any3.png,any4.png,any5.png", "single.png"};
        Step[] steps = handler.getSteps("my_automated_application", "many_multi_image_always_click_test");
        boolean[] expectedBooleans = new boolean[]{true, false, true, false, true};

        assertNotNull("Steps array object must not be null", steps);
        assertEquals(steps.length, expectedArray.length);

        for (int i = 0; i < steps.length; i++) {
            String[] multipleImagesStep =  expectedArray[i].split(",");
            assertNotNull(String.format("Step %s got a null filename array.", i), steps[i].getFilenames());
            int totalStepImages = steps[i].getFilenames().length;
            for (int j = 0; j < totalStepImages; j++) {
                assertEquals(String.format("Step %s failed to compare the second filename.", i),steps[i].getFilenames()[j], multipleImagesStep[j]);
            }
            assertEquals(String.format("Step %s failed to compare booleans.", i), steps[i].isWait(), expectedBooleans[i]);
        }
    }

    @Test
    public void testExternalResourcesPath() throws FileNotFoundException {
        String f = handler.getResourceFilename("test.txt");
        assertEquals("C:\\Windows\\test.txt", f);
    }
}