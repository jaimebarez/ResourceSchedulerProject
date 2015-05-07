package resourcescheduler.resourcescheduler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import resourcescheduler.model.message.Message;

/**
 * Priorizes messages that came first
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class MessageFirstGroupsPriorisator implements MessagePriorisator {

    private final Map<Message, Long> messageGroupMap;
    private final Map<Long, Long> groupsOrder;
    private long lastGivenOrder;

    public MessageFirstGroupsPriorisator() {
        messageGroupMap = new HashMap<>();
        groupsOrder = new HashMap<>();
        lastGivenOrder = Long.MAX_VALUE;
    }

    @Override
    public void unregister(Message msg) {

        messageGroupMap.remove(msg);
    }

    @Override
    public void register(Message msg) {
        long groupId = msg.getGroupId();
        messageGroupMap.put(msg, groupId);
        if (!groupsOrder.containsKey(groupId)) {
            groupsOrder.put(groupId, lastGivenOrder--);
        }
    }

    private Long getGroupId(Message msg) {
        return messageGroupMap.get(msg);
    }

    private Long getGroupOrder(long groupId) {
        return groupsOrder.get(groupId);
    }

    private Long getGroupOrder(Message msg) {
        Long groupId = getGroupId(msg);

        return groupId == null ? null : getGroupOrder(getGroupId(msg));
    }

    @Override
    public Comparator<Message> getComparator() {
        return new Comparator<Message>() {

            @Override
            public int compare(Message m1, Message m2) {
                Long g1 = getGroupOrder(m1);
                Long g2 = getGroupOrder(m2);
                /*Nulls should never happen, because all compared messages 
                 should be registered first, but we do defensive programming*/
                boolean null1 = g1 == null;
                boolean null2 = g2 == null;
                return null1 && null2 ? 0 : null1 ? -1 : null2 ? 1 : Long.compare(g1, g2);
            }
        };
    }
}
