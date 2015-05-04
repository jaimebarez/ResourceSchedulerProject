package resourcescheduler;

import java.util.Iterator;
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
        this.unusedResources = new LinkedList<>();
    }
    
    public int getResourcesQuantity() {
        return resourcesQuantity;
    }
    
    public void setResourcesQuantity(int newResourcesQuantity) {
        final int oldResourcesQuantity = this.resourcesQuantity;
        this.resourcesQuantity = newResourcesQuantity;
        
        final int numberOfNewResources = Math.max(0, newResourcesQuantity - oldResourcesQuantity);
        
        for (int i = 0; i < numberOfNewResources; i++) {
            unusedResources.offer(gatewayFactory.createGateway());
        }
        
        Iterator<Message> unsentMsgsIt = unsentMessageQueue.iterator();
        Iterator<Gateway> unusedResourcesIt = unusedResources.iterator();
        
        int numResourcesCouldProcess = numberOfNewResources;
        
        while (numResourcesCouldProcess >= 0 && unsentMsgsIt.hasNext() && unusedResourcesIt.hasNext()) {
            Message nextMessageToSend = unsentMsgsIt.next();
            Gateway nextNewGateway = unusedResourcesIt.next();
            nextNewGateway.send(nextMessageToSend);
            unusedResourcesIt.remove();
            numResourcesCouldProcess--;
        }
        
    }
    
    public void reveiveMessage(Message message) {
        this.unsentMessageQueue.offer(message);
    }
    
    public int getQueuedMessagesCount() {
        return this.unsentMessageQueue.size();
    }
    
}
