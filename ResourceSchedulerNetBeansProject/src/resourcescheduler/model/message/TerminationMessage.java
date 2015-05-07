package resourcescheduler.model.message;

/**
 *
 * @author Jaime Bárez Lobato - jaimebarez@gmail.com
 */
public abstract class TerminationMessage extends Message {

    public TerminationMessage(long groupId) {
        super(groupId);
    }
}
