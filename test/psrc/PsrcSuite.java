/*
 * Copyright Heathman Innovative Solutions Aug 1, 2014.
 */

package psrc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Brad
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({psrc.logic.LogicSuite.class, psrc.kernel.KernelSuite.class, psrc.core.CoreSuite.class})
public class PsrcSuite {
    
}
