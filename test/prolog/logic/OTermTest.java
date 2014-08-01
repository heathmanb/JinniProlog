/*
 * Copyright Heathman Innovative Solutions Aug 1, 2014.
 */

package prolog.logic;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Brad
 */
public class OTermTest {
    
    public OTermTest() {
    }

    /**
     * Test of putVar method, of class OTerm.
     */
    @Test
    public void testPutVar() throws Exception {
    }

    /**
     * Test of putConst method, of class OTerm.
     */
    @Test
    public void testPutConst() {
    }

    /**
     * Test of putInt method, of class OTerm.
     */
    @Test
    public void testPutInt() {
    }

    /**
     * Test of putFun method, of class OTerm.
     */
    @Test
    public void testPutFun() {
    }

    /**
     * Test of putFloat method, of class OTerm.
     */
    @Test
    public void testPutFloat() {
    }

    /**
     * Test of getTerm method, of class OTerm.
     */
    @Test
    public void testGetTerm() throws Exception {
    }

    public class OTermImpl implements OTerm {

        public Object putVar(int i) throws PrologException {
            return null;
        }

        public Object putConst(String c) {
            return null;
        }

        public Object putInt(int i) {
            return null;
        }

        public Object putFun(String f, Object[] args) {
            return null;
        }

        public Object putFloat(double d) {
            return null;
        }

        public int getTerm(Object t, ITerm I) throws PrologException {
            return 0;
        }
    }
    
}
