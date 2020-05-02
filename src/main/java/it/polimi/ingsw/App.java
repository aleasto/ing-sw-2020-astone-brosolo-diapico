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
                Server.main(new String[]{});
                break;
            case "client":
                Client.main(new String[]{});
                break;
        }
    }
}
