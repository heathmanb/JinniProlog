package jgp;

import prolog.logic.ObjectDict;
import prolog.logic.Stateful;
import java.math.BigInteger;
import static java.math.BigInteger.valueOf;
import static jgp.Ind.big2string;

import prolog.core.Cat;
import prolog.core.BigMath;
import static prolog.core.BigMath.bigint2exps;
import static prolog.core.BigMath.bigint2gray;
import static prolog.core.BigMath.lvar2bigint;
import prolog.logic.*;

/**
 *
 * @author Brad
 */
public class BigTruthTable implements Stateful {

    /**
     *
     */
    public int nvars;

    /**
     *
     */
    public BigInteger tt;
  
  public int hashCode() {
    return nvars+tt.hashCode();
  }
  
  public boolean equals(Object that) {
    if(!(that instanceof TruthTable)) {
        return false;
        }
    BigTruthTable TT=(BigTruthTable)that;
    return (TT.nvars==nvars) && (TT.tt==tt);
  }
  
    /**
     *
     * @param nvars
     * @param tt
     */
    public BigTruthTable(int nvars,BigInteger tt) {
    this.nvars=nvars;
    this.tt=tt;
  }
  
  public String toString() {
    return tt+"="+big2string(tt,1<<nvars);
  }
  
  /**
   * rebuilds a set B from a hereditarily finite set with 0..nvars-1 urelements
   * therefore it should always return B if correctly implemented
     * @return 
   */
  public static BigInteger heval(int nvars,BigInteger B) {
    BigInteger NVARS=valueOf(nvars);
    return heval(NVARS,B);
  }
  
    /**
     *
     * @param NVARS
     * @param B
     * @return
     */
    public static BigInteger heval(BigInteger NVARS,BigInteger B) {  
    if(B.compareTo(NVARS)<0) {
      return B;
    }
    else {
      BigInteger[] Bs=bigint2exps(B);
      BigInteger R=BigMath.zero;
            for (BigInteger B1 : Bs) {
                BigInteger E = heval(NVARS, B1);
                R=R.setBit(E.intValue());
            }
      return R;
    }
  }
  
    /**
     *
     * @param A
     * @param B
     * @return
     */
    public static final BigInteger xor(BigInteger A,BigInteger B) {
    return A.xor(B);
  }
  
  /*
  public static final BigInteger nand(BigInteger A,BigInteger B) {
    int nvars=Math.max(get_nvars(A),get_nvars(B));
    return BigMath.bignot(1<<nvars,A.and(B));
  }
  
  public static final BigInteger not(BigInteger A) {
    int nvars=get_nvars(A);
    return BigMath.bignot(1<<nvars,A);
  }
  */
  
  
  /**
   * Evaluates a BigInt to a BigInt NAND value
     * @return 
   */
  public static BigInteger nandeval(BigInteger B) {
    int nvars=get_nvars(B);
   
    return nandeval(nvars,B);
  }
  
    /**
     *
     * @param nvars
     * @param B
     * @return
     */
    public static BigInteger nandeval(int nvars,BigInteger B) {
    BigInteger M=BigMath.one.shiftLeft(1<<nvars).subtract(BigMath.one);
    BigInteger N=valueOf(nvars);
    BigInteger r=nandeval1(nvars,N,M,B);
    //System.out.println("??b="+B+"=>r="+r);
    return r;
  }
  
  private static BigInteger nandeval1(int nvars,BigInteger N,BigInteger M,BigInteger B) {
    BigInteger r;
    if(B.compareTo(N)<0) {
      int b=B.intValue();
      r=    lvar2bigint(nvars,b);
    }
    else {
      BigInteger[] Bs=bigint2exps(B);
      r=M;
            for (BigInteger B1 : Bs) {
                BigInteger e = nandeval1(nvars, N, M, B1);
                r=r.and(e);
            }
      r=M.andNot(r);
    }
    //System.out.println("!!b="+B+"=>r="+r);
    return r;
  }
  
    /**
     *
     * @param From
     * @param To
     * @return
     */
    public static int sdistance(BigInteger From,BigInteger To) {
    ObjectDict FD=svertices(From);
    ObjectDict TD=svertices(To);
    int r=FD.simdif_count(TD);
    FD=null; TD=null;
    return r;
  }
  
    /**
     *
     * @param B
     * @return
     */
    public static ObjectDict svertices(BigInteger B) {
    ObjectDict D=new ObjectDict();
    collect_svertices(B,D);
    return D;
  }
  
    /**
     *
     * @param B
     * @param D
     */
    public static void collect_svertices(BigInteger B,ObjectDict D) {
    if(null!=D.get(B)) {
        return;
        } 
    D.put(B,B);
    BigInteger[] Bs=bigint2exps(B);
        for (BigInteger B1 : Bs) {
            collect_svertices(B1, D);
        }
  }
  
    /**
     *
     * @param From
     * @param To
     * @return
     */
    public static int hdistance(BigInteger From,BigInteger To) {
    BigInteger diff=From.xor(To);
    return hcomplexity(diff);
  }
  
    /**
     *
     * @param B
     * @return
     */
    public static int hcomplexity(BigInteger B) {
    ObjectDict D=new ObjectDict();
    hcomplexity(B,D);
    int r=D.size();
    D=null;
    return r-1; // zero does not count - but will alsways be present
  }
  
    /**
     *
     * @param B
     * @param D
     */
    public static void hcomplexity(BigInteger B,ObjectDict D) {
    if(null!=D.get(B)) {
        return;
        } 
    D.put(B,B);
    BigInteger[] Bs=bigint2exps(B);
        for (BigInteger B1 : Bs) {
            hcomplexity(B1, D);
        }
  }
  
    /**
     *
     * @param From
     * @param To
     * @return
     */
    public static int gdistance(BigInteger From,BigInteger To) {
    BigInteger diff=From.xor(To);
    return gcomplexity(diff);
  }
  
    /**
     *
     * @param B
     * @return
     */
    public static int gcomplexity(BigInteger B) {
    ObjectDict D=new ObjectDict();
    gcomplexity(B,D);
    int r=D.size();
    D=null;
    return r-1; // zero does not count - but will alsways be present
  }
  
    /**
     *
     * @param B
     * @param D
     */
    public static void gcomplexity(BigInteger B,ObjectDict D) {
    B=  bigint2gray(B);
    if(null!=D.get(B)) {
        return;
        } 
    D.put(B,B);
    BigInteger[] Bs=bigint2exps(B);
        for (BigInteger B1 : Bs) {
            gcomplexity(B1, D);
        }
  }
  
  /**
   * Evaluates a BigInt to a Boolean
     * @return 
   */
  public static int neval(BigInteger B) {
    return neval1(B)?1:0;
  }
  
    /**
     *
     * @param B
     * @return
     */
    public static boolean neval1(BigInteger B) {
    boolean r;
    if(B.equals(BigMath.zero)) {
      r=false;
    }
    else {
      BigInteger[] Bs=bigint2exps(B);
      r=true;
            for (BigInteger B1 : Bs) {
                boolean e = neval1(B1);
                r=r && e;
            }
    }
    r=!r;
    //System.out.println("!!r:"+B+"=>"+r);
    return r;
  }
  
    /**
     *
     * @param B
     * @return
     */
    public static int get_nvars(BigInteger B) {
    int nvars=0;
    BigInteger MAX=BigMath.one;
    while(B.compareTo(MAX)>=0) {
      MAX=BigMath.one.shiftLeft(1<<(nvars++));
    }
    nvars--;
    //System.out.println("MAX="+MAX+",nvars="+nvars);
    return nvars;
  }
  
    /**
     *
     * @param B
     * @return
     */
    public static Cat big2hcat(BigInteger B) {
    return big2hcat(get_nvars(B),B);
  }
  
    /**
     *
     * @param nvars
     * @param n
     * @return
     */
    public static Cat big2hcat(int nvars,BigInteger n) {
    Cat C=new Cat();
    
    int pow=1<<nvars;
    BigInteger MAX=BigMath.one.shiftLeft(pow);
    //if(n.compareTo(MAX)>=0) return null;
    
    for(int i=0;i<nvars;i++) {
      BigInteger B=valueOf(i);
      //String I=B.toString();
      C.setProp(B,"v","l");
      C.setHyper(B,1);
    }
    C.setProp(n,"v","r");
    C.setHyper(n,3);
    
    addBig(C,nvars,n);
    
    return C;
  }
  
  private static void addBig(Cat C,int nvars,BigInteger n) {
    BigInteger NVARS=valueOf(nvars);
    //String N=new BigTruthTable(nvars,n).toString();
    //String N=n.toString();
    
    if(n.compareTo(NVARS)<0) {
      C.setProp(n,"v","l");
      return;
    }
    if(!"r".equals(C.getProp(n,"v"))) {
      C.setProp(n,"v","v");
      C.setHyper(n,0);
    }
    BigInteger[] Ns=bigint2exps(n);
    int l=Ns.length;
    for(int i=0;i<l;i++) {
      BigInteger m=Ns[i];
      //String M=new BigTruthTable(nvars,m).toString();
      //String M=m.toString();
      if(!"l".equals(C.getProp(m,"v"))) {
        C.setProp(m,"v","v");
        C.setHyper(m,0);
      }
      C.setMorphism(n,m,"f","t");
      addBig(C,nvars,m);
    }
  }
  
}
