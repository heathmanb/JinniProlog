package jgp;

import static java.lang.Math.max;
import static java.lang.Thread.sleep;
import prolog.core.Cat;
import prolog.logic.Stateful;
import prolog.logic.Fun;
import prolog.core.BigMath;
import java.math.*;
import static java.math.BigInteger.valueOf;
import java.util.Random;
import static RLI.rli.RLIAdaptor.rli_call;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Top GP class: creates a world in which evolving individuals seek a minimal
 * expression tree matching a model specified as a truth table, in the form of a
 * BigInteger. The world can be customized to support various distance
 * implementations and evaluators.
 */
public class SynWorld extends BigMath implements Runnable, Stateful {

    /**
     *
     */
    public static Random rand = new Random();

    /**
     *
     */
    public static int vtrace = 2;

    /**
     * number of "ur-elements" interpreted as independent variables
     */
    public final int nvars;

    /**
     * size of the phenotype seen as a truth table
     */
    public final int npower;

    /**
     * max number of the total events happening to various individuals
     */
    public final long maxsteps;

    /**
     * model towards which the population tries to converge, seen as the truth
     * table specifying a function
     */
    final protected BigInteger model;

    /**
     *
     */
    final protected Eval evaluator;

    /**
     * Creates an evolvable world using a given formula evaluator.
     *
     * @param evaluator
     */
    public SynWorld(Eval evaluator) {
        this(ranbig(evaluator.getNvars()), evaluator);
    }

    /**
     *
     * @param model
     * @param evaluator
     */
    public SynWorld(BigInteger model, Eval evaluator) {
        this(model, evaluator, new HammingDist());
    }

    /**
     * Creates a new GPWorld. If needed, parameters can be set after this but
     * should be set before calling run().
     *
     * @param model
     * @param evaluator
     * @param distEval
     */
    public SynWorld(BigInteger model, Eval evaluator, DistEval distEval) {
        this.model = model;
        this.evaluator = evaluator;
        this.nvars = evaluator.getNvars();
        this.npower = 1 << nvars;
        int n = 1 << (nvars + 2);
        this.maxsteps = (n < 64) ? (1L << n) : Long.MAX_VALUE;
    }

    /**
     * Static constructor call with exception catching.
     *
     * @return
     */
    public static SynWorld makeWorld() {
        try {
            return new SynWorld(new BigInteger("01101001", 3), new EvalNAND(3));
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    "SynWorld.makeWorld",
                    e);
            return null;
        }
    }

    /**
     *
     * @param nvars
     * @return
     */
    public static BigInteger ranbig(int nvars) {
        return new BigInteger(1 << nvars, rand);
    }

    /**
     * starts a GPWorld as a Thread
     */
    public static void run_bg() {
        Runnable R = makeWorld();
        if (null == R) {
            return;
        }
        Thread T = new Thread(R, "GPThread");
        T.start();
    }

    /**
     * starts a GPWorld as a Thread
     */
    public static void run_fg() {
        Runnable R = makeWorld();
        if (null != R) {
            R.run();
        }
    }

    /**
     * Implements an evolution cycle, starting by creating a few root
     * individuals and then applying randomly evolution steps to random
     * individuals.
     *
     * @return
     */
    public BigInteger evolve() {
        for (long steps = 0; steps < maxsteps; steps++) {
            BigInteger B = valueOf(steps);
            if (model.equals(evaluator.eval(B))) {
                return B;
            }
            showProgress(steps);
        }

        return null;

    }

    /**
     * Runs an evolution cycle for a population and shows the results -
     * including a possible "perfect" individual matching the model seeked by
     * the evolutionary process.
     */
    @Override
    public void run() {
        try {
            showParams();
            BigInteger B = evolve();
            if (null != B) {
                show(B);
            } else {
                System.out.println("SEARCH UNSUCCESSFUL AFTER steps=" + maxsteps);
            }
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    "SynWorld.run",
                    e);
        }
    }

    /**
     *
     * @param B
     */
    public void show(BigInteger B) {
        System.out.println("\n! FOUND PERFECT at step=" + B);
        if (vtrace > 1) {
            rshow(B);
        }
    }

    /**
     *
     */
    public void showParams() {
        System.out.println(
                "PARAMS: nvars=" + nvars
                + ",npower=" + npower
                + ",maxsteps=" + maxsteps
                + "\n");
        System.out.println("VARS:\n" + evaluator.showVars());
    }

    /**
     *
     * @param steps
     */
    public void showProgress(long steps) {
        if (times(steps)) {
            System.out.println("Steps:" + steps);
        }
    }

    boolean times(long steps) {
        long each = 10000 * (1 << max(3, nvars));
        return steps % each == 0;
    }

    /**
     *
     * @param B
     */
    public void rshow(BigInteger B) {
        int ccount = 2;
        System.out.println("!!!:" + B + "=>\n" + evaluator.toExpr(B));
        rshow(nvars + ccount, evaluator.getArity(), B);
    }

    /**
     *
     * @param maxur
     * @param arity
     * @param B
     */
    public static void rshow(int maxur, int arity, BigInteger B) {
        Cat C = bigint2cat(maxur, arity, B);
        rshow(C);
    }

    /**
     *
     * @param C
     */
    public static void rshow(Cat C) {
        if (null == C) {
            return;
        }
        Fun G = new Fun("rshow", C);
        rli_call("localhost", "renoir", G);
        try {
            sleep(2000);
        } catch (InterruptedException e) {
        }
    }

}
