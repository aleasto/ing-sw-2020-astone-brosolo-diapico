package it.polimi.ingsw;


import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Server.Server;

public class App {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("You must specify server or client");
            System.exit(1);
        }

        switch (args[0]) {
            case "server":
                new Server().start();
                break;
            case "client":
                new Client().start();
                break;
        }
    }
}
