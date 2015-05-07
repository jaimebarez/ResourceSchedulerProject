package resourcescheduler.model.message;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public abstract class Message {

    private final long groupId;

    public Message(long groupId) {
        this.groupId = groupId;
    }

    public long getGroupId() {
        return groupId;
    }

    public abstract void completed();

}
