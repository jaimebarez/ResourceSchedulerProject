package resourcescheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import resourcescheduler.model.gateway.AvailableResourcesListener;
import resourcescheduler.model.gateway.CompleteGateway;
import resourcescheduler.model.gateway.MessageCompletedListener;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceScheduler {

    private final CompleteGateway gateway;
    private final List<Message> unsentMessagesList;
    private final Object mutex = new Object();

    public ResourceScheduler(CompleteGateway gateway) {
        this.gateway = gateway;

        this.unsentMessagesList = new LinkedList<>();

        gateway.addOnMessageCompletedListener(new OnMsgCompleteTryToSendMessages());
        gateway.addAvailableResourcesListener(new OnAvailableResourcesTryToSendMessages());
    }

    public void reveiveMessage(Message message) {
        synchronized (mutex) {
            unsentMessagesList.add(message);
        }
        tryToSendMessages();
    }

    public int getQueuedMessagesCount() {
        synchronized (mutex) {
            return this.unsentMessagesList.size();
        }
    }

    private final AtomicBoolean tryingToSendMessage = new AtomicBoolean(false);

    private void tryToSendMessages() {
        if (tryingToSendMessage.compareAndSet(false, true)) {

            boolean available = true;

            while (available && !unsentMessagesList.isEmpty()) {

                Message nextUnsentMsg = unsentMessagesList.get(0);

                synchronized (mutex) {
                    int availableResources = gateway.getAvailableResources();
                    available = availableResources > 0;

                    if (available) {

                        unsentMessagesList.remove(0);
                        gateway.send(nextUnsentMsg);

                    }
                }
            }
            tryingToSendMessage.set(false);
        }
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

            tryToSendMessages();

        }
    }

}
