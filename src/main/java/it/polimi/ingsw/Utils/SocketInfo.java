package it.polimi.ingsw.Utils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;

public class SocketInfo {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final Timer pingTimer;

    public SocketInfo(Socket socket, ObjectOutputStream out, ObjectInputStream in, Timer pingTimer) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.pingTimer = pingTimer;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public Timer getPingTimer() {
        return pingTimer;
    }
}
