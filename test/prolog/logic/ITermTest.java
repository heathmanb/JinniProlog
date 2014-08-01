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
public class ITermTest {
    
    public ITermTest() {
    }

    /**
     * Test of putVar method, of class ITerm.
     */
    @Test
    public void testPutVar() throws Exception {
    }

    /**
     * Test of putConst method, of class ITerm.
     */
    @Test
    public void testPutConst() throws Exception {
    }

    /**
     * Test of putString method, of class ITerm.
     */
    @Test
    public void testPutString() throws Exception {
    }

    /**
     * Test of putInt method, of class ITerm.
     */
    @Test
    public void testPutInt() throws Exception {
    }

    /**
     * Test of putFloat method, of class ITerm.
     */
    @Test
    public void testPutFloat() throws Exception {
    }

    /**
     * Test of putFun method, of class ITerm.
     */
    @Test
    public void testPutFun() throws Exception {
    }

    /**
     * Test of putObject method, of class ITerm.
     */
    @Test
    public void testPutObject() throws Exception {
    }

    /**
     * Test of getTerm method, of class ITerm.
     */
    @Test
    public void testGetTerm() throws Exception {
    }

    public class ITermImpl implements ITerm {

        public int putVar(Object id) throws PrologException {
            return 0;
        }

        public int putConst(String c) throws PrologException {
            return 0;
        }

        public int putString(String s) throws PrologException {
            return 0;
        }

        public int putInt(int i) throws PrologException {
            return 0;
        }

        public int putFloat(double d) throws PrologException {
            return 0;
        }

        public int putFun(String f, int[] args) throws PrologException {
            return 0;
        }

        public int putObject(Object o) throws PrologException {
            return 0;
        }

        public Object getTerm(int xref, OTerm O) throws PrologException {
            return null;
        }
    }
    
}
