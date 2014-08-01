package prolog.kernel;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static prolog.kernel.Extender.activateBytecode;
import static prolog.kernel.JavaIO.toReader;
import prolog.logic.*;
import static prolog.logic.Defs.OUTPUT_INT;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.println;

/**
 *
 * @author Brad
 */
public class CodeIO implements Stateful {

    private CodeStore codeStore;

    /**
     *
     * @param codeStore
     */
    public CodeIO(CodeStore codeStore) {
        this.codeStore = codeStore;
    }

    /**
     *
     * @param fname
     * @param codeStore
     * @return
     */
    public static Boolean load(String fname, CodeStore codeStore) {
        boolean ok = new CodeIO(codeStore).try_load(fname);
        return ok;
    }

    /**
     * Loads a bytecode file from a URL or a file. If this fails, tries to load
     * byte code from Java class.
     */
    synchronized boolean try_load(String fName) {
        if (jload()) {
            return true;
        }
        return fload(fName);
    }

    private boolean jload() {
        boolean ok = false;
        try {
            ok = activateBytecode(this.codeStore);
        } catch (Exception e) {
            errmes("Error loading Prolog bytecode from Java classes", e);
        }
        if (ok) {
            println("Loading Prolog bytecode from Java classes.");
        }
        return ok;
    }

    private boolean fload(String fName) {
        boolean ok;
        try (PrologReader in = toReader(fName)) {
            floadfromReader(in);
            ok = true;
        } catch (Exception e) {
            errmes("Error in loading:" + fName, e);
            ok = false;
        }
        return ok;
    }

    /**
     * Reads instructions from *.bp Prolog bytecode files.
     * 
     * @param in
     * @throws prolog.logic.PrologException
     */
    public void floadfromReader(PrologReader in) throws PrologException {
        int opcode, reg, arity;

        for (;;) {
            String l = in.readln();
            opcode = parseInt(l);
            l = in.readln();
            reg = parseInt(l);
            l = in.readln();
            arity = parseInt(l);
            l = in.readln();
            this.codeStore.loadInstruction(opcode, reg, l, arity);
            if (opcode == CodeStore.END) {
                break;
            }
        }
        if (opcode != CodeStore.END) {
            throw new LoadException("Premature end of file during instruction loading.");
        }
    }

    /**
     *
     * @param out
     * @param op
     * @param reg
     * @param name
     * @param arity
     */
    static final public void write_instr(PrologWriter out, int op, int reg, String name, int arity) {
        out.println(OUTPUT_INT(op));
        out.println(OUTPUT_INT(reg));
        out.println(OUTPUT_INT(arity));
        out.println(name);
    }

}
