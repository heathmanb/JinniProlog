package prolog.logic;

/**
 * allows building external terms represented as generic Object refrences in a uniform way
 * in particular, it allows interoperation with Kernel Prolog interpreter
 */
public interface OTerm {

    /**
     *
     * @param i
     * @return
     * @throws PrologException
     */
    public Object putVar(int i) throws PrologException;

    /**
     *
     * @param c
     * @return
     */
    public Object putConst(String c);

    /**
     *
     * @param i
     * @return
     */
    public Object putInt(int i);

    /**
     *
     * @param f
     * @param args
     * @return
     */
    public Object putFun(String f,Object[] args);

    /**
     *
     * @param d
     * @return
     */
    public Object putFloat(double d);

    /**
     *
     * @param t
     * @param I
     * @return
     * @throws PrologException
     */
    public int getTerm(Object t,ITerm I) throws PrologException;
}