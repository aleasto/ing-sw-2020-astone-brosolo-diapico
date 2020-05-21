package it.polimi.ingsw;


import it.polimi.ingsw.Client.CLI;
import it.polimi.ingsw.Client.JavaFX;
import it.polimi.ingsw.Server.Server;
import javafx.application.Application;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        if (args.length < 1) {
            fatal("You must specify server or client");
        }

        switch (args[0]) {
            case "server":
                new Server().start();
                break;
            case "client":
                if (args.length < 2) {
                    fatal("You must specify cli or gui");
                }
                switch (args[1]) {
                    case "cli":
                        new CLI().start();
                        break;
                    case "gui":
                        // We shall go through Application.launch
                        Application.launch(JavaFX.class, Arrays.copyOfRange(args, 2, args.length));
                        break;
                    default:
                        fatal("Unrecognized client args");
                }
                break;
            default:
                fatal("Unrecognized args");
        }
    }

    public static void fatal(String message) {
        System.err.println(message);
        System.exit(1);
    }
}
