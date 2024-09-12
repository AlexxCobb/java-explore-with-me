package ru.practicum;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class StatServerAppTest
        extends TestCase {

    public StatServerAppTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(StatServerAppTest.class);
    }

    public void testApp() {
        assertTrue(true);
    }
}
