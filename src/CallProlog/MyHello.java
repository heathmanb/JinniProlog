import prolog.kernel.JavaIO;
import prolog.kernel.Machine;
import prolog.kernel.Top;
import prolog.kernel.*;
import java.util.*;
import java.io.*;
import static prolog.kernel.Top.new_machine;
import static prolog.logic.Interact.halt;
/**
 *  Shows examples of calls form Java to Prolog and back
 */
public class MyHello { 
 
  /**
   *  This class embeds Jinni into your application
   *  after initializing it from a .jc file created
   *  using the command "serialize(myfile)" that saves
   *  the state of a com piled application as  serialized
   *  Java object.
   *  To compile this type compile.bat, to run it type runhello.bat
   * 
   *  Note that new_machine() with no parameter starts with a default
   *  engine.
     * @param args
   */
  public static void main (String args[]) { 
    Machine M=new_machine("hello.jc");
    M.run("println(type(go))");
    //Top.toplevel();
        halt(0);
  }
}