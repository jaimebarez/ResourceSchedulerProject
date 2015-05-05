/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcescheduler.model.message;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jaime.barez.lobato
 */
public class GroupingMessageTest {

    public GroupingMessageTest() {
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
    public void testMessagesHaveGroupId() {
        System.out.println("testGetGroupId");

        for (long l : new long[]{Long.MAX_VALUE, Long.MIN_VALUE, 0, 10, -10}) {
            GroupingMessage gm = new GroupingMessage(l);
            assertEquals(gm.getGroupId(), l);
        }
    }
}
