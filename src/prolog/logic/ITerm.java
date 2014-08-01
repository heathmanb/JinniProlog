package prolog.logic;

/**
 * allows building integer represented terms on a heap in a uniform way
 */
public interface ITerm {

    /**
     *
     * @param id
     * @return
     * @throws PrologException
     */
    public int putVar(Object id) throws PrologException;

    /**
     *
     * @param c
     * @return
     * @throws PrologException
     */
    public int putConst(String c) throws PrologException;

    /**
     *
     * @param s
     * @return
     * @throws PrologException
     */
    public int putString(String s) throws PrologException;

    /**
     *
     * @param i
     * @return
     * @throws PrologException
     */
    public int putInt(int i) throws PrologException;

    /**
     *
     * @param d
     * @return
     * @throws PrologException
     */
    public int putFloat(double d) throws PrologException;

    /**
     *
     * @param f
     * @param args
     * @return
     * @throws PrologException
     */
    public int putFun(String f,int[] args) throws PrologException;

    /**
     *
     * @param o
     * @return
     * @throws PrologException
     */
    public int putObject(Object o) throws PrologException;

    /**
     *
     * @param xref
     * @param O
     * @return
     * @throws PrologException
     */
    public Object getTerm(int xref,OTerm O) throws PrologException;
}
