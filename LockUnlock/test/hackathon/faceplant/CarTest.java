/*
 * Copyright 2015, AT&T Intellectual Property. All rights reserved.
 */
package hackathon.faceplant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CarTest {

    private Car instance;

    @Before
    public void setUp() throws Exception {
        instance = new Car("http://tango.hack.att.io:3000", "1XACR15XOTTA00024");

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testUnlock() {

        instance.unlock();
    }

    @Test
    public void testLock() {

        instance.lock();
    }

}
