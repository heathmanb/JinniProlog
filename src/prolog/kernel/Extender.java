package prolog.kernel;

import prolog.logic.*;

import java.io.InputStream;

/*
 public class Extender extends ExtenderStub {
 Extender(Machine M) {super(M);}
  
 public static PrologReader stdIn() {return JavaIO.toReader(System.in);}     
 public static PrologWriter stdOut() {return JavaIO.toWriter(System.out);}
       
 //C# code 
 //public static PrologReader stdIn() {return JavaIO.toReader(Console.OpenStandardInput());}   
 //public static PrologWriter stdOut() {return JavaIO.toWriter(Console.OpenStandardOutput());}

 //public static long doubleToLongBits(double d) {return System.BitConverter.DoubleToInt64Bits(d);}
 //public static double longBitsToDouble(long bits64) {return System.BitConverter.Int64BitsToDouble(bits64);}
 }
 */
//*
import prolog.core.*;
import static prolog.kernel.JavaIO.toReader;
import static prolog.kernel.JavaIO.toWriter;

/**
 * Bridge between mandatory prolog.kernel classes and optional classes in
 * prolog.core.
 */

public class Extender extends ExtenderFactory implements Stateful {

    Extender(Machine M) {
        super(M);
    }

    static PrologReader toTokenReader(String fname) throws ExistenceException {
        return TokenReader.toTokenReader(fname);
    }

    static PrologReader string2TokenReader(String cs) throws ExistenceException {
        return TokenReader.string2TokenReader(cs);
    }

    /**
     *
     * @param codeStore
     * @return
     * @throws Exception
     */
    public static boolean activateBytecode(CodeStore codeStore) throws Exception {
        return Javafier.activateBytecode(codeStore);
    }

    static void turnOff() {
        Javafier.turnOff();
    }

    static InputStream zip2stream(String jarname, String fname, boolean quiet) {
        return Zipper.zip2stream(jarname, fname, quiet);
    }

    /**
     *
     * @return
     */
    public static PrologReader stdIn() {
        return toReader(System.in);
    }

    /**
     *
     * @return
     */
    public static PrologWriter stdOut() {
        return toWriter(System.out);
    }

    /**
     *
     * @return
     */
    public static String getAppletHome() {
        return PrologApplet.getAppletHome();
    }

    /**
     *
     * @param f
     * @param O
     * @return
     */
    public static boolean toFile(String f, Object O) {
        return Transport.toFile(f, O);
    }

    /**
     *
     * @param f
     * @return
     */
    static public Object fromFile(String f) {
        return Transport.fromFile(f);
    }
}
//*/
