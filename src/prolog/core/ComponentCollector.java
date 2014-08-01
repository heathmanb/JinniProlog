package prolog.core;

import prolog.logic.*;

public class ComponentCollector implements GraphVisitor {

    ComponentCollector(RankedGraph RG) {
        this.RG = RG;
    }

    private int componentCount;
    private IntStack collected;
    private int elementCount;

    private RankedGraph RG;

    @Override
    public void init() {
        componentCount = -1;
        elementCount = -1;
        this.collected = new IntStack();
    }

    @Override
    public void start() {
        componentCount++;
        elementCount = 0;
    }

    @Override
    public void stop() {
        collected.push(elementCount);
    }

    ;
  
    @Override
    public Object end() {
        RG = null;
        componentCount++;
        return collected;
    }

    @Override
    public boolean isVisited(Object V) {
        return RG.getComponent(V) >= 0;
    }

    @Override
    public boolean visitIn() {
        return true;
    }

    @Override
    public boolean visitOut() {
        return true;
    }

    @Override
    public void visit(Object V) {
        elementCount++;
        RG.setComponent(V, componentCount);
    }

    @Override
    public void unvisit(Object V) {
    }
}
