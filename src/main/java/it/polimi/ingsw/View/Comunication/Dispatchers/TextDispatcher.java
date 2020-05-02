package it.polimi.ingsw.View.Comunication.Dispatchers;

import it.polimi.ingsw.View.Comunication.TextMessage;
import it.polimi.ingsw.View.Comunication.Listeners.TextListener;

import java.util.ArrayList;
import java.util.List;

// Currently unused, but i think it can be useful later on
public interface TextDispatcher {
    void addTextListener(TextListener listener);
    void removeTextListener(TextListener listener);
    void notifyText(TextMessage message);
    void onRegisterForText(TextListener listener);
}
