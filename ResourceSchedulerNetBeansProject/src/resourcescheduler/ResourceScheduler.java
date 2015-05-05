package resourcescheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import resourcescheduler.model.gateway.AvailableResourcesListener;
import resourcescheduler.model.gateway.CompleteGateway;
import resourcescheduler.model.gateway.MessageCompletedListener;
import resourcescheduler.model.message.GroupingMessage;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceScheduler {

    private final CompleteGateway gateway;
    private final PriorityQueue<Message> unsentMessagesQueue;
    private final Object mutex = new Object();
    private final MessageGroupsManager messageGroupsManager;

    public ResourceScheduler(CompleteGateway gateway) {
        this.gateway = gateway;

        this.messageGroupsManager = new MessageGroupsManager();
        this.unsentMessagesQueue = new PriorityQueue<>(messageGroupsManager.getGroupsReceiveOrderComparator());

        gateway.addOnMessageCompletedListener(new OnMsgCompleteTryToSendMessages());
        gateway.addAvailableResourcesListener(new OnAvailableResourcesTryToSendMessages());
    }

    public void reveiveMessage(Message msg) {
        receiveMessage(msg, Long.MIN_VALUE);
    }

    public void receiveMessage(GroupingMessage msg) {
        receiveMessage(msg, msg.getGroupId());
    }

    private void receiveMessage(Message msg, long groupId) {
        synchronized (mutex) {
            messageGroupsManager.register(msg, groupId);
            unsentMessagesQueue.add(msg);
        }
        tryToSendMessages();
    }

    public int getQueuedMessagesCount() {
        synchronized (mutex) {
            return this.unsentMessagesQueue.size();
        }
    }

    private final AtomicBoolean tryingToSendMessage = new AtomicBoolean(false);

    private void tryToSendMessages() {
        if (tryingToSendMessage.compareAndSet(false, true)) {

            boolean keepGoing = true;

            boolean available, notEmptyQueue;

            while (keepGoing) {
                synchronized (mutex) {

                    int availableResources = gateway.getAvailableResources();
                    available = availableResources > 0;
                    notEmptyQueue = !unsentMessagesQueue.isEmpty();

                    keepGoing = available && notEmptyQueue;

                    if (keepGoing) {

                        Message nextUnsentMsg = unsentMessagesQueue.poll();
                        sendMessageToWateway(nextUnsentMsg);

                    }
                }
            }
            tryingToSendMessage.set(false);
        }
    }

    protected void sendMessageToWateway(Message nextUnsentMsg) {
        gateway.send(nextUnsentMsg);
    }

    private class OnAvailableResourcesTryToSendMessages implements AvailableResourcesListener {

        @Override
        public void resourcesBecameAvailable() {
            tryToSendMessages();
        }
    }

    private class OnMsgCompleteTryToSendMessages implements MessageCompletedListener {

        @Override
        public void notifyMessageCompleted(Message msg) {

            messageGroupsManager.unregister(msg);

            tryToSendMessages();

        }
    }

    private class MessageGroupsManager {

        private final Map<Message, Long> messageGroupMap;
        private final Map<Long, Long> groupsOrder;
        private long lastGivenOrder;

        public MessageGroupsManager() {
            messageGroupMap = new HashMap<>();
            groupsOrder = new HashMap<>();
            lastGivenOrder = Long.MAX_VALUE;
        }

        private long unregister(Message msg) {
            synchronized (mutex) {
                return messageGroupMap.remove(msg);
            }
        }

        private void register(Message msg, long groupId) {
            synchronized (mutex) {
                messageGroupMap.put(msg, groupId);
                if (!groupsOrder.containsKey(groupId)) {
                    groupsOrder.put(groupId, lastGivenOrder--);
                }
            }
        }

        private long getGroupId(Message msg) {
            return messageGroupMap.get(msg);
        }

        private long getGroupOrder(long groupId) {
            return groupsOrder.get(groupId);
        }

        private long getGroupOrder(Message msg) {
            return getGroupOrder(getGroupId(msg));
        }

        public Comparator<Message> getGroupsReceiveOrderComparator() {
            return new Comparator<Message>() {

                @Override
                public int compare(Message m1, Message m2) {
                    long g1 = getGroupOrder(m1);
                    long g2 = getGroupOrder(m2);
                    return Long.compare(g2, g1);
                }
            };
        }

    }

}
