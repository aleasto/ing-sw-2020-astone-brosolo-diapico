package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.TextMessage;
import it.polimi.ingsw.View.Comunication.Listeners.TextListener;

import java.util.ArrayList;
import java.util.List;

public interface TextDispatcher {
    List<TextListener> listeners = new ArrayList<>();

    default void addTextListener(TextListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
        onRegisterForText(listener);
    }

    default void removeTextListener(TextListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    default void notifyText(TextMessage message) {
        synchronized (listeners) {
            for (TextListener listener : listeners) {
                listener.onText(message);
            }
        }
    }

    void onRegisterForText(TextListener listener);
}
