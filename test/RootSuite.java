/*
 * Copyright Heathman Innovative Solutions Aug 1, 2014.
 */

import AGUI.AGUISuite;
import CallProlog.CallPrologSuite;
import GP.GPSuite;
import OLDscripts.OLDscriptsSuite;
import agentgui.AgentguiSuite;
import build.BuildSuite;
import jgp.JgpSuite;
import jgui.JguiSuite;
import jinniprolog.JinniprologSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import prolog.PrologSuite;
import psrc.PsrcSuite;
import tests.TestsSuite;

/**
 * August 1, 2014 1P
 * 1,366 tests passed 10 tests caused an error.
 * BUILD SUCCESSFUL (total time: 2 minutes 26 seconds)
 * 
 * @author Brad
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    JinniprologSuite.class,
    JgpSuite.class,
    OLDscriptsSuite.class, 
    PrologSuite.class, 
    prolog3d.PROLOG3DSuite.class, 
    rli.RLISuite.class, 
    CallPrologSuite.class, 
    TestsSuite.class, 
    BuildSuite.class, 
    PsrcSuite.class, 
    GPSuite.class, 
    AgentguiSuite.class, 
    JguiSuite.class, AGUISuite.class})
public class RootSuite {
    
}
