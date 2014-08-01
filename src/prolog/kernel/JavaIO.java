package prolog.kernel;

/*
 * Copyright (C) Paul Tarau 1996-2012
 */

import java.io.*;
import static java.lang.Math.max;
import static java.lang.Runtime.getRuntime;
import java.net.*;
import static prolog.core.PrologApplet.getAppletHome;
import static prolog.kernel.Extender.stdIn;
import static prolog.kernel.Extender.stdOut;
import static prolog.kernel.Extender.zip2stream;
import prolog.logic.*;

/**
 * Main IO class, providing operations on file, URL, string based streams.
 * Called though Reflection from Prolog.
 */
public class JavaIO extends Interact {

    //public static String defaultCharEncoding="Cp1252"; // Windows Latin

    /**
     *
     */
        public static boolean showOutput = true;

    /**
     *
     */
    public static int showTrace = 0; // 1 shows it

    // begin stdin/stdout ******************
    private static PrologReader input = stdIn();
    private static PrologWriter output = stdOut();

    /**
     *
     * @return
     */
    public static PrologReader getStdInput() {
        return input;
    }

    /**
     *
     * @param f
     */
    public static void setStdInput(InputStream f) {
        input = toReader(f);
    }

    /**
     *
     * @return
     */
    public static PrologWriter getStdOutput() {
        return output;
    }

    /**
     *
     * @param f
     */
    public static void setStdOutput(OutputStream f) {
        output = toWriter(f);
    }

    // end stdin/sdout ********************

    /**
     *
     * @param f
     * @return
     */
        public static PrologReader toReader(InputStream f) {
        return new PrologReader(f);
    }

    /**
     *
     * @param fname
     * @return
     * @throws ExistenceException
     */
    public static PrologReader toReader(String fname) throws ExistenceException {
        return new PrologReader(url_or_file(fname));
    }

    /**
     *
     * @param fname
     * @return
     * @throws ExistenceException
     */
    public static PrologClauseStringReader toClauseReader(String fname) throws ExistenceException {
        return new PrologClauseStringReader(url_or_file(fname));
    }

    /**
     *
     * @param cs
     * @return
     * @throws ExistenceException
     */
    public static PrologReader string2PrologReader(String cs) throws ExistenceException {
        return new PrologReader(string2reader(cs));
    }

    /**
     *
     * @param cs
     * @return
     * @throws ExistenceException
     */
    public static PrologClauseStringReader
            string2PrologClauseStringReader(String cs)
            throws ExistenceException {
        Reader stream = string2reader(cs);
        return new PrologClauseStringReader(stream);
    }

    /**
     *
     * @param s
     * @return
     */
    public static final Reader string2reader(String s) {
        return new StringReader(s);
    }

    /**
     *
     * @param R
     * @return
     */
    public static String reader2string(Reader R) {
        StringBuilder buf = new StringBuilder();
        try {
            int c;
            while (PrologReader.EOF != (c = R.read())) {
                buf.append((char) c);
            }
        } catch (EOFException e) {
            // ok
        } catch (IOException e) {
            errmes("error in reader2string", e);
            return null;
        }
        return buf.toString();
    }

    /**
     *
     * @param url
     * @return
     * @throws ExistenceException
     */
    public static String url2string(String url) throws ExistenceException {
        return reader2string(toReader(url));
    }

    /**
     *
     * @param f
     * @return
     */
    public static PrologWriter toWriter(OutputStream f) {
        return new PrologWriter(f);
    }

    /**
     *
     * @param s
     * @return
     */
    public static PrologWriter toWriter(String s) {
        PrologWriter f = null;
        //mes("HERE"+s);
        try {
            f = toWriter(new FileOutputStream(s));
        } catch (FileNotFoundException e) {
            warnmes("write error, to: " + s);
        }
        return f;
    }

    /**
     *
     * @param f
     * @param s
     */
    synchronized static public final void print(PrologWriter f, String s) {
        f.print(s);
    }

    /**
     *
     * @param o
     * @param s
     */
    public static final void println(PrologWriter o, String s) {
        o.println(s);
    }

    static final String readln(PrologReader f) {
        return f.readln();
    }

    // I/O for programs without a Machine

    /**
     *
     * @return
     */
        public static final String readln() {
        return readln(getStdInput());
    }

    /**
     *
     * @param s
     */
    public static final void print(String s) {
        print(getStdOutput(), s);
    }

    /**
     *
     * @param s
     */
    public static final void println(String s) {
        println(getStdOutput(), s);
    }

    /**
     *
     * @param s
     */
    public static final void traceln(String s) {
        if (showTrace >= 1) {
            println(s);
        }
    }

    /**
     *
     * @param s
     */
    public static final void dump(String s) {
        println(">>>: " + s);
    }

    /**
     *
     * @param mes
     */
    public static final void assertion(String mes) {
        errmes("assertion failed", (new Exception(mes)));
    }

    /**
     *
     * @param cmd
     * @return
     */
    public static final int system(String cmd) {
        try {
            getRuntime().exec(cmd);
        } catch (IOException e) {
            errmes("error in system cmd: " + cmd, e);
            return 0;
        }
        return 1;
    }

    /**
     * Opens a stream based on a URL or file or a zipped component in
     * [ZIPSTORE].zip.
     *
     * @param s URL or local file name.
     * @return Input stream from URL or File
     * @throws prolog.logic.ExistenceException URL or file not found.
     */
    public static final InputStream url_or_file(String s)
            throws ExistenceException {
        InputStream stream = null;
        String dir;

        if (s.startsWith("http:/")
                || s.startsWith(":", 1)
                || s.startsWith("file:/")
                || s.startsWith("ftp:/")) {
            dir = "";
        } else {
            dir = getPrologHome();
        }

        //System.err.println("dir=<"+dir+"> s="+s);
        if (null == stream && !isApplet) {
            try {
                File F = new File(s);
                if (F.exists()) {
                    stream = new FileInputStream(F);
                }
            } catch (FileNotFoundException e) {
            }
        }

        //if(null!=stream) return stream;
        //dump(s+"$=>"+stream);
        if (null == stream && !isApplet) {
            try {
                File F = new File(dir + s);
                if (F.exists()) {
                    stream = new FileInputStream(F);
                    //System.err.println("dir=<"+dir+"> s="+s);
                }
            } catch (FileNotFoundException e) {
            }
        }

        if (null == stream && !isApplet) {
            stream = zip2stream(Top.ZIPSTORE, s, true); //ends with *.zip or *.jar       
        }    //if(null==stream && null==applet)
        //    stream=Extender.zip2stream(zip_or_jar(Top.ZIPSTORE),s,true); //ends with *.zip or *.jar  

        if (null == stream) {
            String jarURL = "jar:" + Top.ZIPSTORE + "!/" + s;
            // looks like "jar:file:/home/tarau/prolog.jar!/"

            stream = url2stream(jarURL, true);

            //System.err.println("TRYING jarURL===>"+jarURL+"===>"+stream);
        }

        /*
         if(null==stream && null!=applet) {
         try {
         stream=applet.getClass().getResourceAsStream(s);
         }
         catch(Throwable e) {}
         }
         */
        if (null == stream) {
            stream = url2stream(dir + s, true);
            //System.err.println("dir=<"+dir+"> s="+s);
        }

        //System.err.println("ZIPSTORE="+Main.ZIPSTORE+" dir=<"+dir+"> s="+s+"=>"+stream);
        if (null == stream) {
            throw new ExistenceException("error opening for read: " + s);
        }

        traceln("url_or_file found: <" + dir + ">" + s);

        return stream;
    }

    /*
     private static String zip_or_jar(String s) {
     if(s.endsWith(".zip"))
     s=s.substring(0,s.length()-4)+".jar";
     else if(s.endsWith(".jar")) 
     s=s.substring(0,s.length()-4)+".zip";
     else
     s=null;
     return s;
     }
     */
    /**
     * Opens a stream for reading from a URL
     */
    private static InputStream url2stream(String f, boolean quiet) {
        //System.err.println("trying URL: "+f);
        InputStream stream = null;
        try {
            URL url = new URL(f);
            stream = url.openStream();
        } catch (MalformedURLException e) {
            if (quiet) {
                return null;
            }
            errmes("bad URL: " + f, e);
        } catch (IOException e) {
            if (quiet) {
                return null;
            }
            errmes("unable to read URL: " + f, e);
        }

        return stream;
    }

    /**
     *
     * @param pf
     * @return
     */
    public static String pathOf(String pf) {
        int split = max(pf.lastIndexOf('/'), pf.lastIndexOf('\\'));
        return (split > 0) ? pf.substring(0, split + 1) : "";
    }

    /**
     *
     * @param pf
     * @return
     */
    public static String fileOf(String pf) {
        int split = max(pf.lastIndexOf('/'), pf.lastIndexOf('\\'));
        return (split > 0) ? pf.substring(split + 1) : pf;
    }

    /**
     *
     * @param prologHome
     */
    public static void setPrologHome(String prologHome) {
        if (!isApplet) {
            Top.JINNI_HOME = prologHome;
        } else {
            warnmes("cannot override home path for applets");
        }
    }

    /**
     *
     * @return
     */
    public static String getPrologHome() {
        final String prologHome;
        if (!isApplet) {
            prologHome = Top.JINNI_HOME;
        } else {
            prologHome = getAppletHome();
        }
        //Prolog.dump("getHome()====>"+prologHome+NL);
        return prologHome;
    }

    /**
     *
     * @param cmd
     * @return
     * @throws SystemException
     */
    public static final String runCommand(String cmd) throws SystemException {
        try {
            Process P = getRuntime().exec(cmd);
            return runProcess(P);
        } catch (Exception e) {
            throw new SystemException("error in OS call: " + e.getMessage() + cmd);
        }
    }

    /**
     *
     * @param P
     * @return
     * @throws Exception
     */
    public static final String runProcess(Process P) throws Exception {
        StringBuilder buf = new StringBuilder();
        PrologReader err;
        try (PrologReader in = toReader(P.getInputStream())) {
            err = toReader(P.getErrorStream());
            String s;
            while ((s = in.readln()) != null) {
                buf.append(s).append(NL);
            }
            while ((s = err.readln()) != null) {
                buf.append(s).append(NL);
            }
        }
        err.close();
        //P.destroy();
        P.waitFor();

        return buf.toString();
    }
}

/*
TODO: Dead code.
 class VirtualStdOut {
 public static PrologWriter stdout=new PrologWriter(System.out);
 }

 class VirtualStdIn {
 public static PrologReader stdin=new PrologReader(System.in);
 }


 class VirtualStdOut extends PrintStream {

 public static PrologWriter stdout=new PrologWriter(new VirtualStdOut());
 //public static PrologWriter stdout=new PrologWriter(System.out);

 VirtualStdOut() {
 super(new VirtualStdOutAdaptor(),true);
 }
 }

 class VirtualStdOutAdaptor extends OutputStream {

 VirtualStdOutAdaptor() {
 }
  
 public void write(int b) {
 System.out.write(b);//System.out.flush();
 }
  
 public void flush() {
 System.out.flush();
 }
 }

 class VirtualStdIn extends InputStream {

 public static PrologReader stdin=new PrologReader(new VirtualStdIn());
 //public static PrologReader stdin=new PrologReader(System.in);

 VirtualStdIn() {
 }
  
 public int read() throws IOException {
 return System.in.read();
 }
    
 public int read(byte[] bs, int off, int len) throws IOException {
 return System.in.read(bs); 
 }
 }

 */
