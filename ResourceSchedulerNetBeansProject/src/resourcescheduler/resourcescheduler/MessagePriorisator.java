package resourcescheduler.resourcescheduler;

import java.util.Comparator;
import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public interface MessagePriorisator {

    abstract void register(Message msg);

    abstract void unregister(Message msg);

    public abstract Comparator<Message> getComparator();
}
