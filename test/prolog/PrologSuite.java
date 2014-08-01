/*
 * Copyright Heathman Innovative Solutions Aug 1, 2014.
 */

package prolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Brad
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({prolog.logic.LogicSuite.class, prolog.kernel.KernelSuite.class, prolog.core.CoreSuite.class})
public class PrologSuite {
    
}
