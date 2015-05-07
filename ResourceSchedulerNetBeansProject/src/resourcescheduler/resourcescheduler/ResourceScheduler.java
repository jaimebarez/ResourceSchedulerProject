package resourcescheduler.resourcescheduler;

import java.util.Collection;
import java.util.HashSet;
import resourcescheduler.resourcescheduler.extras.MessageReceivementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import resourcescheduler.model.gateway.AvailableResourcesListener;
import resourcescheduler.model.gateway.NotifyingGatewayImpl;
import resourcescheduler.model.gateway.MessageCompletedListener;
import resourcescheduler.model.message.Message;
import resourcescheduler.model.message.TerminationMessage;
import resourcescheduler.resourcescheduler.extras.PreviousTermMsgReceivedException;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class ResourceScheduler {

    private final NotifyingGatewayImpl gateway;
    private final Object mutex = new Object();
    private final Set<Long> receivedTerminationGroups;
    private final Collection<Long> cancelledGroups;
    private MessagePriorisator messagePriorisator;
    private Queue<Message> unsentMessagesQueue;

    public ResourceScheduler(NotifyingGatewayImpl gateway) {
        this.gateway = gateway;
        receivedTerminationGroups = new HashSet<>();
        cancelledGroups = new HashSet<>();
        setMessagePriorisator(new MessageFirstGroupsPriorisator());

        gateway.addOnMessageCompletedListener(new OnMsgCompleteTryToSendLeftMessages());
        gateway.addAvailableResourcesListener(new OnAvailableResourcesTryToSendLeftMessages());
    }

    public final void setMessagePriorisator(MessagePriorisator messagePriorisator) {
        this.messagePriorisator = messagePriorisator;
        synchronized (mutex) {
            unsentMessagesQueue = new PriorityQueue<>(this.messagePriorisator.getComparator());
        }
    }

    public final MessagePriorisator getMessagePriorisator() {
        synchronized (mutex) {
            return this.messagePriorisator;
        }
    }

    protected void internalReceiveMessage(Message msg) throws MessageReceivementException {
        long groupId = msg.getGroupId();
        if (!cancelledGroups.contains(groupId)) {

            synchronized (mutex) {
                messagePriorisator.register(msg);
                unsentMessagesQueue.add(msg);
            }
            tryToSendMessages();
        }
    }

    public void addCancellableGroup(long groupId) {

        cancelledGroups.add(groupId);
    }

    public void receiveMessage(TerminationMessage msg) throws MessageReceivementException {

        checkIfMessageTerminated(msg);
        receivedTerminationGroups.add(msg.getGroupId());

        internalReceiveMessage(msg);
    }

    public void receiveMessage(Message msg) throws MessageReceivementException {
        checkIfMessageTerminated(msg);
        internalReceiveMessage(msg);
    }

    private void checkIfMessageTerminated(Message msg) throws PreviousTermMsgReceivedException {
        if (receivedTerminationGroups.contains(msg.getGroupId())) {

            throw new PreviousTermMsgReceivedException();
        }
    }

    public int getQueuedMessagesCount() {
        synchronized (mutex) {
            return this.unsentMessagesQueue.size();
        }
    }

    private void tryToSendMessages() {

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
    }

    protected void sendMessageToWateway(Message nextUnsentMsg) {
        gateway.send(nextUnsentMsg);
    }

    private class OnAvailableResourcesTryToSendLeftMessages implements AvailableResourcesListener {

        @Override
        public void resourcesBecameAvailable() {
            tryToSendMessages();
        }
    }

    private class OnMsgCompleteTryToSendLeftMessages implements MessageCompletedListener {

        @Override
        public void notifyMessageCompleted(Message msg) {
            synchronized (mutex) {
                messagePriorisator.unregister(msg);
            }

            tryToSendMessages();
        }
    }
}
