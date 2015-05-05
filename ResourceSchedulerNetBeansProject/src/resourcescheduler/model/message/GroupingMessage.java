package resourcescheduler.model.message;

/**
 *
 * @author jaime.barez.lobato
 */
public class GroupingMessage implements Message {

    private final long groupId;

    public GroupingMessage(long groupId) {
        this.groupId = groupId;
    }

    public long getGroupId() {
        return groupId;
    }

    @Override
    public void completed() {

    }

}
