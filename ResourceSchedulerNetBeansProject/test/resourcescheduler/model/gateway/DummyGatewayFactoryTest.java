package resourcescheduler.model.gateway;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jaime Bárez Lobato
 */
public class DummyGatewayFactoryTest {
    private DummyGatewayFactory dummyGatewayFactory;
    
    public DummyGatewayFactoryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.dummyGatewayFactory = new DummyGatewayFactory();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createGateway method, of class DummyGatewayFactory.
     */
    @Test
    public void testCreateGateway() {
        System.out.println("createGateway");
        
        Gateway dummyGateway = dummyGatewayFactory.createGateway();
        assertNotNull(dummyGateway);
    }
    
}
