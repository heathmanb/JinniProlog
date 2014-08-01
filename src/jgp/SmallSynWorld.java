package jgp;

import static java.lang.Long.parseLong;
import static java.lang.Long.valueOf;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import prolog.core.Cat;
import prolog.logic.Stateful;
import prolog.core.BigMath;
import java.math.*;
import static java.math.BigInteger.valueOf;
import java.util.Random;
import static jgp.SynWorld.rshow;

/**
 * Top GP class: creates a world in which evolving individuals seek a minimal
 * expression tree matching a model specified as a truth table, in the form of a
 * BigInteger. The world can be customized to support various distance
 * implementations and evaluators.
 */
public class SmallSynWorld extends BigMath implements Runnable, Stateful {

    /**
     *
     * @param nvars
     * @return
     */
    public static long ranlong(int nvars) {
        return new BigInteger(1 << nvars, rand).longValue();
    }

    /**
     *
     */
    public static Random rand = new Random();

    /**
     *
     * @param nvars
     * @param smodel
     */
    public SmallSynWorld(int nvars, String smodel) {
        this(nvars, parseLong(smodel, 2));
    }

    /**
     *
     * @param nvars
     */
    public SmallSynWorld(int nvars) {
        this(nvars, abs(rand.nextLong() % (1L << (1 << nvars))));
    }

    /**
     *
     * @param nvars
     * @param model
     */
    public SmallSynWorld(int nvars, long model) {
        this.nvars = nvars;
        this.model = model;
        this.npower = 1 << nvars;
    }

    /**
     * nb. of operands of the evaluator
     */
    public final int arity = 2;

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
    public final long maxsteps = Long.MAX_VALUE;

    /**
     * model towards which the population tries to converge, seen as the truth
     * table specifying a function
     */
    final protected long model;

    /**
     * Implements an evolution cycle, starting by creating a few root
     * individuals and then applying randomly evolution steps to random
     * individuals.
     *
     * @return
     */
    public long evolve() {
        for (long steps = 0; steps < maxsteps; steps++) {
            if (model == eval(arity, nvars, steps)) {
                return steps;
            }
            showProgress(steps);
        }

        return -1L;

    }

    /**
     * Runs an evolution cycle for a population and shows the results -
     * including a possible "perfect" individual matching the model searched by
     * the evolutionary process.
     */
    public void run() {
        try {
            showParams();
            if (testing()) {
                return;
            }
            long B = evolve();
            if (B >= 0) {
                show(B);
            } else {
                System.out.println("SEARCH UNSUCCESSFUL AFTER steps=" + maxsteps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public static void run_fg() {
        (new SmallSynWorld(3, "10010110")).run();
    }

    /**
     *
     * @param B
     */
    public void show(long B) {
        System.out.println("\n! FOUND PERFECT at step=" + B);
        lshow(B);
    }

    /**
     *
     */
    public void showParams() {
        System.out.println("PARAMS: model=" + model + ",nvars=" + nvars + ",npower=" + npower + ",maxsteps="
                + maxsteps + "\n");
        //System.out.println("VARS:\n"+evaluator.showVars());
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

    // fast static long tools
    /**
     *
     * @param nbits
     * @return
     */
    public static final long longones(int nbits) {
        return (1L << nbits) - 1L;
    }

    /**
     *
     * @param maxvar
     * @param nvar
     * @return
     */
    public static final long lvar2long(int maxvar, int nvar) {
        long mask = longones(1 << maxvar);
        int nk = maxvar - (nvar + 1);
        long d = (1L << (1 << nk)) + 1L;
        return (mask / d);
    }

    /**
     *
     * @param L
     * @param n
     * @return
     */
    public static final long getBit(long L, int n) {
        return L & (1L << n);
    }

    /**
     *
     * @param L
     * @param n
     * @return
     */
    public static final long setBit(long L, int n) {
        return L | (1L << n);
    }

    /**
     *
     * @param arity
     * @param N
     * @return
     */
    public static long[] long2tuple(int arity, long N) {
        long[] tuple = new long[arity];
        int l = 64;
        for (int k = 0; k < l; k++) {
            long bit = getBit(N, k);
            if (0L == bit) {
                continue;
            }
            int m = k % arity;
            int d = k / arity;
            long I = tuple[m];
            tuple[m] = setBit(I, d);
        }
        return tuple;
    }

    /**
     * applies the primitive operation during synthesis
     *
     * @return
     */
    public static long applyOp(int nvars, long[] Bs) {
        return longones(1 << nvars) & ~(Bs[0] & Bs[1]);
    }

    /**
     *
     * @param arity
     * @param nvars
     * @param B
     * @return
     */
    public static long eval(int arity, int nvars, long B) {
        if (B < 1L) { // 0 represents constant 0
            return 0;
        } else if (B == 1L) {
            return longones(1 << nvars); // longones represents constant 1
        } else if (B < (nvars + 2)) { // vars in [2..nvars+1]
            return lvar2long(nvars, (int) (B - 2));
        } else { // complex expressions in [nvars+2..]
            long[] tuple = long2tuple(arity, B);
            for (int i = 0; i < tuple.length; i++) {
                tuple[i] = eval(arity, nvars, tuple[i]);
            }
            return applyOp(nvars, tuple);
        }
    }

    /**
     *
     * @param O
     */
    public static void pp(Object O) {
        System.out.println(O);
    }

    /**
     *
     * @param L
     */
    public void lshow(long L) {
        rshow(nvars + 2, arity, BigInteger.valueOf(L));
    }

    /**
     *
     * @param nvars
     * @param N
     * @return
     */
    public static long[] long2pair(int nvars, long N) {
        int pow = 1 << nvars;
        long tt = 1L << pow;
        int halfpow = 1 << (nvars - 1);
        long halftt = 1L << halfpow;
        long mask = tt - 1L;
        N = N & mask;
        long low = halftt - 1L;
        long hi = (N & ~low) >> halfpow;
        low = N & low;
        long[] pair = {hi, low};
        //long R=pair2long(nvars,pair);
        //if(R!=N) Interact.println("###="+N+"="+R);
        return pair;
    }

    /**
     *
     * @param nvars
     * @param pair
     * @return
     */
    public static long pair2long(int nvars, long[] pair) {
        long hi = pair[0];
        long low = pair[1];
        int halfpow = 1 << (nvars - 1);
        long res = (hi << halfpow) | low;
        return res;
    }

    /**
     *
     * @param nvars
     * @param n
     * @return
     */
    public static Cat long2cat(int nvars, long n) {
        Cat C = new Cat();
        for (int i = 1; i <= nvars; i++) {
            String I = "" + i;
            C.setProp(I, "v", "l");
            C.setHyper(I, 1);
        }

        String N = new TruthTable(nvars, n).toString();
        C.setProp(N, "v", "r");
        C.setHyper(N, 3);

        addLong(C, nvars, n);

        return C;
    }

    private static void addLong(Cat C, int nvars, long n) {
        String PI = "" + nvars;
        String N = new TruthTable(nvars, n).toString();
        if (n < 2) {
            return;
        }
        if (!"r".equals(C.getProp(N, "v"))) {
            C.setProp(N, "v", "v");
            C.setHyper(N, 0);
        }
        long[] Ns = long2pair(nvars, n);
        for (int i = 0; i < 2; i++) {
            long m = Ns[i];
            String M = new TruthTable(nvars - 1, m).toString();
            if (!"l".equals(C.getProp(M, "v"))) {
                C.setProp(M, "v", "v");
                C.setHyper(M, 0);
            }
            C.setMorphism(N, PI, "i", "f");
            String mid = N + ":" + i;
            C.setProp(mid, "v", "i");
            C.setHyper(mid, 2);
            C.setMorphism(N, mid, "f", "m");
            C.setMorphism(mid, M, "m", "t");
            addLong(C, nvars - 1, m);
        }
    }

    /**
     *
     * @return
     */
    public static boolean testing() {
        int t = 2;
        int nvars = 3;
        long L0 = valueOf("01101001", 2);
        //int nvars=4; long L0=Long.valueOf("0110100110010110",2);
        //int nvars=5; long L0=Long.valueOf("01101001100101101001011001101001",2);
        System.out.println("L0=" + L0);
        int pow = 1 << nvars;
        long tt = 1L << pow;
        if (t != 0) {
            System.out.println("TESTING!!!");

            testModel(nvars, L0);

            if (t > 1) {
                return true;
            }

            for (long i = 0L; i < 10; i++) {
                long L = abs(GPWorld.rand.nextLong());
                testModel(nvars, L);
            }

            return true;
        }
        return false;
    }

    /**
     *
     * @param nvars
     * @param L
     */
    public static void testModel(int nvars, long L) {
        int pow = 1 << nvars;
        long tt = 1L << pow;
        long[] ls = long2pair(nvars, L);
        pp("L=" + L + "(" + ls[0] + "," + ls[1] + ")");
        //pp("Cat:"+long2cat(nvars,L));
        Cat C = long2cat(nvars, L);
        rshow(C);
        //C.dualize();SynWorld.rshow(C);
    }

    /*
     public static boolean testing1() {
     int t=1;long L0=11111L;
     int nvars=;int arity=2;
     Eval e=new EvalNAND(nvars);
     if(t!=0) {
     System.out.println("TESTING!!!");

     for(long i=0;i<64;i++) {
     long L=L0+i;
        
     BigInteger B=BigInteger.valueOf(L);

     long[] ls=long2tuple(2,L);
     BigInteger[] Bs=bigint2tuple(2,B);

     pp(L+"=>"+longones(1<<4)+":"+ls[0]+","+ls[1]);
     pp(B+"=>"+bigones(1<<4)+":"+Bs[0]+","+Bs[1]+'\n');
        
     BigInteger R=e.eval(B);
     long r=eval(arity,nvars,L);
        
     pp(r+"L=B"+R);
     }

     return true;
     }
     return false;
     }
     */
}
