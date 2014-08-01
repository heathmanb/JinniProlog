/*
 * Copyright Heathman Innovative Solutions Aug 1, 2014.
 */

package rli;

import java.rmi.RemoteException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Brad
 */
public class PrologServiceTest {
    
    public PrologServiceTest() {
    }

    /**
     * Test of serverCallProlog method, of class PrologService.
     */
    @Test
    public void testServerCallProlog() throws Exception {
    }

    /**
     * Test of rli_in method, of class PrologService.
     */
    @Test
    public void testRli_in() throws Exception {
    }

    /**
     * Test of rli_out method, of class PrologService.
     */
    @Test
    public void testRli_out() throws Exception {
    }

    public class PrologServiceImpl implements PrologService {

        public Object serverCallProlog(Object goal) throws RemoteException {
            return null;
        }

        public Object rli_in() throws RemoteException {
            return null;
        }

        public void rli_out(Object T) throws RemoteException {
        }
    }
    
}
