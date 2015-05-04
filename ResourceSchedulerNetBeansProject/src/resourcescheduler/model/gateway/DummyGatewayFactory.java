package resourcescheduler.model.gateway;

import resourcescheduler.model.message.Message;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class DummyGatewayFactory extends GatewayAbstractFactory{

    @Override
    public Gateway createGateway() {
        return new Gateway() {

            @Override
            public void send(Message msg) {
                System.out.printf("Message '%s' sent\n", msg);
            }
        };
    }
    
}
