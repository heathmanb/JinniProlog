package prolog.core;

import java.io.*;
import java.net.*;
import prolog.logic.Stateful;
import prolog.kernel.JavaIO;

/**
 * Lightweight Prolog shell client (that could be made self-contained if needed)
 * that works like a remote shell allowing users to control and possible share a
 * Prolog "server".
 */
public final class ShellClient implements Stateful {

    private LineNumberReader R;
    private PrintWriter W;

    /**
     *
     * @param host
     * @param port
     * @throws Exception
     */
    public ShellClient(String host, int port) throws Exception {
        Socket socket = new Socket(host, port);
        init_connection(socket);
    }

    /**
     *
     * @param inputStream
     * @return
     */
    public LineNumberReader toReader(InputStream inputStream) {
        //return new LineNumberReader(new InputStreamReader(inputStream));
        return JavaIO.toReader(inputStream);
    }

    /**
     *
     * @param f
     * @return
     */
    public static PrintWriter toWriter(OutputStream f) {
        //return new PrintWriter(f,true);
        return JavaIO.toWriter(f);
    }

    /**
     *
     * @param socket
     * @throws Exception
     */
    public void init_connection(Socket socket) throws Exception {
        R = JavaIO.toReader(socket.getInputStream());
        W = JavaIO.toWriter(socket.getOutputStream());
    }

    /**
     *
     */
    public void run() {
        ShellPrinter O = new ShellPrinter(R, toWriter(System.out));
        (new Thread(O, "ShellThread")).start();
        ShellPrinter I = new ShellPrinter(toReader(System.in), W);
        I.run();
    }

    /**
     *
     */
    public static class ShellPrinter implements Runnable {

        ShellPrinter(LineNumberReader from, PrintWriter to) {
            this.from = from;
            this.to = to;
        }

        private LineNumberReader from;
        private PrintWriter to;

        /**
         *
         */
        public void stop() {
            try {
                from.close();
                to.close();
            } catch (IOException e) {
            }
        }

        @Override
        public void run() {
            while (true) {
                int c;
                try {
                    c = from.read();
                } catch (IOException e) {
                    c = -1;
                }
                if (-1 == c) {
                    break;
                }
                to.write(c);
                to.flush();
            }
            stop();
        }
    }
}
