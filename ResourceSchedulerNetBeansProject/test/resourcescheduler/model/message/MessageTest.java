package resourcescheduler.model.message;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class MessageTest {

    @Test
    public void testMessagesHaveAGroupId() {
        final List<Long> groupIds = Arrays.asList(3L, 2L, 4L, Long.MIN_VALUE, 7L, -3L, Long.MAX_VALUE, 2L);
        for (Long groupId : groupIds) {
            Message msg = new DummyMessage(groupId);
            assertEquals(groupId, Long.valueOf(msg.getGroupId()));
        }
    }
}
