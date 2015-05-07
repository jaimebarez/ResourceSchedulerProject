package resourcescheduler.model.message;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public class DummyMessage extends Message {

    private static final long dummyId = -1l;

    public DummyMessage(long groupId) {
        super(groupId);
    }

    public DummyMessage() {
        super(dummyId);
    }

    @Override
    public void completed() {
        //Do nothing
    }
}
