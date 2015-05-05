
package resourcescheduler.model.message;

/**
 *
 * @author jaime.barez.lobato
 */
public class DummyMessage implements Message {

    @Override
    public void completed() {
        System.out.println("MSG Completed");
    }
}
