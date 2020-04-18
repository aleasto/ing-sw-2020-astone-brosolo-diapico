package it.polimi.ingsw.View;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable<T> {

    private List<Observer<T>> observers = new ArrayList<>();

    public void registerObserver(Observer<T> observer){
        synchronized (observers) {
            observers.add(observer);
        }
        // Allow sending initial data
        onRegister(observer);
    }

    public void unregisterObserver(Observer<T> observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    protected void notifyChange(T message){
        synchronized (observers) {
            for (Observer<T> observer : observers) {
                observer.onChange(message);
            }
        }
    }

    protected void onRegister(Observer<T> obs) {};
}
