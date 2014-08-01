package prolog.kernel;

import prolog.logic.*;
import java.io.*;
import static prolog.kernel.Machine.unQuote;
import static prolog.kernel.Top.initProlog;
import static prolog.kernel.Top.new_machine;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.halt;
import static prolog.logic.Interact.warnmes;

/**
 * Interactive Console Shell Abstraction For Querying the Prolog Machine.
 * Designed to be overridden by Remote, Applet or Web based shells or shells for
 * machines without a console like Pocket PC. It can be seen as a "skin" for the
 * Prolog interpreter.
 */
public class Shell implements Runnable, Stateful {

    /**
     *
     * @param args
     * @param input
     * @param output
     * @param init
     * @param prompt
     */
    public Shell(String args[], PrologReader input, PrologWriter output,
            boolean init, String prompt) {
        this.input = input;
        this.output = output;
        if (init) {
            this.M = initProlog(args, input, output);
        } else {
            restart_machine(true);
        }
        this.prompt = prompt;
        this.init = init;
        //System.err.println("mclass="+M.getClass());
    }

    private Machine M;
    private String prompt;
    private boolean init;
    private PrologReader input;
    private PrologWriter output;

    private void restart_machine(boolean ok) {
        this.M = new_machine(this.input, this.output);
        if (ok) {
            warnmes("Starting New Prolog Machine.");
        } else {
            warnmes("Restarting Prolog Machine: " + M);
        }
    }

    /**
     *
     * @param args
     * @param input
     * @param output
     * @param init
     */
    public Shell(String args[], PrologReader input, PrologWriter output, boolean init) {
        this(args, input, output, init, "?- ");
    }

    /**
     *
     * @param args
     * @param input
     * @param output
     */
    public Shell(String args[], PrologReader input, PrologWriter output) {
        this(args, input, output, true);
    }

    /**
     *
     * @param args
     */
    public Shell(String[] args) {
        this(args, null, null);
    }

    /**
     *
     */
    public Shell() {
        this(null);
    }

    // begin IO

    /**
     *
     * @return
     */
        public String readln() {
        //return M.readln();
        if (null == input) {
            return consoleReadln();
        }
        return JavaIO.readln();

    }

    /**
     *
     * @param S
     */
    public void print(String S) {
        //M.print(S);
        JavaIO.print(S);
    }

    /**
     *
     * @param S
     */
    public void println(String S) {
        //M.println(S);
        JavaIO.println(S);
    }
    // end IO

    /**
     *
     * @return
     */
    public String readQuery() {
        M.print(prompt);
        M.flush();
        if (null == input) {
            return consoleReadln();
        }
        return M.readln();
    }

    /**
     *
     * @param S
     */
    public void send_query(String S) {
        M.load_engine(make_query(S));
    }

    /**
     *
     * @param S
     * @return
     */
    public Object make_query(String S) {
        //String SGoal="__A:-scall('("+S+")',__A)";  

        Var Answer = new Var(1); //only var!
        Fun SGoal = new Fun(":-",
                Answer,
                new Fun("scall", S, Answer)
        );

        return SGoal;
    }

    /**
     *
     * @return
     * @throws PrologException
     */
    public String get_answer_element() throws PrologException {
        int v = M.ask();
        if (0 == v) {
            return null;
        }
        String S = M.termToString(v);
        S = unQuote(S);
        return S;
    }

    /**
     *
     * @param S
     * @return
     * @throws PrologException
     */
    public boolean shell_step(String S) throws PrologException {
        send_query(S);

        OUTER:
        for (int i = 0;; i++) {
            String A = get_answer_element();
            if (null == A) {
                break;
            }
            switch (A) {
                case "no":
                case "yes":
                    if (i > 0) {
                        println(";");
                    }   println(A);
                println("");
                    break OUTER;
                case "new":
                    if (i > 0) {
                        println(";");
                    }   break;
                case "=":
                    String X = get_answer_element();
                    String T = get_answer_element();
                    println(X + " = " + T);
                    break;
                default:
                    println(M + " returned:: " + A);
                    return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        if (null == M) {
            warnmes("unable to initialize Prolog engine");
            return;
        }
        for (;;) {
            String S = readQuery();
            if (null == S) {
                break;
            }
            if ("".equals(S)) {
                continue;
            }

            boolean ok;
            try {
                ok = shell_step(S);
            } catch (PrologException e) {
                //JavaIO.errmes("Error caught by Shell: ", e);
                warnmes("Error caught by Shell!");
                ok = false;
            }
            if (!ok) {
                restart_machine(false);
            }
        }
        if (init) {
            halt(0);
        }
    }

    // using jline
    static final private jline.ConsoleReader in = makeReader();

    private static jline.ConsoleReader makeReader() {
        try {
            return new jline.ConsoleReader();
        } catch (IOException e) {
            errmes("unable to start ConsoleIO");
            return null;
        }
    }

    /**
     *
     * @return
     */
    public String consoleReadln() {
        try {
            return in.readLine("");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public int read() throws IOException {
        return in.readVirtualKey();
    }

}
