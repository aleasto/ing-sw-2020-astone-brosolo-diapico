package it.polimi.ingsw.Client;

import it.polimi.ingsw.Game.Player;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.View.CLIView;
import it.polimi.ingsw.View.ClientRemoteView;
import it.polimi.ingsw.View.Communication.CommandMessage;
import it.polimi.ingsw.View.Communication.ConnectionMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        PrintStream stdout = new PrintStream(System.out);

        Player player = null;
        String ip;
        String lobby;

        // For now just get player and lobby info from stdin
        stdout.println("Welcome. Who are you? `player name godlikelevel`");
        while (true) {
            Scanner commandScanner = new Scanner(stdin.nextLine());
            commandScanner.useDelimiter("[,\\s]+");
            try {
                String command = commandScanner.next();
                if (command.equals("player")) {
                    player = new Player(commandScanner.next(), commandScanner.nextInt());
                    stdout.println("Ok. Now connect to a lobby: `connect ip lobby`");
                } else if (command.equals("connect")) {
                    if (player == null) {
                        stdout.println("Please tell me who you are first: `player name godlikelevel`");
                        continue;
                    }
                    ip = commandScanner.next();
                    lobby = commandScanner.next();
                    break;
                }
            } catch (Exception ex) {
                stdout.println("Invalid command");
            }
        }

        // Do the connection
        try {
            Socket socket = new Socket(ip, Server.PORT_NUMBER);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new ConnectionMessage(player, lobby));

            // Start the views
            CLIView cli = new CLIView(player) {
                final ClientRemoteView remoteView = new ClientRemoteView(socket, this);

                @Override
                public void run() {
                    Thread remoteThread = new Thread(remoteView);
                    remoteThread.start();
                    super.run();

                    // When the CLI thread stops, stop the remote view too.
                    remoteThread.interrupt();
                }

                @Override
                public void onCommand(CommandMessage message) {
                    remoteView.onCommand(message);
                }
            };
            new Thread(cli).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
