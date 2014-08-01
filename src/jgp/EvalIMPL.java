package jgp;

import java.math.*;
import prolog.core.BigMath;

/**
 *
 * @author Brad
 */
public class EvalIMPL extends Eval {
 
    /**
     *
     * @param nvars
     */
    public EvalIMPL(int nvars){
    super("=>",nvars);
  }
  
  /**
   * applies the primitive operation during synthesis
     * @return 
   */
  public BigInteger applyOp(BigInteger[] Bs) {    
     return impl(Bs[0],Bs[1]);
  }
}
