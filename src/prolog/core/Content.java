package prolog.core;

import prolog.logic.*;

public final class Content implements Stateful {

    Object key;
    Object value;
    final Object data;

    public Content(Object key, Object value, Object data) {
        this.key = key;
        this.value = value;
        this.data = data;
    }

    @Override
    public String toString() {
        return "(" + key + "," + value + "," + data + ")";
    }
}
