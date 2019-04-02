package com.porfiriopartida.remotecontroller;

import com.porfiriopartida.remotecontroller.automation.Step;
import com.porfiriopartida.remotecontroller.utils.image.RobotUtils;
import org.easymock.EasyMock;
import org.junit.Test;

import static org.junit.Assert.*;

public class ThreadHandlerTest {
    @Test
    public void testSetupThreads() throws Exception {
        ThreadHandler handler = new ThreadHandler();

        assertEquals(0, handler.getThreadsCount());
        assertNull(handler.getAlwaysRunThread());
        assertNull(handler.getTestCaseThread());

        handler.setup(getAlwaysClickTestData(), getStepsTestData());

        assertEquals(handler.getThreadsCount(), 2);
        assertNotNull(handler.getAlwaysRunThread());
        assertNotNull(handler.getTestCaseThread());
    }
    @Test(expected = Exception.class)
    public void testCannotRunRunningTest() throws Exception {
        ThreadHandler handler = buildThreadHandler();
        handler.runTestCase();
        handler.runTestCase();
        fail("Exception was expected");
    }
    @Test
    public void testStopRunningTest() throws Exception {
        ThreadHandler handler = buildThreadHandler();

        handler.runTestCase();

        assertTrue("Thread is NOT alive", handler.isAnyThreadRunning());
        assertEquals("Not all threads are running", handler.getRunningThreadsCount(), handler.getThreadsCount());

        handler.stopRun();

        assertFalse("At least one Thread is STILL RUNNING", handler.isAnyThreadRunning());
        assertNotEquals("At least one Thread is STILL RUNNING", handler.getRunningThreadsCount(), handler.getThreadsCount());
    }
    @Test(expected = TestCaseValidationException.class)
    public void testEmptyValidation() throws TestCaseValidationException {
        ThreadHandler handler = new ThreadHandler();
        handler.validate();
    }
    @Test(expected = TestCaseValidationException.class)
    public void testValidation() throws Exception {
        ThreadHandler handler = new ThreadHandler();
        handler.runTestCase();
    }

    @Test
    public void testAlwaysClickThread() throws Exception {
        robotUtilsMock = EasyMock.createStrictMock(RobotUtils.class);
        String[] testAlwaysClick  = getAlwaysClickTestData();
        EasyMock.expect(robotUtilsMock.clickOnScreen(false, testAlwaysClick)).andReturn(true).anyTimes();

        ThreadHandler handler = new ThreadHandler();
        Step[] testCaseSteps  = getStepsTestData();
        handler.setup(testAlwaysClick, testCaseSteps);

        handler.setRobotUtils(robotUtilsMock);

        EasyMock.replay(robotUtilsMock);
        handler.runTestCase();
        while(handler.isAnyThreadRunning()){
            //Give a lock until all threads are released.
        }
        EasyMock.verify(robotUtilsMock);
    }

    @Test
    public void testStepsThread() throws Exception {
        robotUtilsMock = EasyMock.createStrictMock(RobotUtils.class);
        String[] testAlwaysClick  = new String[]{};
        Step step = new Step();
        step.setFilenames("MyTestFilename");

        EasyMock.expect(robotUtilsMock.clickOnScreen(step.isWait(), step.getFilenames())).andReturn(true).times(1);

        ThreadHandler handler = new ThreadHandler();

        Step[] testCaseSteps  = new Step[]{step};

        handler.setup(testAlwaysClick, testCaseSteps);

        handler.setRobotUtils(robotUtilsMock);

        EasyMock.replay(robotUtilsMock);
        handler.runTestCase();
        while(handler.isAnyThreadRunning()){
            //Give a lock until all threads are released.
        }
        EasyMock.verify(robotUtilsMock);

    }

    private String[] getAlwaysClickTestData(){
        return new String[] {"always_click.png"};
    }
    private Step[] getStepsTestData() {
        return new Step[] {
                new Step()
        };
    }
    private RobotUtils robotUtilsMock;
    private ThreadHandler buildThreadHandler() throws Exception {
        ThreadHandler handler = new ThreadHandler();
        String[] testAlwaysClick  = getAlwaysClickTestData();
        Step[] testCaseSteps  = getStepsTestData();
        handler.setup(testAlwaysClick, testCaseSteps);

        robotUtilsMock = EasyMock.createStrictMock(RobotUtils.class);
        handler.setRobotUtils(robotUtilsMock);
        return handler;
    }

}