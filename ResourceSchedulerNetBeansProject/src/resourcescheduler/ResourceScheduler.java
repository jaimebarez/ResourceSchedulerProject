package resourcescheduler;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.beans.binding.IntegerExpression;
import static javafx.beans.binding.IntegerExpression.integerExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import resourcescheduler.model.gateway.Gateway;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class ResourceScheduler {

    private final IntegerProperty idleResources;
    private final IntegerProperty busyResources;
    private final IntegerExpression totalRealResources;
    private final IntegerProperty desiredResources;
    private final IntegerExpression availableResources;

    private final Queue<Message> unsentMessagesQueue;

    private final Gateway gateway;

    public ResourceScheduler(Gateway gateway) {
        this.gateway = gateway;

        this.idleResources = new SimpleIntegerProperty(0);
        this.busyResources = new SimpleIntegerProperty(0);
        this.desiredResources = new SimpleIntegerProperty(0);

        this.totalRealResources = integerExpression(idleResources.add(busyResources));
        this.availableResources = integerExpression(desiredResources.subtract(totalRealResources));

        this.unsentMessagesQueue = new LinkedBlockingQueue<>();
    }

    /**
     *
     * @return The number of available resources. Negative number if we set less
     * resources quantity than the number of the current busy resources
     */
    public int getAvailableResourcesQuantity() {
        return availableResources.intValue();
    }

    public int getDesiredResourcesQuantity() {
        return this.desiredResources.intValue();
    }

    public void setDesiredResourcesQuantity(int quantity) {
        if(quantity<0){
            throw new IllegalArgumentException("Desired resources quantity must not be <0");
        }
        this.desiredResources.set(quantity);
    }

    public void reveiveMessage(Message message) {
        this.unsentMessagesQueue.offer(message);
    }

    public int getQueuedMessagesCount() {
        return this.unsentMessagesQueue.size();
    }

}
