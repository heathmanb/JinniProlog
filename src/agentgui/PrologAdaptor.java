package agentgui;

import prolog.logic.Prolog;
import prolog.kernel.PrologWriter;
import prolog.kernel.PrologReader;
import prolog.kernel.Machine;
import prolog.kernel.Top;
import prolog.logic.*;
import prolog.kernel.*;
import prolog.core.*;
import static prolog.kernel.Top.new_machine;
import static prolog.logic.Prolog.getDefaultProlog;

/**
 *
 * @author Brad
 */
public class PrologAdaptor implements Runnable {
	private Prolog prolog;
    private final Object metacall;
    
    /**
     *
     */
    public PrologAdaptor() {
		this(null);
	}

    /**
     *
     * @param metacall
     */
    public PrologAdaptor(Object metacall) {
      this.prolog = getDefaultProlog();
      this.metacall=metacall;
    }
    
    /**
     *
     * @param query
     * @return
     */
    public Object callProlog(Object query) {
	   return callProlog(query,null,null);
	}
	
    /**
     *
     * @param query
     * @param input
     * @param output
     * @return
     */
    public Object callProlog(Object query,PrologReader input,PrologWriter output) {
		Machine E = new_machine(this.prolog,input,output); // possibly cached
		Object R = E.query_engine(query); // does work on server
		// System.err.println(E.hashCode() +":"+query+"=>"+R);
		return R;
	}
	
   public void run() {	
      if(null==this.metacall) {
          System.out.println("override run in prologAdaptor or define metacall");
        } else {
        callProlog(this.metacall);
      }
   }
	
    /**
     *
     */
    public void bg_run() {
	   Thread T=new Thread(this,"PrologAdaptorThread");
	   T.setDaemon(true);
	   T.start();
   }
}
