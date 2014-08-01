package prolog.core;

import java.io.IOException;
import prolog.kernel.*;
import prolog.logic.*;
import java.net.*;
import static prolog.kernel.JavaIO.toReader;
import static prolog.kernel.JavaIO.toWriter;
import static prolog.logic.Interact.errmes;

/**
 * Allows ShellClient components to connect from remote machines and run
 * commands on a Prolog server.
 */
public class ShellServer extends Transport implements Runnable {

    /**
     *
     * @param args
     * @param port
     * @throws SystemException
     */
    public ShellServer(String args[], int port) throws SystemException {
        super(port);
        this.args = args;
    }

    /**
     *
     * @param port
     * @throws SystemException
     */
    public ShellServer(int port) throws SystemException {
        this(null, port);
    }

    private String[] args;

    @Override
    public void run() {
        for (;;) {
            Transport service = new Transport(this);
            PrologReader R;
            PrologWriter W;
            try {
                Socket socket = service.client_socket;
                R = toReader(socket.getInputStream());
                W = toWriter(socket.getOutputStream());
            } catch (IOException e) {
                errmes("error in ShellServer", e);
                break;
            }

            Shell S = new Shell(args, R, W, false, "?-| ");
            Thread T = new Thread(S, "ShellThread");
            T.setDaemon(true);
            T.start();
        }
    }
}
