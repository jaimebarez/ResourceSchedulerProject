/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcescheduler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import resourcescheduler.model.gateway.fakeimplementations.InstantProcessingGateway;
import resourcescheduler.model.message.GroupingMessage;
import resourcescheduler.model.message.Message;

/**
 *
 * @author jaime.barez.lobato
 */
public class CancellableResourceSchedulerTest {

    public CancellableResourceSchedulerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCanCancelAGroupOfMessages() {
        final Set<Long> receivedGroups = new HashSet<>();
        InstantProcessingGateway gateway = new InstantProcessingGateway() {

            @Override
            public void send(Message msg) {
                super.send(msg);
                if (msg instanceof GroupingMessage) {
                    receivedGroups.add(((GroupingMessage) msg).getGroupId());
                }
            }

        };
        CancellableResourceScheduler cRsched = new CancellableResourceScheduler(gateway);
        gateway.setDesiredAvailableResources(1);

        final List<Long> groupsToSend = Arrays.asList(1L, 2L, 3L, 4L, 5L);

        for (Long group : groupsToSend) {
            cRsched.receiveMessage(new GroupingMessage(group));
        }
        assertTrue(receivedGroups.containsAll(groupsToSend));

        receivedGroups.clear();
        cRsched.addCancellableGroup(4);

        for (Long group : groupsToSend) {
            cRsched.receiveMessage(new GroupingMessage(group));
        }
        assertFalse(receivedGroups.containsAll(groupsToSend));
        assertFalse(receivedGroups.contains(4L));
        
        //Nothing happens
        receivedGroups.clear();
        cRsched.addCancellableGroup(99);
        for (Long group : groupsToSend) {
            cRsched.receiveMessage(new GroupingMessage(group));
        }
        assertFalse(receivedGroups.containsAll(groupsToSend));
        assertFalse(receivedGroups.contains(4L));
        
        receivedGroups.clear();
        cRsched.addCancellableGroup(3);
        for (Long group : groupsToSend) {
            cRsched.receiveMessage(new GroupingMessage(group));
        }
        assertFalse(receivedGroups.containsAll(groupsToSend));
        assertFalse(receivedGroups.contains(4L));
        assertFalse(receivedGroups.contains(3L));
    }

}
