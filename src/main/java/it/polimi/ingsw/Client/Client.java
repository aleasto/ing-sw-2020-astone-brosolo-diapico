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

    // For easy debugging
    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        Scanner stdin = new Scanner(System.in);
        PrintStream stdout = new PrintStream(System.out);

        Player player;

        // For now just get player and lobby info from stdin
        stdout.println("Welcome. Who are you? `name godlikelevel`");
        while (true) {
            Scanner commandScanner = new Scanner(stdin.nextLine());
            commandScanner.useDelimiter("[,\\s]+");
            try {
                player = new Player(commandScanner.next(), commandScanner.nextInt());
                break;
            } catch (Exception ex) {
                stdout.println("Invalid command");
            }
        }

        CLIView cli = new CLIView(player);
        cli.run(); // CLI is runnable, but we can just run it on this thread too
    }
}
