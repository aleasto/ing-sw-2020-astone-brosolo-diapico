package it.polimi.ingsw.View;

public interface Observer<T> {
    public void onChange(T message);
}
