package resourcescheduler;

import java.util.LinkedList;
import java.util.Queue;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceScheduler {

    private int resourcesQuantity;
    private final Queue<Message> unsentMessageQueue;

    public ResourceScheduler() {
        this.resourcesQuantity = 0;
        this.unsentMessageQueue = new LinkedList<>();
    }

    public int getResourcesQuantity() {
        return resourcesQuantity;
    }

    public void setResourcesQuantity(int resourcesQuantity) {
        this.resourcesQuantity = resourcesQuantity;
    }

    public void reveiveMessage(Message message) {
        this.unsentMessageQueue.offer(message);
    }

    public int getQueuedMessagesCount() {
        return this.unsentMessageQueue.size();
    }

}
