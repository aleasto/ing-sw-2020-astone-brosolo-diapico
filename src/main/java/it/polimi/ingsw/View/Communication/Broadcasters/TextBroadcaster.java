package it.polimi.ingsw.View.Communication.Broadcasters;

import it.polimi.ingsw.View.Communication.TextMessage;
import it.polimi.ingsw.View.Communication.Listeners.TextListener;

// Currently unused, but i think it can be useful later on
public interface TextBroadcaster {
    void addTextListener(TextListener listener);
    void removeTextListener(TextListener listener);
    void notifyText(TextMessage message);
    void onRegisterForText(TextListener listener);
}
