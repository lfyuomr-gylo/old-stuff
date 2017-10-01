package com.github.lfyuomr.gylo.lamport.mutex;

import com.github.lfyuomr.gylo.lamport.mutex.demo.Demo;
import org.junit.Test;

public class DistributedMutexTest {
    private Demo demo;

    @Test
    public void stressTestTenNodes() throws Exception {
        new Demo("-a=10").run();
    }
}