package resourcescheduler;

import java.util.LinkedList;
import java.util.Queue;
import resourcescheduler.model.gateway.Gateway;
import resourcescheduler.model.gateway.GatewayAbstractFactory;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceScheduler {

    private int resourcesQuantity;
    private final Queue<Message> unsentMessageQueue;
    private final Queue<Gateway> unusedResources;
    private final GatewayAbstractFactory gatewayFactory;

    public ResourceScheduler(GatewayAbstractFactory gatewayFactory) {
        this.gatewayFactory = gatewayFactory;

        this.resourcesQuantity = 0;
        this.unsentMessageQueue = new LinkedList<>();
        unusedResources = new LinkedList<>();
    }

    public int getResourcesQuantity() {
        return resourcesQuantity;
    }

    public void setResourcesQuantity(int newResourcesQuantity) {
        final int oldResourcesQuantity = this.resourcesQuantity;
        this.resourcesQuantity = newResourcesQuantity;

        int numberOfNewResource = Math.max(0, newResourcesQuantity - oldResourcesQuantity);

        for (Message unsentMessageQueue1 : unsentMessageQueue) {

        }

    }

    public void reveiveMessage(Message message) {
        this.unsentMessageQueue.offer(message);
    }

    public int getQueuedMessagesCount() {
        return this.unsentMessageQueue.size();
    }

}
