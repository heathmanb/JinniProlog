package RLI.rli;

import java.rmi.NotBoundException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import prolog.kernel.Top;
import prolog.kernel.Machine;
import prolog.logic.Prolog;
import prolog.kernel.PrologReader;
import prolog.kernel.PrologWriter;
import prolog.logic.ObjectDict;
import prolog.core.Hub;

public class RLIServer implements PrologService {

    private final static ObjectDict dict = new ObjectDict();

    private static int lastid = 0;

    private String portName;

    private Prolog prolog;

    private PrologReader reader;

    private PrologWriter writer;

    private Machine machine;

    private int id;

    private Hub hub;

    public RLIServer(String portName, Prolog prolog, PrologReader reader,
            PrologWriter writer) {
        this.portName = portName;
        this.prolog = prolog;
        this.reader = reader;
        this.writer = writer;
        this.machine = null;
        assignId();
    }

    public String getPort() {
        return portName;
    }

    private void assignId() {
        Integer I = (Integer) dict.get(getPort());
        if (null == I) {
            this.id = ++lastid;
            dict.put(getPort(), id);
        } else {
            this.id = I;
        }
    }

    /**
     * Equivalent to rmiregistry.exe creates a registry if not already created
     */
    public void create_registry() {
        try {
            System.setProperty("java.rmi.server.codebase",
                    "file:/bin/prolog.jar");
            java.rmi.registry.LocateRegistry.createRegistry(
                    Registry.REGISTRY_PORT);
        } catch (RemoteException re) {
            System.err.println("Registry Creation error: " + re);
        }
    }

    public int start() {
        create_registry();
        try { // ?? maybe the stub needs to be returned?
            PrologService stub = (PrologService) UnicastRemoteObject
                    .exportObject(this, 0);

            // (Re)Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(this.portName, stub);

            // System.err.println("Starting Server: " + this.portName);
        } catch (RemoteException e) {
            System.err.println(
                    "Server start exception: " + e.toString()
                    + ",port=" + portName);
            if (RLIAdaptor.trace) {
                Logger.getAnonymousLogger().log(Level.SEVERE,
                        "RLIClient.rli_out: port=" + portName,
                        e);
            }
            System.err.println("Please make sure you have started rmiregistry");
            return -1;
        }
        if (null == hub) {
            hub = new Hub(0L);
        }
        return this.id;
    }

    @Override
    public Object rli_in() {
        return hub.collect();
    }

    @Override
    public void rli_out(Object T) {
        hub.putElement(T);
    }

    public boolean bg_stop() {
        System.err.println("Stopping Server: " + this.portName);
        try {
            Registry registry = LocateRegistry.getRegistry();
            registry.unbind(this.portName);
            return UnicastRemoteObject.unexportObject(this, true);
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Server stop exception: " + e);
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    "RLIServer.bg_stop",
                    e);
            return false;
        }
    }

    @Override
    public Object serverCallProlog(Object query) {
        Object R;
        //// if(true) return R; // exhibits genuine RMI memory leak !!!

        if (null == query) { // this stops server
            bg_stop();
            return null;
        }

        if (null == this.prolog) {
            this.prolog = Prolog.getDefaultProlog();
        }

        // if machine is reused, new tasks will stop previous tasks - unexpected
        // behavior !!!
        if (machine != null) {
            machine.stop(); // make sure no memory leaks !!!
        }
        machine = Top.new_machine(this.prolog, reader, writer); // cached
        machine.set_instance_id(this.id); // it seems to be the case anyway ???
        R = machine.query_engine(query); // does work on server

        return R;
    }
}
