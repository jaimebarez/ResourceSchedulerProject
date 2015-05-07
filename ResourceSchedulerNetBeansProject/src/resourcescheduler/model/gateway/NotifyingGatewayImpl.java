package resourcescheduler.model.gateway;

import java.util.LinkedList;
import java.util.List;
import resourcescheduler.model.message.Message;

/**
 * Gateway implementation that notifies relevat events
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public abstract class NotifyingGatewayImpl implements Gateway {

    private final List<MessageCompletedListener> messageCompletedListeners;
    private final List<AvailableResourcesListener> availableResourcesListeners;

    private int busyResources, desiredAvailableResources;

    private final Object mutex;

    public NotifyingGatewayImpl() {
        mutex = new Object();

        this.messageCompletedListeners = new LinkedList<>();
        this.availableResourcesListeners = new LinkedList<>();

        this.busyResources = 0;
        this.desiredAvailableResources = 0;

        addOnMessageCompletedListener(new InternalMessageCompletedNotifier());
    }

    @Override
    public void send(Message msg) {
        synchronized (mutex) {
            busyResources++;
        }
    }

    public int getAvailableResources() {

        return Math.max(0, getRealAvailableResources());
    }

    /**
     * Real available resources can be negative. Negative available resources
     * means that the number of desired available resources was given a lower
     * value than the currently busy resources at that moment. Example: Desired
     * available resources is set to 10. 10 messages are received, and resources
     * are too slow, so they keep processing. Then, the
     * desiredAvailableResources is set to 4. RealAvailableResources will be -6.
     */
    private int getRealAvailableResources() {
        synchronized (mutex) {
            return desiredAvailableResources - busyResources;
        }
    }

    public int getBusyResources() {
        synchronized (mutex) {
            return busyResources;
        }
    }

    public int getDesiredAvailableResources() {
        synchronized (mutex) {
            return desiredAvailableResources;
        }
    }

    public void setDesiredAvailableResources(int desiredAvailableResources) {
        if (desiredAvailableResources < 0) {
            throw new IllegalArgumentException("Negative numbers not allowed");
        }
        synchronized (mutex) {
            this.desiredAvailableResources = desiredAvailableResources;
        }
        realAvailableResourcesMayHaveChanged();
    }

    private void realAvailableResourcesMayHaveChanged() {

        synchronized (mutex) {
            if (getRealAvailableResources() > 0) {
                fireAvailableResources();
            }
        }
    }

    /**
     * An extender class that wants to complete messages and keep the notifying
     * functionality working, must call this.
     *
     * @param msg
     */
    protected void setMessageCompleted(Message msg) {
        msg.completed();
        fireMessageCompleted(msg);
    }

    private void fireMessageCompleted(Message msg) {
        for (MessageCompletedListener mCL : messageCompletedListeners) {
            mCL.notifyMessageCompleted(msg);
        }
    }

    public final boolean addOnMessageCompletedListener(MessageCompletedListener mCL) {
        return messageCompletedListeners.add(mCL);
    }

    public final boolean removeOnMessageCompletedListener(MessageCompletedListener mCL) {
        return messageCompletedListeners.remove(mCL);
    }

    private void fireAvailableResources() {
        for (AvailableResourcesListener aRL : availableResourcesListeners) {
            aRL.resourcesBecameAvailable();
        }
    }

    public final boolean addAvailableResourcesListener(AvailableResourcesListener aRL) {
        return availableResourcesListeners.add(aRL);
    }

    public final boolean removeOnAvailableResourcesListener(AvailableResourcesListener aRL) {
        return availableResourcesListeners.remove(aRL);
    }

    private class InternalMessageCompletedNotifier implements MessageCompletedListener {

        @Override
        public void notifyMessageCompleted(Message msg) {
            synchronized (mutex) {
                busyResources--;
            }
            realAvailableResourcesMayHaveChanged();
        }
    }
}
