package prolog.core;

import java.net.*;
import java.io.*;
import prolog.logic.*;

/**
 * Implements TCP/IP Tunneling that allows programs like Prolog HTTP servers to
 * be located behind a firewall and still be exposed through a proxy machine as
 * publicly visible servers.
 */
public class NetTunnel implements Stateful {

    /**
     *
     * @param s
     */
    public static void pp(String s) {
    }

    static final String cusage
            = "client <LinkHost> <LinkPort> <LocalServer> <LocalPort>\n\n";
    static final String susage
            = "server <ServerPort> <LinkServerPort>\n\n";
    static final String hint = "\nUSAGE: Start the following components:\n\n"
            + "1. local server behind firewall or NAT, if needed, on LocalPort\n\n"
            + "2. virtual server visible on the Net, with\n\t"
            + susage
            + "3. client behind firewall or NAT with\n\t"
            + cusage;

    /**
     *
     */
    public static void usage() {
        pp(hint);
    }

    /**
     *
     * @param ServerPort
     * @param LinkServerPort
     * @return
     */
    public static VS server(int ServerPort, int LinkServerPort) {
        VS vs = VS.server(ServerPort, LinkServerPort);
        if (null == vs) {
            usage();
        }
        return vs;
    }

    /**
     *
     * @param LinkHost
     * @param LinkPort
     * @param LocalServer
     * @param LocalPort
     * @return
     */
    public static VC client(
            String LinkHost, int LinkPort,
            String LocalServer, int LocalPort) {
        VC vc = VC.client(LinkHost, LinkPort, LocalServer, LocalPort);
        if (null == vc) {
            usage();
        }
        return vc;
    }

    /**
     *
     */
    public static class Connector implements Runnable {

        private InputStream iFrom = null;
        private OutputStream oTo = null;

        transient private Socket from, to;

        Connector(Socket from, Socket to) {
            this.from = from;
            this.to = to;
        }

        InputStream open_input(Socket client) throws IOException {
            return client.getInputStream();
        }

        OutputStream open_output(Socket client) throws IOException {
            return client.getOutputStream();
        }

        /**
         *
         */
        public void stop() {
            try {
                iFrom.close();
            } catch (IOException i) {
            }
            try {
                oTo.close();
            } catch (IOException i) {
            }
            try {
                from.close();
            } catch (IOException i) {
            }
            try {
                to.close();
            } catch (IOException i) {
            }
            pp("stopped: " + from + "=>" + to);
        }

        @Override
        public void run() {
            byte[] buf = new byte[1 << 16];
            try {
                iFrom = open_input(from);
                oTo = open_output(to);
                pp("CONNECTING: " + from + "=>" + to);
                for (;;) {
                    int n = iFrom.read(buf);
                    //Main.pp("<:"+n);
                    if (n < 0) {
                        break;
                    }
                    oTo.write(buf, 0, n);
                    oTo.flush();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }

            stop();
        }
    }

    /**
     * Virtual Client:
     */
    public static class VC implements Runnable, Stateful {

        VC(String rHost, int rPort, String lHost, int lPort) {
            this.rHost = rHost;
            this.rPort = rPort;
            this.lHost = lHost;
            this.lPort = lPort;
        }

        private String rHost;
        private int rPort;
        private String lHost;
        private int lPort;

        private Connector reader = null;
        private Connector writer = null;
        transient Socket toRemote = null;
        transient Socket toLocal = null;

        @Override
        public void run() {
            pp("tunneling: " + rHost + ":" + rPort + "=>" + lHost + ":" + lPort);
            for (;;) {
                try {
                    toRemote = new Socket(rHost, rPort);
                    pp("toRemote=" + toRemote);
                    toLocal = new Socket(lHost, lPort);
                    pp("toLocal=" + toLocal);
                    reader = new Connector(toRemote, toLocal);
                    writer = new Connector(toLocal, toRemote);
                } catch (IOException e) {
                    //e.printStackTrace();
                    break;
                }
                (new Thread(writer, "TunnelThread")).start();
                reader.run();
            }
            stop();
        }

        /**
         *
         */
        public void stop() {
            if (null != reader) {
                reader.stop();
            }
            if (null != writer) {
                writer.stop();
            }
            try {
                toRemote.close();
            } catch (IOException i) {
            }
            try {
                toLocal.close();
            } catch (IOException i) {
            }
            pp("stopped tunneling: " + rHost + ":" + rPort + "=>" + lHost + ":" + lPort);
        }

        /**
         *
         * @param rHost
         * @param rPort
         * @param lHost
         * @param lPort
         * @return
         */
        public static VC client(String rHost, int rPort, String lHost, int lPort) {
            try {
                VC vc = new VC(rHost, rPort, lHost, lPort);
                new Thread(vc, "TunnelThread").start();
                return vc;
            } catch (Exception e) {
                //e.printStackTrace();
                return null;
            }
        }
    }

    /**
     *
     */
    public static class VS extends ServerSocket implements Runnable, Stateful {

        VS(int fromPort, int toPort) throws IOException {
            super(fromPort);
            this.toServer = new ServerSocket(toPort);
        }

        transient private ServerSocket toServer;
        transient private Socket toService = null;
        transient private Socket fromService = null;

        @Override
        public void run() {
            while (null != toServer) {
                try {
                    pp("accepting toServer: " + toServer);
                    toService = this.toServer.accept();
                    pp("accepting this: " + this);
                    fromService = accept();
                } catch (IOException e) {
                    //e.printStackTrace();
                    continue;
                }
                Connector reader = new Connector(fromService, toService);
                Connector writer = new Connector(toService, fromService);

                pp("connecting: " + fromService + "=>" + toService);

                (new Thread(reader, "TunnelThread")).start();
                (new Thread(writer, "TunnelThread")).start();
            }
        }

        /**
         *
         */
        public void stop() {
            try {
                toServer.close();
            } catch (IOException e) {
            }
            toServer = null;
            try {
                close();
            } catch (IOException e) {
            }
            try {
                toService.close();
            } catch (IOException e) {
            }
            try {
                fromService.close();
            } catch (IOException e) {
            }
            pp("stopped server on: " + this);
        }

        /**
         *
         * @param from
         * @param to
         * @return
         */
        public static VS server(int from, int to) {
            try {
                VS vs = new VS(from, to);
                new Thread(vs, "TunnelThread").start();
                return vs;
            } catch (IOException e) {
                //e.printStackTrace();
                return null;
            }
        }
    }
}
