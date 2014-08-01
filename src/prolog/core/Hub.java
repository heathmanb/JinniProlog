package prolog.core;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import prolog.logic.*;
import static prolog.logic.Interact.errmes;

/**
 * An Hub is a device which synchronizes Producers and Consumers. Called through
 * Reflection from Prolog.
 */
public class Hub implements Stateful {

    private static final long secTimout = 10;
    private static long msTimeout;  // secs max
    private static Hub atomizer;

    /**
     *
     */
    public static boolean trace = true;
    static {
        reset_critical(secTimout);
    }

    private final boolean forever;
    private Object port;
    private long timeout;

    /**
     *
     * @param t
     */
    public Hub(long t) {
        this.forever = (0L == t);
        this.port = null;
        this.timeout = t + currentTimeMillis();
    }

    /**
     *
     * @param secs
     */
    public static void reset_critical(long secs) {
        if (null != atomizer) {
            atomizer.stop();
        }
        msTimeout = 1000L * secs;
        atomizer = new Hub(msTimeout);
    }

    /**
     *
     * @return
     */
    public static int enter_critical() {
        return enter_critical(currentThread());
    }

    /**
     *
     * @param who
     * @return
     */
    public static int enter_critical(Object who) {
        int ok = atomizer.putElement(who);
        if (0 == ok) {
            Object frozen = atomizer.port;
            reset_critical(secTimout);
            PrologException e
                    = new PrologException(
                            "timeout entering critical for: "
                                    + who
                                    + ",frozen:"
                                    + frozen);
            if (! trace) {
                errmes("atomizer_error", e);
            }
        }
        return ok;
    }

    /**
     *
     * @return
     */
    public static int exit_critical() {
        return exit_critical(currentThread());
    }

    /**
     *
     * @param who
     * @return
     */
    public static int exit_critical(Object who) {
        int ok = 0;
        Object other = null;
        if (atomizer.contains(who)) {
            other = atomizer.collect();
            if (who.equals(other)) {
                ok = 1;
            }
        }
        if (0 == ok) {
            reset_critical(secTimout);
            PrologException e
                    = new PrologException(
                            "error exiting critical, expecting: "
                                    + who
                                    + ",found: "
                                    + other);
            if (! trace) {
                errmes("atomizer_error", e);
            }
        }
        return ok;
    }

    /**
     *
     * @param other
     * @return
     */
    synchronized public boolean contains(Object other) {
        return other.equals(port);
    }

    /**
     *
     * @return
     */
    public synchronized Object collect() {
        int ok = 1;
        while (null == port) {
            try {
                if (forever) {
                    wait();
                } else {
                    long t = this.timeout - currentTimeMillis();
                    if (t <= 0) {
                        ok = 0;
                        break;
                    } // avoid: wait(0) - it means forever !!!
                    wait(t);
                }
            } catch (InterruptedException e) {
            }
        }
        Object result = null;
        if (ok > 0) {
            result = port;
            port = null;
        }
        notifyAll();
        return result;
    }

    /**
     *
     * @param T
     * @return
     */
    public synchronized int putElement(Object T) {
        int ok = 1;
        while (null != port) {
            try {
                if (forever) {
                    wait();
                } else {
                    long t = this.timeout - currentTimeMillis();
                    if (t <= 0) {
                        ok = 0;
                        break;
                    }; // avoid: wait(0) - it means forever !!!
                    wait(t);
                }
            } catch (InterruptedException e) {
            }
        }
        if (ok > 0) {
            port = T;
        }
        notifyAll();
        return ok;
    }

    /**
     *
     */
    synchronized public void stop() {
        this.timeout = -1L;
    }
}
