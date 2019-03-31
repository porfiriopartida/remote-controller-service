package com.porfiriopartida.remotecontroller;

import org.junit.Test;

import static org.junit.Assert.*;

public class ThreadHandlerTest {
    @Test
    public void testBuildThreads() throws Exception {
        ThreadHandler handler = new ThreadHandler();
        String testCase = "mytest";
        String namespace = "testing";


        assertEquals(0, handler.getThreadsCount());
        assertNull(handler.getAlwaysRunThread());
        assertNull(handler.getIdentifyThread());
        assertNull(handler.getTestCaseThread());

        handler.runTestCase(namespace, testCase);

        assertEquals(handler.getThreadsCount(), 3);
        assertNotNull(handler.getAlwaysRunThread());
        assertNotNull(handler.getIdentifyThread());
        assertNotNull(handler.getTestCaseThread());
    }
    @Test(expected = Exception.class)
    public void testCannotRunRunningTest() throws Exception {
        ThreadHandler handler = new ThreadHandler();
        String testCase = "mytest";
        String namespace = "testing";

        handler.runTestCase(namespace, testCase);

        handler.runTestCase(namespace, testCase);

        fail("Exception was expected");
    }
    @Test
    public void testStopRunningTest() throws Exception {
        ThreadHandler handler = new ThreadHandler();
        String testCase = "mytest";
        String namespace = "testing";

        handler.runTestCase(namespace, testCase);

        assertTrue("Thread is NOT alive", handler.isAnyThreadRunning());
        assertEquals("Not all threads are running", handler.getRunningThreadsCount(), handler.getThreadsCount());

        handler.stop();

        assertFalse("At least one Thread is STILL RUNNING", handler.isAnyThreadRunning());
        assertNotEquals("At least one Thread is STILL RUNNING", handler.getRunningThreadsCount(), handler.getThreadsCount());
    }
}