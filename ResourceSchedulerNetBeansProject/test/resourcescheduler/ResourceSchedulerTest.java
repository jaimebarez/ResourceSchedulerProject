package resourcescheduler;

import junit.framework.Assert;
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
public class ResourceSchedulerTest {
    
    private ResourceScheduler resourceScheduler;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.resourceScheduler = new ResourceScheduler();
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void testCanConfigureResourcesQuantity() {
        int exampleResourcesNumber = 2;
        resourceScheduler.setResourcesQuantity(exampleResourcesNumber);
        
        Assert.assertEquals(exampleResourcesNumber, resourceScheduler.getResourcesQuantity());
    }
    
}
