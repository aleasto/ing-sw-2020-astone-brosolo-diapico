package it.polimi.ingsw.Game;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;

import java.util.List;

public interface GameState {
    default void Move(Player player, int fromX, int fromY, int toX, int toY) throws InvalidCommandException, InvalidMoveActionException {
        throw new InvalidCommandException("You cannot perform a move action now");
    }
    default void Build(Player player, int fromX, int fromY, int toX, int toY, int lvl) throws InvalidCommandException, InvalidBuildActionException {
        throw new InvalidCommandException("You cannot perform a build action now");
    }
    default void EndTurn(Player previousPlayer, Player newPlayer) throws InvalidCommandException {
        throw new InvalidCommandException("You cannot end your turn now");
    }
    default void SetGodPool(Player player, List<String> godPool) throws InvalidCommandException {
        throw new InvalidCommandException("You cannot set a god pool now");
    }
    default void SetGod(Player player, String god) throws InvalidCommandException {
        throw new InvalidCommandException("You cannot choose your god now");
    }
    default void PlaceWorker(Player player, int x, int y) throws InvalidCommandException {
        throw new InvalidCommandException("You cannot place down a worker now");
    }
}
