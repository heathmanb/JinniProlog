/*
 * Copyright Heathman Innovative Solutions Aug 1, 2014.
 */

package rli;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Brad
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    rli.PrologServiceTest.class, 
    rli.RLIAdaptorTest.class,
    rli.RLIClientTest.class,
    rli.RLIServerTest.class
})
public class RLISuite {
    
}
