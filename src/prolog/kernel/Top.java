package prolog.kernel;

import prolog.logic.*;
import java.io.ByteArrayOutputStream;
import static java.lang.System.currentTimeMillis;
import static prolog.kernel.Extender.fromFile;
import static prolog.kernel.Extender.toFile;
import static prolog.kernel.Extender.turnOff;
import static prolog.kernel.Init.greeting;
import static prolog.kernel.JavaIO.pathOf;
import static prolog.kernel.JavaIO.println;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.halt;
import static prolog.logic.Interact.warnmes;
import static prolog.logic.Prolog.addPrologClass;
import static prolog.logic.Prolog.dump;
import static prolog.logic.Prolog.getDefaultProlog;
import static prolog.logic.Prolog.getPrologClass;
import static prolog.logic.Prolog.setDefaultProlog;

/**
 * De facto Main class - used by Main - which is kept lightweight. Called though
 * Reflection from Prolog - and from outside modules.
 */
public final class Top {

    /**
     * this class embeds a Machine into your application
     * 
     * @param bpFile Byte prolog file name.
     * @return Prolog engine.
     */
    public static Machine newProlog(String bpFile) {
     // bpFile becomes default embedded wam.bp if null

        Machine I = new_machine();
     //I.query_engine(new Fun("ttyprint","boo"));
        //I.load_engine(new Fun(":-","done","$prolog_loop"));
        return I;
    }

    /*
    TODO: Dead code.
     public static Object[] askProlog(Machine I,Object[] bundle) {
     I.setBundle(bundle);
     Prolog.dump("PTERM:"+new PortableTerm(bundle,0));
     Prolog.dump("askProlog GOT TERM:"+I.fromBundle(bundle));
     try {
     int t=I.ask();
     if(0==t) return null;
     }
     catch(Exception e) {
     e.printStackTrace();
     return null;
     }
     return I.getBundle();
     }
     */

    /**
     *
     * @param I
     * @param bundle
     * @return
     */
    
    public static Object[] askProlog(Machine I, Object[] bundle) {
     //if(null==bundle) return null;
        //Prolog.dump("PTERM:"+new PortableTerm(bundle,0));

     //Prolog.dump("askProlog GOT TERM:"+I.fromBundle(bundle));
        PortableTerm P = new PortableTerm(bundle, 0);
        //Machine M=new_machine();
        EncodedTerm T = P.outputTo(I.prolog);
        //M.stop();
        I.load_engine(T);
        int t;
        try {
            t = I.ask();
            if (0 == t) {
                return null;
            }
        } catch (PrologException e) {
            e.printStackTrace();
            return null;
        }
        T = I.encodedCopy(t);
        P = new PortableTerm(T, I.prolog);
        bundle = P.export(0);

     //Prolog.dump("askProlog RETURNS TERM:"+I.fromBundle(bundle));
        return bundle;
    }

  //EncodedTerm T=P.outputTo(prolog);

    /**
     *
     * @param I
     */
        public static void stopProlog(Machine I) {
        I.stop();
    }

  // API tester

    /**
     *
     */
        public static void testProlog() {
        Fun goal = new Fun("println", "hello");
        Machine C = new_machine();
        Object[] bundle = C.toBundle(goal);
        if (null == bundle) {
            return;
        }
        Machine I = newProlog(null);
        bundle = askProlog(I, bundle);
        if (null == bundle) {
            return;
        }
        dump("ANSWER BUNDLE:" + new PortableTerm(bundle, 1));
        dump("ANSWER TERM:" + C.fromBundle(bundle));
    }

    /**
     *
     * @return
     */
    public static int get_verbosity() {
        return Interact.verbosity;
    }

    /**
     *
     * @param level
     */
    public static void set_verbosity(int level) {
        Interact.verbosity = level;
    }

    /**
     *
     * @return
     */
    public static int get_quickfail() {
        return Interact.quickfail;
    }

    /**
     *
     * @param level
     */
    public static void set_quickfail(int level) {
        Interact.quickfail = level;
    }

    /**
     *
     */
    public static String defaultEncoding = "UTF-8"; // same as "UTF8"

    /**
     *
     * @param encoding
     */
    public static void setEncoding(String encoding) {
        defaultEncoding = encoding;
    }

    /**
     *
     * @return
     */
    public static String getEncoding() {
        return defaultEncoding;
    }

    /**
     * default jar or zip file containing all our Java classes and Prolog
     * bytecode
   *
     */
    public static String ZIPSTORE = "prolog.jar";

    /**
     * default jar or zip file containing all our Java classes and Prolog
     * bytecode
   *
     */
    public static String JINNI_HOME = "";

    /**
     *
     * @return
     */
    public static final String getPrologName() {
        return Init.getPrologName();
    }

    /**
     *
     * @return
     */
    public static final int getPrologVersion() {
        return Init.XBRAND_MINOR;
    }

    private static ObjectDict dict = new ObjectDict();

    /**
     *
     * @param key
     * @return
     */
    public synchronized static final Object getProp(Object key) {
        return dict.get(key);
    }

    /**
     *
     * @param key
     * @param val
     */
    public synchronized static final void setProp(Object key, Object val) {
        dict.put(key, val);
    }

    /**
     *
     * @param key
     */
    public synchronized static final void rmProp(Object key) {
        dict.remove(key);
    }

    /**
     *
     * @param file
     */
    public synchronized static final void saveProps(String file) {
        toFile(file, dict);
    }

    /**
     *
     * @param file
     */
    public synchronized static final void loadProps(String file) {
        dict = (ObjectDict) fromFile(file);
    }

    /**
     *
     */
    public synchronized static final void clearProps() {
        dict = new ObjectDict();
    }

    /**
     * Initializes the runtime system and returns a new Machine ready to run
     * queries.      *
     * args[0] contains the absolute path to the *.zip file (usually prolog.zip)
     * containing our java classes (usually prolog.kernel.*) and Prolog bytecode
     * (usually in wam.bp).
     *
     * args[k] with k>0 contain Prolog goals to be run in a default Prolog
     * envoronment before control is given to an interactive shell (the Prolog
     * toplevel).
     *
     * The second argument, is a boolean requesting, if true suppression of
     * various Prolog messages.
     * 
     * @param argv * Command line arguments
     * @return Prolog machine
     */
    final public static Machine initProlog(String[] argv) {
        return initProlog(argv, null, null);
    }

    /**
     *
     * @param argv
     * @param input
     * @param output
     * @return
     */
    final public static Machine initProlog(String[] argv, PrologReader input, PrologWriter output) {
        greeting(get_verbosity());
        int argc = 0;

    //if(null!=argv) for(int j=0;j<argv.length;j++) {Prolog.dump("argv["+j+"]="+argv[j]);}
        if (null == argv) {
            // nothing to do - defaults are fine
        } else if (argv.length > 0 && argv[0].indexOf('(') >= 0 && argv[0].indexOf(')') > 0) {
            // nothing to do - this is not a file or url command !!!
        } else if (argv.length > 0 && argv[0].endsWith(".bp")) {
            Interact.PROLOG_BYTECODE_FILE = argv[0];
            turnOff(); // overrides loading of Javafied default bytecode
            argc++;
            //Prolog.dump("found=>"+argv[0]);
        } else if (argv.length > 0 && argv[0].endsWith(".jc")) {
            turnOff(); // overrides loading of Javafied default bytecode
            argc++;
            //Machine M=new_machine(argv[0]);Prolog.setDefaultProlog(M.prolog);return M;
            setDefaultProlog(new_prolog(argv[0]));
        } else if (argv.length > 0 && (argv[0].equals("..") || argv[0].equals(".")
                || argv[0].indexOf('/') >= 0 || argv[0].indexOf('\\') >= 0)) {
            if (argv[0].endsWith(".zip") || argv[0].endsWith(".jar")) {
                ZIPSTORE = argv[0];
                JINNI_HOME = pathOf(argv[0]);
            } else {
                char last = argv[0].charAt(argv[0].length() - 1);
                if ('/' == last || '\\' == last) {
                    JINNI_HOME = argv[0];
                } else {
                    JINNI_HOME = argv[0] + '/';
                }
            }
            argc++;
        } else if (argv.length > 0 && (argv[0].endsWith(".zip") || argv[0].endsWith(".jar"))) {
            ZIPSTORE = argv[0];
            argc++;
        }

        Machine machine = new_machine(input, output);

        if (null == machine) {
            return null;
        }
        //if(null==argv||0==argv.length) return machine;

        if (Init.XBRAND_START_IDE > 0) {
            if (null == argv || 0 == argv.length) {
                argv = new String[2];
                argv[0] = "ide";
                argv[1] = "sleep(500000)";
            }
        }
    // splashes away with this !!!
        //if(null==argv||0==argv.length) {argv=new String[1]; argc=0; argv[0]="ide";}

        boolean gotInitFile = false;
        for (String query : argv) {

            if (!gotInitFile && (query.endsWith(".pl") || query.endsWith(".pro")) || query.endsWith(".jpl") || query.endsWith("]")) {
                gotInitFile = true; // the compiler can only handle one initial file at this point...
                if (!query.endsWith("]")) {
                    query = "'" + query + "'";
                }
                query = "compile(" + query + ")";
            }
            //JavaIO.println("cmd query ?- "+ query+".");
            String answer = machine.run(query);
            //JavaIO.println("cmd answer => "+ answer);
            if (answer.equals("no") || answer.startsWith("the(exception(")) {
                warnmes("error trying: " + query);
                machine.stop();
                machine = null;
                break;
            }
        }
        return machine;
    }

    /**
     * Starts a new Machine and Prolog toplevel - deprecated -
     * @param machine
     */
    @Deprecated
    public static void toplevel(Machine machine) {
        long duration;
        String answer;
        PrologReader input = null;
        PrologWriter output = null;

        for (;;) {
            duration = currentTimeMillis();
            answer = machine.run(null);
            println("Toplevel engine returned: " + answer);

            if (!answer.equals("no")) {
                break;
            }

            machine = new_machine(input, output);
            if (null == machine) {
                break; // regenerate machine
            }
            duration = currentTimeMillis() - duration;

            if (duration < 400L) {
                break;
            }
        }
        halt(0);
    }

    /**
     *
     */
    public static void toplevel() {
        (new Shell(null, null, null, false)).run();
    }

    /**
     * Returns a new Machine - de facto constructor to be used from outside the
     * package.
     * 
     * @param P Prolog binary.
     * @param input Input reader
     * @param output Output writer
     * @return 
     */
    public final static Machine new_machine(
            Prolog P, PrologReader input, PrologWriter output) {
        return Machine.new_machine(P, input, output);
    }

    /**
     *
     * @param P
     * @return
     */
    public final static Machine new_machine(Prolog P) {
        if (null == P) {
            P = getDefaultProlog();
        }
        return Machine.new_machine(P, null, null);
    }

    /**
     *
     * @param input
     * @param output
     * @return
     */
    public final static Machine new_machine(PrologReader input, PrologWriter output) {
        return new_machine(getDefaultProlog(), input, output);
    }

    /**
     *
     * @return
     */
    public final static Machine new_machine() {
        return new_machine(null, null);
    }

    /**
     *
     * @param serializedFname
     * @return
     */
    public final static Machine new_machine(String serializedFname) {
        Prolog P = new_prolog(serializedFname);
        setDefaultProlog(P);
        return new_machine(null, null);
    }

    /**
     *
     * @param query
     * @return
     */
    public final static String collect_call(Object query) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrologWriter stringOutput = new PrologWriter(output);
        Fun G = new Fun("and", query, "fail");
        Machine E = new_machine(null, stringOutput);
        E.query_engine(G);
        return output.toString();
    }

    /**
     *
     * @param query
     * @return
     */
    public final static Object call(Object query) {
        Machine E = new_machine();
        Object answer = E.query_engine(query);
        E.stop();
        return answer;
    }

    /**
     *
     * @param serializedClassName
     * @param R
     * @param W
     * @return
     */
    public final static Machine new_cached_machine(String serializedClassName,
            PrologReader R, PrologWriter W) {
        Prolog P = getPrologClass(serializedClassName);
        if (null == P) {
            P = new_prolog(serializedClassName + ".jc");
            if (null == P) {
                return null;
            }
            addPrologClass(serializedClassName, P);
        }
        return new_machine(P, R, W);
    }

    /**
     *
     * @param serializedClassName
     * @return
     */
    public final static Machine new_cached_machine(String serializedClassName) {
        return new_cached_machine(serializedClassName, null, null);
    }

    /**
     *
     * @param serializedFname
     * @return
     */
    public final static Prolog new_prolog(String serializedFname) {
        Prolog prolog = (Prolog) fromFile(serializedFname);
        prolog.refresh();
        return prolog;
    }

    /**
     * Main entry point. Command line parameters include the Prolog home
     * directory (or zip or jar file) the name of the *.bp bytecode file as well
     * as Prolog goals to be run before the toplevel interpreter (with the ?-
     * prompt) is started. No need for heap,stack etc. related parameters, all
     * memory managment is automated.
     * 
     * @param argv Command line arguments.
     */
    public static void jinniMain(String[] argv) {

        try {
            Machine M = initProlog(argv); // quietness: false means verbose
            if (M != null) {
                toplevel(M);
            }
        } catch (Throwable e) {
            errmes("irrecoverable " + getPrologName() + " error", e);
        }
    }

}
