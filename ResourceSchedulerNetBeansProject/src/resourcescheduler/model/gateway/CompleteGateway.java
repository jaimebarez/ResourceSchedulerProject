package resourcescheduler.model.gateway;

import java.util.LinkedList;
import java.util.List;
import resourcescheduler.model.message.Message;

/**
 *
 * @author jaime.barez.lobato
 */
public abstract class CompleteGateway implements Gateway {

    private final List<MessageCompletedListener> messageCompleteListeners;
    private final List<AvailableResourcesListener> availableResourcesListeners;

    private int busyResources, desiredAvailableResources;

    private final Object mutex = new Object();

    public CompleteGateway() {
        this.messageCompleteListeners = new LinkedList<>();
        this.availableResourcesListeners = new LinkedList<>();

        this.busyResources = 0;
        this.desiredAvailableResources = 0;
        addOnMessageCompletedListener(new MessageCompletedListener() {

            @Override
            public void notifyMessageCompleted(Message msg) {
                synchronized (mutex) {
                    busyResources--;
                    
                }
                realAvailableResourcesMayHaveChanged();
            }
        });
    }

    @Override
    public void send(Message msg) {
        synchronized (mutex) {
            busyResources++;
        }
    }

    private void fireMessageCompleted(Message msg) {
        for (MessageCompletedListener mCL : messageCompleteListeners) {
            mCL.notifyMessageCompleted(msg);
        }
    }

    public final boolean addOnMessageCompletedListener(MessageCompletedListener mCL) {
        return messageCompleteListeners.add(mCL);
    }

    public final boolean removeOnMessageCompletedListener(MessageCompletedListener mCL) {
        return messageCompleteListeners.remove(mCL);
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

    public int getAvailableResources() {

        return Math.max(0, getRealAvailableResources());
    }

    private int getRealAvailableResources() {
        synchronized (mutex) {
            return desiredAvailableResources - busyResources;
        }
    }

    /**
     * @return the busyResources
     */
    public int getBusyResources() {
        synchronized (mutex) {
            return busyResources;
        }
    }

    /**
     * @return the desiredAvailableResources
     */
    public int getDesiredAvailableResources() {
        synchronized (mutex) {
            return desiredAvailableResources;
        }
    }

    /**
     * @param desiredAvailableResources the desiredAvailableResources to set
     */
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

    protected void setMessageCompleted(Message msg) {
        msg.completed();
        fireMessageCompleted(msg);
    }
}
