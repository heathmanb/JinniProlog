package prolog.core;

/**
 *
 * @author Brad
 */
public class CycleDetector implements GraphVisitor {

    CycleDetector(RankedGraph RG) {
        this.RG = RG;
    }

    private static int WHITE = 0;
    private static int GRAY = -1;
    private static int BLACK = 1;

    private RankedGraph RG;

    /**
     *
     */
    @Override
    public void init() {
        RG.hasCycle = false;
    }

    /**
     *
     */
    @Override
    public void start() {
    }

    /**
     *
     */
    @Override
    public void stop() {
    }

    /**
     *
     * @return
     */
    @Override
    public Object end() {
        RG = null;
        return null;
    }

    /**
     *
     * @param V
     * @return
     */
    @Override
    public boolean isVisited(Object V) {
        int color = RG.getColor(V);
        //Prolog.dump("testing="+V+",col="+color);
        if (GRAY == color) {
            RG.hasCycle = true;
        }
        //Prolog.dump("color="+V+",hasCycle="+ RG.hasCycle);
        return color != WHITE;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean visitIn() {
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean visitOut() {
        return true;
    }

    /**
     *
     * @param V
     */
    @Override
    public void visit(Object V) {
        //Prolog.dump("visit="+V+",col="+RG.getColor(V));
        RG.setColor(V, GRAY);
    }

    /**
     *
     * @param V
     */
    @Override
    public void unvisit(Object V) {
        RG.setColor(V, BLACK);
        //Prolog.dump("unvisit="+V+",col="+RG.getColor(V));
    }
}
