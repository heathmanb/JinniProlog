package prolog.kernel;

import prolog.logic.*;
import java.io.InputStream;
import static prolog.kernel.JavaIO.toReader;
import static prolog.kernel.JavaIO.toWriter;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.errmes;

/**
 * Class that switches off some of the engine extensions which break automatic
 * conversion to C# for alternative .NET implementation. Note that the Prolog
 * engine runs at full functionality as a J# application under .NET.
 */
public class ExtenderStub {

    ExtenderStub(Machine M) {
        this.M = M;
    }

    private Machine M;

    static PrologReader toTokenReader(String fname) throws ExistenceException {
        return null;
    }

    static PrologReader string2TokenReader(String cs) throws ExistenceException {
        return null;
    }

    static boolean activateBytecode(CodeStore codeStore) throws Exception {
        return false;
    }

    static void turnOff() {
    }

    static InputStream zip2stream(String jarname, String fname, boolean quiet) {
        return null;
    }

    /* reflection related */

    /**
     *
     * @param xref
     * @return
     * @throws PrologException
     */
    
    final public Object getTerm(int xref) throws PrologException {
        errmes("reflection disabled: getTerm=" + M.dumpCell(xref));
        return null;
    }

    /**
     *
     * @param xref
     * @return
     */
    public final int new_java_class(int xref) {
        errmes("reflection disabled: new_java_class=" + M.dumpCell(xref));
        return 0;
    }

    /**
     *
     * @param xref
     * @param xargs
     * @return
     */
    public final int new_java_object(int xref, int xargs) {
        errmes("reflection disabled: new_java_object=" + M.dumpCell(xref));
        return 0;
    }

    /**
     *
     * @param classref
     * @param xref
     * @param xmeth
     * @param xargs
     * @return
     */
    public final int invoke_java_method(int classref, int xref, int xmeth, int xargs) {
        errmes("reflection disabled: invoke_java_method=" + M.dumpCell(xmeth));
        return 0;
    }

    /**
     *
     * @param xref
     * @return
     */
    public final boolean delete_java_class(int xref) {
        errmes("reflection disabled: delete_java_class=" + M.dumpCell(xref));
        return false;
    }

    /**
     *
     * @param xref
     * @return
     */
    public final boolean delete_java_object(int xref) {
        errmes("reflection disabled: delete_java_object=" + M.dumpCell(xref));
        return false;
    }

    /**
     *
     * @param xref
     * @param xfield
     * @return
     */
    public final int get_java_field_handle(int xref, int xfield) {
        errmes("reflection disabled: invoke_java_method=" + M.dumpCell(xfield));
        return 0;
    }

    /**
     *
     * @return
     */
    public static PrologReader stdIn() {
        try {
            return toReader("stdin.txt");
        } catch (ExistenceException e) {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public static PrologWriter stdOut() {
        return toWriter("stdout.txt");
    }

    /**
     *
     * @return
     */
    public static String getAppletHome() {
        return "";
    }

    /**
     *
     * @param f
     * @param O
     * @return
     */
    public static boolean toFile(String f, Object O) {
        return false;
    }

    /**
     *
     * @param f
     * @return
     */
    static public Object fromFile(String f) {
        return null;
    }
}
