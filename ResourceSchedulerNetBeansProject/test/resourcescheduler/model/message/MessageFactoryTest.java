package resourcescheduler.model.message;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class MessageFactoryTest {

    @Test
    public void testCreateDummyMessage() {
        Message result = MessageFactory.createDummyMessage();
        assertNotNull(result);
    }
}
