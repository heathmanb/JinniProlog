package jgp;

import java.math.BigInteger;
import static java.math.BigInteger.valueOf;
import static jgp.Ind.big2string;

import prolog.core.Cat;
import prolog.logic.Stateful;

class TruthTable implements Stateful {
  public int nvars;
  public long tt;
  
  public int hashCode() {
    return nvars+(int)tt;
  }
  
  public boolean equals(Object that) {
    if(!(that instanceof TruthTable)) {
        return false;
        }
    TruthTable TT=(TruthTable)that;
    return (TT.nvars==nvars) && (TT.tt==tt);
  }
  
  public TruthTable(int nvars,long tt) {
    this.nvars=nvars;
    this.tt=tt;
  }
  
  public String toString() {
    return nvars+":"+big2string(valueOf(tt),1<<nvars);
  }
  
}
