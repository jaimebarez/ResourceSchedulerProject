package resourcescheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.IntegerExpression;
import static javafx.beans.binding.IntegerExpression.integerExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import resourcescheduler.model.gateway.CompleteMsgNotifyingGateway;
import resourcescheduler.model.gateway.Gateway;
import resourcescheduler.model.gateway.MessageCompletedListener;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceScheduler {

    private final IntegerProperty idleResources, busyResources, desiredResources;
    private final IntegerExpression totalRealResources, availableResources;

    private final CompleteMsgNotifyingGateway gateway;
    private final List<Message> unsentMessagesList;
    private final Object mutex = new Object();

    public ResourceScheduler(CompleteMsgNotifyingGateway gateway) {
        this.gateway = gateway;

        this.idleResources = new SimpleIntegerProperty(0);
        this.busyResources = new SimpleIntegerProperty(0);
        this.desiredResources = new SimpleIntegerProperty(0);

        this.totalRealResources = integerExpression(idleResources.add(busyResources));
        this.availableResources = integerExpression(desiredResources.subtract(totalRealResources));

        this.unsentMessagesList = new LinkedList<>();

        gateway.addOnMessageCompletedListener(new ResourceSchedulerMessageCompletedListener());

    }

    /**
     *
     * @return The number of available resources. Negative number if we set less
     * resources quantity than the number of the current busy resources
     */
    public int getAvailableResourcesQuantity() {

        synchronized (mutex) {
            return availableResources.intValue();
        }
    }

    public int getDesiredResourcesQuantity() {
        synchronized (mutex) {
            return this.desiredResources.intValue();
        }
    }

    public void setDesiredResourcesQuantity(int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Desired resources quantity must not be <0");
        }
        synchronized (mutex) {
            this.desiredResources.set(quantity);
            tryToSendMessages();
        }
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
                    int avRQ = getAvailableResourcesQuantity();
                    available = avRQ > 0;
                    System.out.println("av   "+avRQ+" "+available);
                    if (available) {
                        System.out.println("!");
                        increment(busyResources, 1);
                        System.out.println(unsentMessagesList.size());
                        unsentMessagesList.remove(0);
                        System.out.println(unsentMessagesList.size());
                        gateway.send(nextUnsentMsg);

                    }
                }

            }
            tryingToSendMessage.set(false);
        }
    }

    private static void increment(IntegerProperty intProp, int increment) {
        intProp.set(intProp.get() + increment);
    }

    private class ResourceSchedulerMessageCompletedListener implements MessageCompletedListener {

        public ResourceSchedulerMessageCompletedListener() {

        }

        @Override
        public void notifyMessageCompleted(Message msg) {
            System.out.println("Notifycompleted");
            synchronized (mutex) {
                if (getAvailableResourcesQuantity() >= 0) {
                    increment(idleResources, 1);
                    tryToSendMessages();
                }
            }

        }
    }

}
