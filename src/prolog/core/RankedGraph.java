package prolog.core;

import java.util.Arrays;
import java.util.Random;
import prolog.kernel.*;
import prolog.logic.*;

/**
 * An extension to the class Graph adapted to support ranking vertex algorithms
 * like GraphRanker
 */
public class RankedGraph extends Graph {

    private double d, e;
    private int maxIter;
    private IntStack componentCounts;
    public boolean hasCycle;

    /**
     * Creates a RankedGraph with given d=damping factor, e=convergence
     * threshold, maxIter=limit on number of iterations.
     *
     * @param d
     * @param e
     * @param maxIter
     */
    public RankedGraph(double d, double e, int maxIter) {
        super();
        init(d, e, maxIter);
    }

    public RankedGraph(int size, double d, double e, int maxIter) {
        super(size);
        init(d, e, maxIter);
    }

    /**
     * Creates a RankedGraph with default constructor parameters.
     */
    public RankedGraph() {
        this(0.85, 0.001, 100);
    }

    /**
     * Creates a RankedGraph with default constructor parameters.
     *
     * @param size
     */
    public RankedGraph(int size) {
        this(size, 0.85, 0.001, 100);
    }

    private void init(double d, double e, int maxIter) {
        this.d = d;
        this.e = e;
        this.maxIter = maxIter;
        this.componentCounts = null;
        this.hasCycle = false;
    }

    @Override
    protected Object wrapData(Object data) {
        RankedData rd = new RankedData(data);
        return rd;
    }

    @Override
    public Object vertexData(Object v) {
        RankedData rd = (RankedData) super.vertexData(v);
        if (null == rd) {
            return null;
        }
        return rd.data;
    }

    /**
     * gets the rank of a vertex
     *
     * @param v
     * @return
     */
    public double getRank(Object v) {
        RankedData rd = (RankedData) super.vertexData(v);
        return rd.rank;
    }

    @Override
    public Object getSelected(Object v) {
        if (RANK == filter) {
            return getRank(v);
        } else {
            return super.getSelected(v);
        }
    }

    /**
     * sets the rank of a vertex
     *
     * @param v
     * @param rank
     */
    public void setRank(Object v, double rank) {
        RankedData rd = (RankedData) super.vertexData(v);
        rd.rank = rank;
    }

    /**
     * gets the the hyper-level of a vertex (0 if vertex 1 if hyperedge, 2 if
     * set of hyperedges etc.
     *
     * @param v
     * @return
     */
    public int getHyper(Object v) {
        RankedData rd = (RankedData) super.vertexData(v);
        return rd.hyper;
    }

    public void setHyper(Object v, int hyper) {
        RankedData rd = (RankedData) super.vertexData(v);
        rd.hyper = hyper;
    }

    public void dualizeHyper() {
        ObjectIterator Vs = vertexIterator();
        while (Vs.hasNext()) {
            Object V = Vs.next();
            int h = getHyper(V);
            h = (0 == h) ? 1 : 0;
            setHyper(V, h);
        }
    }

    public void addHyperEdge(Object H, Object[] Ks) {
        addVertex(H, "hyperedge");
        setHyper(H, 1);
        Arrays.stream(Ks).forEach(K -> {
            addVertex(K, "hypervertex");
            addEdge(K, H, "vertex_to_edge");
        });
    }

    public void addHyperArrow(Object H, Object[] Is, Object[] Os) {
        addVertex(H, "directedHyperedge");
        setHyper(H, 2);
        Arrays.stream(Is).forEach(I -> {
            addVertex(I, "hyperFrom");
            addEdge(I, H, "fanIn");
        });
        Arrays.stream(Os).forEach(O -> {
            addVertex(O, "hyperTo");
            addEdge(H, O, "fanOut");
        });
    }

    static public void hgtest() {
        RankedGraph G = new RankedGraph();
        G.addHyperEdge("AB", new Object[]{"A", "B"});
        G.addHyperEdge("BC", new Object[]{"B", "C"});
        G.addHyperEdge("CA", new Object[]{"C", "A"});
        Prolog.dump("hgtest:\n" + G);
    }

    /**
     * gets the DFS component number of a vert
     *
     * @param v
     * @return
     */
    public int getComponent(Object v) {
        RankedData rd = (RankedData) super.vertexData(v);
        return rd.component;
    }

    /**
     * sets the DFS component number of a vertex
     *
     * @param v
     * @param component
     */
    public void setComponent(Object v, int component) {
        RankedData rd = (RankedData) super.vertexData(v);
        rd.component = component;
    }

    /**
     * gets the color number of a vertex
     *
     * @param v
     * @return
     */
    public int getColor(Object v) {
        RankedData rd = (RankedData) super.vertexData(v);
        return rd.color;
    }

    /**
     * sets the DFS component number of a vertex
     *
     * @param v
     * @param color
     */
    public void setColor(Object v, int color) {
        RankedData rd = (RankedData) super.vertexData(v);
        rd.color = color;
    }

    /**
     * clears data associated with a vertex
     *
     * @param v
     */
    public void clearData(Object v) {
        RankedData rd = (RankedData) super.vertexData(v);
        rd.clear();
    }

    /**
     * performs one iterative GraphRanker step
     *
     * @param classic
     * @return
     */
    public boolean graphRankerStep(boolean classic) {
        //TODO: stream?
        ObjectIterator Vs = vertexIterator();
        boolean done = true;
        while (Vs.hasNext()) {
            Object V = Vs.next();
            double r0 = getRank(V);
            ObjectIterator Ts = classic ? inIterator(V) : outIterator(V);
            double sum = 0.0;
            while (Ts.hasNext()) {
                Object T = Ts.next();
                double t = getRank(T) / (classic ? outDegree(T) : inDegree(T));
                sum += t;
            }
            double r
                    = (1 - d) + d * sum;
            setRank(V, r);
            done = done && (Math.abs(r - r0) < e);
        }
        return done;
    }

    /**
     * Runs GraphR
     *
     * @return anker on this RankedGraph
     */
    public int runGraphRanker() {
        return runGraphRanker(true);
    }

    public int runDualGraphRanker() {
        return runGraphRanker(false);
    }

    /**
     * Runs GraphRanker on this RankedGraph in "classic" or "dual" mode
     *
     * @param classic
     * @return
     */
    public int runGraphRanker(boolean classic) {
        for (int i = 0; i < maxIter; i++) {
            if (graphRankerStep(classic)) {
                return i;
            }
        }
        return -1;
    }

    public RankedKey[] getRankedKeys() {
        ObjectIterator Vs = vertexIterator();
        RankedKey[] Ks = new RankedKey[size()];
        int i = 0;
        while (Vs.hasNext()) {
            Object V = Vs.next();
            Ks[i++] = new RankedKey(V, getRank(V));
        }
        Tools.sort(Ks);
        normalizeRanks(Ks);
        //Prolog.dump("!!!here"+this);
        return Ks;
    }

    public void normalizeRanks(RankedKey[] Ks) {
        //Prolog.dump("normalizing ranks");
        if (Ks.length == 0) {
            return;
        }
        double maxrank = Ks[0].rank;
        if (maxrank <= 0) {
            return;
        }
        for (RankedKey K : Ks) {
            K.rank = K.rank / maxrank;
            setRank(K.key, K.rank);
        }
    }

    private RankedGraph rankedClone() {
        RankedKey[] Ks = getRankedKeys();
        RankedGraph G = new_instance();

        for (RankedKey RK : Ks) {
            Object K = RK.key;
            Entry _e = getEntry(K);
            G.addNewEntry(K, _e.value);
        }
        return G;
    }

    /**
     * Sorts the vertices by decreasing rank order
     */
    public void rankSort() {
        // runGraphRanker();
        RankedGraph G = rankedClone();
        cloneFrom(G);
    }

    /**
     * Marks Connected Components
     */
    public void markComponents() {
        GraphVisitor C = new ComponentCollector(this);
        componentCounts = (IntStack) visit(C);
    }

    public int checkCycle() {
        GraphVisitor D = new CycleDetector(this);
        visit(D);
        return hasCycle ? 1 : 0;
    }

    public ObjectStack filterComponent(int c) {
        if (!(c >= 0 && c < countComponents())) {
            return null;
        }
        ObjectStack Cs = new ObjectStack();
        ObjectIterator Vs = this.vertexIterator();
        while (Vs.hasNext()) {
            Object V = Vs.next();
            if (c == getComponent(V)) {
                Cs.push(V);
            }
        }
        return Cs;
    }

    public int countComponents() {
        if (null == componentCounts) {
            return -1;
        }
        return componentCounts.size();
    }

    public int componentSize(int c) {
        if (!(c >= 0 && c < countComponents())) {
            return -1;
        }
        return componentCounts.at(c);
    }

    public int giantComponent() {
        if (null == componentCounts) {
            return -1;
        }
        int max = -1;
        int giant = -1;
        for (int i = 0; i < componentCounts.size(); i++) {
            int c = componentCounts.at(i);
            if (c <= max) {
                continue;
            }
            max = c;
            giant = i;
        }
        return giant;
    }

    /**
     * adds random vertices and edges to this RankedGraph
     *
     * @param seed
     * @param size
     * @param degree
     */
    public void randomize(int seed, int size, int degree) {
        Random R;
        if (0 == seed) {
            R = new Random();
        } else {
            R = new Random(seed);
        }
        for (int i = 0; i < size; i++) {
            String V = "" + i;
            addVertex(V, "V");
            int steps = R.nextInt(degree);
            for (int j = 0; j < steps; j++) {
                int neighbor = R.nextInt(size);
                String N = "" + neighbor;
                int luck = R.nextInt(4);
                if (0 == luck) {
                    addVertex(N, "V");
                    addEdge(N, V, "E");
                } else if (1 == luck) {
                    addVertex(N, "V");
                    addEdge(V, N, "E");
                } else {
                    // do nothing 50% of the time
                }
            }
        }
    }

    public static String showInfo(RankedGraph RG) {
        int times = RG.runGraphRanker();
        RG.rankSort();
        RG.markComponents();
        int cyclic = RG.checkCycle();
        StringBuilder s = new StringBuilder();
        int c = RG.countComponents();
        int g = RG.giantComponent();
        int gs = RG.componentSize(g);
        s.append("\nhasCycles=").append(cyclic)
                .append(",nb. of components=").append(c)
                .append(",giant=").append(g)
                .append(",gsize=").append(gs)
                .append(" PR steps=").append(times)
                .append("\n");
        for (int i = 0; i < c; i++) {
            s.append("component[").append(i)
                    .append("]->size=").append(RG.componentSize(i))
                    .append("\n");
        }
        return s.toString();
    }

    /**
     * trims a Ranked Graph such that possibly only first N highest ranked
     * elements possibly only from the giant component are shown. Note that the
     * trimmed graph can now be composed of multiple connected components again
     * - as the trimming can break the giant component.
     * @param giantOnly
     * @param max
     * @return 
     */
    public RankedGraph trimRankedGraph(int giantOnly, int max) {
        if (max == 0) {
            return trimToGiant();
        }
        //Main3D.pp("giantOnly="+giantOnly+" g0="+G.size());
        RankedGraph G = trim(giantOnly > 0, max);
        if (giantOnly == 2) {
            G.markComponents();
            //Main3D.pp("g1="+G.size());
            G = G.trim(giantOnly > 0, max);
            G.markComponents();
            //Main3D.pp("g2="+G.size());
        }
        //Main3D.pp("\n"+giantOnly+" trimmed g2=\n"+G);
        return G;
    }

    public RankedGraph new_instance() {
        return new RankedGraph();
    }

    public RankedGraph trimToGiant() {
        RankedKey[] Ks = getRankedKeys();
        RankedGraph G = new_instance();

        int giant = giantComponent();
        for (RankedKey RK : Ks) {
            Object K = RK.key;
            Entry _e = getEntry(K);
            if (giant != getComponent(K)) {
                continue;
            }
            G.addNewEntry(K, _e.value);
        }
        return G;
    }

    public RankedGraph trim(boolean giantOnly, int max) {
        // assumed ranked, rank sorted, and components computed
        RankedGraph A = new_instance();
        int giant = giantComponent();
        ObjectIterator vs = vertexIterator();

        // filter vertices
        int count = 0;
        while (vs.hasNext()) {
            Object V = vs.next();
            if (giantOnly && giant != getComponent(V)) {
                continue;//
            }
            if (count++ >= max) {
                break; //
            }
            Object D = vertexData(V); //
            A.addVertex(V, D);
            A.setRank(V, getRank(V));
            A.setHyper(V, getHyper(V));
            A.setComponent(V, -1); // will be recomputed !!!
            A.setColor(V, 0);
        }

        // loop again, filter edges    
        vs = A.vertexIterator();
        while (vs.hasNext()) {
            Object from = vs.next();
            ObjectIterator es = outIterator(from);
            while (es.hasNext()) {
                Object to = es.next();
                if (null == A.get(to)) {
                    continue; //
                }
                A.addEdge(from, to, edgeData(from, to));
            }
        }

        return A;
    }

    public static void rgtest() {
        try {
            //RankedGraph G=new RankedGraph();G.randomize(0,30,2);

            RankedGraph G = new RankedGraph();
            //G.addVertex("a","only");

            G.addEdge("a", "b", "e1");
            G.addEdge("a", "d", "e2");
            G.addEdge("b", "c", "e3");
            G.addEdge("d", "e", "e4");
            G.addEdge("e", "a", "e5");

            G.markComponents();
            int c = G.countComponents();
            int g = G.giantComponent();
            int gs = G.componentSize(g);

            int times = G.runGraphRanker();
            G.rankSort();

            int x = G.checkCycle();
            Prolog.dump(
                    "\n" + x + " cycles,"
                    + G.componentCounts + "\ncomponents="
                    + c + ",giant=" + g + " ,gsize="
                    + gs + "\ntimes=" + times + " after ranking:\n" + G.toString());
            G = G.trim(true, 10);
            IGraph IG = new IGraph(G, null);
            Prolog.dump("\n----IGraph---\n" + IG);
            Prolog.dump("\n---------\n");
        } catch (Throwable e) {
            JavaIO.printStackTrace(e);
        }
    }

    /*   
     public static void srgtest() {
  
     RankedGraph G=new RankedGraph();
     G.addEdge("a","b","e1");
     G.addEdge("a","c","e2");
     G.addEdge("b","c","e3");
     G.addEdge("c","a","e4");
     G.addEdge("d","c","e5");
     Prolog.dump("before G=\n"+G);  
     G.runGraphRanker();
     Prolog.dump("ranked G=\n"+G);  
     G.rankSort();
     Prolog.dump("rankSorted G=\n"+G);
     Prolog.dump("\n---------\n"); 
    
     G=new RankedGraph();
     G.addEdge("a","b","1");
     G.addEdge("a","c","2");
     G.addEdge("c","a","3");
     G.addEdge("b","b","4");
     G.addEdge("b","d","5");
     G.addEdge("d","a","6");
     G.addVertex("e","99");
     G.setHyper("e",1);
     G.addEdge("a","d","7");
     //G.setMorphism("a","d","9","boo");
     ObjectIterator Vs=G.vertexIterator();
     while(Vs.hasNext()) {
     Object V=Vs.next();
     Prolog.dump(V+": in="+G.inDegree(V)+" out="+G.outDegree(V));
     }
     Prolog.dump("\n"+G.toString());
     Prolog.dump("super:\n"+G.asMap());
     G.removeEdge("c","a");
     Prolog.dump("\nc=>a removed:\n"+G.toString());
     G.removeVertex("b");
     Prolog.dump("b removed:\n"+G.toString());
     int times=G.runDualGraphRanker();
     G.rankSort();
     Prolog.dump("\ntimes="+times+" after ranking:\n"+G.toString());
    
     Prolog.dump("\n---------\n"); 
     }
     */

    public static class RankedKey implements Stateful, Comparable {

        final Object key;
        double rank;

        RankedKey(Object key, double rank) {
            this.key = key;
            this.rank = rank;
        }

        @Override
        public int compareTo(Object O) {
            double orank = ((RankedKey) O).rank;
            if (rank < orank) {
                return 1;
            } else if (rank > orank) {
                return -1;
            } else {
                return 0;
            }
        }

        @Override
        public String toString() {
            return "<" + key + ">:(" + rank + ")";
        }
    }
}
