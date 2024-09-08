package ru.practicum;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class MainServiceAppTest
        extends TestCase {

    public MainServiceAppTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(MainServiceAppTest.class);
    }

    public void testApp() {
        assertTrue(true);
    }
}
