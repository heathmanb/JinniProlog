package prolog.core;

import prolog.kernel.*;
import prolog.logic.*;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Implements basic HTTP protocol allowing Prolog to act like a a self contained
 * Web service - in particular to serve it's own code and Prolog files over the
 * Web for supporting a Prolog applet.
 *
 * use: run_http_server -- on default Port 8001 run_http_server(Port)
 */
public class HttpService implements Runnable, Stateful {

    transient private Socket serviceSocket;
    private String www_root;

    public HttpService(Socket serviceSocket, String www_root) {
        this.serviceSocket = serviceSocket;
        this.www_root = www_root;
    }

    byte[] fileORjarfile2bytes(String fname) throws IOException {
        //Prolog.dump("trying: "+fname);
        try {
            return Transport.file2bytes(fname);
        } catch (IOException e) {
            //throw e;
            if (fname.startsWith("./")) {
                fname = fname.substring(2);
            }
            InputStream in = Zipper.zip2stream(Top.ZIPSTORE, fname, true);
            if (null == in) {
                throw e;
            }
            BufferedInputStream stream = new BufferedInputStream(in);
            //Prolog.dump("zipstore: "+fname);
            try {
                return streamToBytes(stream);
            } catch (ExistenceException ee) {
                throw e;
            }
        }
    }

    public static byte[] streamToBytes(InputStream in) throws ExistenceException {
        if (null == in) {
            return null;
        }
        try {
            IntStack is = new IntStack();
            for (;;) {
                int c = in.read();
                if (c == -1) {
                    break;
                }
                is.push((byte) c);
            }
            in.close();
            return is.toByteArray();
        } catch (IOException e) {
            throw new ExistenceException("error in zip file");
        }
    }

    public static String content_header = "HTTP/1.0 200 OK\nContent-Length: ";

    private String fix_file_name(String f) {
        return www_root.concat(
                (f.endsWith("/")
                ? f.concat("index.html")
                : f)
        );
    }

    private static String nextToken(String s) {
        StringTokenizer t = new StringTokenizer(s, " ");
        t.nextToken();
        return t.nextToken();
    }

    @Override
    public void run() {
        try (BufferedReader in
                = new BufferedReader(
                        new InputStreamReader(serviceSocket.getInputStream()));
                DataOutputStream out
                = new DataOutputStream(serviceSocket.getOutputStream())) {
                    while (true) {
                        String s = in.readLine();
                        if (s.length() < 1) {
                            break;
                        }
                        if (s.startsWith("GET")) {
                            String f = nextToken(s);
                            f = fix_file_name(f);
                            byte[] bs = fileORjarfile2bytes(f);
                            out.writeBytes(content_header + bs.length + "\n\n");
                            Transport.bwrite_to(out, bs);
                        } else if (s.startsWith("POST")) {
                            String f = nextToken(s);
                            f = fix_file_name(f);
                            String line = "?";
                            String contentLength = null;
                            while (!line.equals("")) {
                                line = in.readLine().toLowerCase();
                                if (line.startsWith("content-length:")) {
                                    contentLength = nextToken(line);
                                }
                            }

                            int content_length = Integer.parseInt(contentLength);

                            byte[] is = new byte[content_length];
//                            in.readFully(is);

                            String result = call_jinni_post_handler(new String(is), f);
                            byte[] os;
                            if (null == result || result.equals(f)) {
                                // use template file
                                os = fileORjarfile2bytes(f);
                            } else {
                            // use result - it assumes the client
                                // processed the template file + action script
                                os = result.getBytes();
                            }
                            out.writeBytes(content_header + os.length + "\n\n");
                            Transport.bwrite_to(out, os);
                        } else /* ignore other headers */ {
                            // Prolog.dump(s);
                        }

                    }
                } catch (IOException ex) {

                }
    }

    private String call_jinni_post_handler(String is, String os) {
        Machine M = Top.new_machine(null, null);
        if (null == M) {
            return null;
        }
        Object R = new Var(1);
        Fun Goal = new Fun("post_method_wrapper", is, os, R);
        Fun Query = new Fun(":-", R, Goal);
        if (!M.load_engine(Query)) {
            return null;
        }
        try {
            // to allow gc of objects involved the Prolog query should not fail !!!
            String answer = (String) M.get_answer();
            M.removeObject(is);
            M.removeObject(os);
            M.removeObject(answer);
            M.stop();
            return answer;
        } catch (Exception e) {
            //Prolog.dump("exception in POST method handler"+e);
            return null;
        }
    }

    /**
     * Starts a HTTP server rooted in the directory www_root
     * @param port
     * @param www_root
     */
    public static void run_http_server(int port, String www_root) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket serviceSocket = serverSocket.accept();
                Thread T
                        = new Thread(new HttpService(serviceSocket, www_root), "HttpThread");
                T.setDaemon(true);
                T.start();
            }
        } catch (IOException e) {
            JavaIO.errmes("http_server_error", e);
        }
    }
}
