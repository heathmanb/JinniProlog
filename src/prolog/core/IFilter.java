package prolog.core;

import prolog.logic.*;

public interface IFilter extends Stateful {

    public Object filterVertex(Object VData);

    public Object filterEdge(Object EData);
}
