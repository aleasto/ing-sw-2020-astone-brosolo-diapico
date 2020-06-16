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

    /**
     * Compare this pair to another
     *
     * @param obj the other object
     * @return true if both first and second are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pair)) return false;
        Pair other = (Pair) obj;
        return first.equals(other.first) && second.equals(other.second);
    }
}
