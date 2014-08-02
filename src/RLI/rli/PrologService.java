package rli.rli;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Brad
 */
public interface PrologService extends Remote {

    /**
     *
     * @param goal
     * @return
     * @throws RemoteException
     */
    public Object serverCallProlog(Object goal) throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public Object rli_in() throws RemoteException;

    /**
     *
     * @param T
     * @throws RemoteException
     */
    public void rli_out(Object T) throws RemoteException;
}
