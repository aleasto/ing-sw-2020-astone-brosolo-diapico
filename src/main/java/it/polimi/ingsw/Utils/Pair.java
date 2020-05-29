package it.polimi.ingsw.Utils;

import java.io.Serializable;

public final class Pair<A,B> implements Serializable {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
