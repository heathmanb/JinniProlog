package RLI.rli;

import java.rmi.AccessException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RLIClient {

    public static int rli_wait(String host, String portName, int timeout) {
        boolean bound = false;
        boolean forever = (0 == timeout);
        int duration = -1;
        while (!bound && (forever || timeout > 0)) {
            if (RLIAdaptor.trace) {
                System.out.println(
                        "waiting for:" + portName
                        + ",timeout=" + timeout);
            }
            duration = (duration + 1) % 4;
            int sleeptime = duration + 1;
            if (sleeptime > timeout) {
                sleeptime = timeout;
            }
            timeout = timeout - sleeptime;

            if (RLIAdaptor.cacheServers && "localhost".equals(host)
                    && null != RLIAdaptor.InnerServers.get(portName)) {
                bound = true;
                break;
            }

            try {
                Object answer = callPrologServer(host, portName, "true");
                if (RLIAdaptor.trace) {
                    System.out.println(
                            "got from server:" + portName
                            + "=>" + answer);
                }
                if (null != answer) {
                    bound = true;
                }
            } catch (ConnectException | NotBoundException e) {
                rliSleep(sleeptime);
            } catch (RemoteException e) {
                // includes AccessException, RemoteException - they indicate
                // programming errors
                System.err.println(
                        "Error in rli_wait: " + e.toString()
                        + ",port=" + portName);
                // rliSleep(sleeptime);
                break;
            }

        }
        return bound ? 1 : 0;
    }

    private static Object callPrologServer(
            String host, String portName, Object goal)
            throws ConnectException, NotBoundException, AccessException,
            RemoteException {
        Registry registry = LocateRegistry.getRegistry(host);
        PrologService stub = (PrologService) registry.lookup(portName);
        return stub.serverCallProlog(goal);
    }

    private static void rliSleep(int secs) {
        if (RLIAdaptor.trace) {
            System.out.println("sleeping for:" + secs);
        }
        try {
            Thread.sleep(1000L * secs);
        } catch (InterruptedException ie) {
        }
        if (RLIAdaptor.trace) {
            System.out.println("slept for:" + secs);
        }
    }

    public static Object clientPrologCall(
            String host, String portName, Object goal) {
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            PrologService stub = (PrologService) registry.lookup(portName);
            Object answer = stub.serverCallProlog(goal);
            return answer;
        } catch (RemoteException | NotBoundException e) {

            Logger.getAnonymousLogger().log(Level.SEVERE,
                    "RLIClient.clientPrologCall: port=" + portName
                    + ",CALLING=>" + goal, e);
            return null;
        }
    }

    public static Object rli_in(String host, String portName) {
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            PrologService stub = (PrologService) registry.lookup(portName);
            Object answer = stub.rli_in();
            return answer;
        } catch (RemoteException | NotBoundException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    "RLIClient.rli_in: port=" + portName,
                    e);
            return null;
        }
    }

    public static void rli_out(String host, String portName, Object T) {
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            PrologService stub = (PrologService) registry.lookup(portName);
            stub.rli_out(T);
        } catch (RemoteException | NotBoundException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    "RLIClient.rli_out: port=" + portName
                    + ", term=" + T,
                    e);
        }
    }

    public static void stopServer(String host, String portName) {
        clientPrologCall(host, portName, null);
    }
}
