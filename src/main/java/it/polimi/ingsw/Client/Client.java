package it.polimi.ingsw.Client;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.View.CLIView;
import it.polimi.ingsw.View.ClientRemoteView;
import it.polimi.ingsw.View.Communication.ConnectionMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Player player = new Player("Pippo", null, 999);

        Socket socket = null;
        ObjectOutputStream out = null;
        try {
            socket = new Socket("localhost", Server.PORT_NUMBER);

            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new ConnectionMessage(player, "la lobby più bella"));

            CLIView cli = new CLIView(player);
            ClientRemoteView remoteView = new ClientRemoteView(socket, cli);
            new Thread(cli).start();
            new Thread(remoteView).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
