package resourcescheduler.model;

import resourcescheduler.model.message.MessageFactory;
import resourcescheduler.model.message.Message;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jaimebarez
 */
public class MessageFactoryTest {

    public MessageFactoryTest() {
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
    public void testCreateDummyMessage() {
        Message result = MessageFactory.createDummyMessage();
        assertNotNull(result);
    }

}
