package it.polimi.ingsw.Game;

import it.polimi.ingsw.Exceptions.InvalidBuildActionException;
import it.polimi.ingsw.Exceptions.InvalidCommandException;
import it.polimi.ingsw.Exceptions.InvalidMoveActionException;

import java.util.List;

public interface GameState {
    /**
     * Handle a move command in a specific game phase
     *
     * @param player the player that invokes this call
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     * @throws InvalidCommandException depending on implementation
     * @throws InvalidMoveActionException depending on implementation
     */
    default void Move(Player player, int fromX, int fromY, int toX, int toY) throws InvalidCommandException, InvalidMoveActionException {
        throw new InvalidCommandException("You cannot perform a move action now");
    }

    /**
     * Handle a build command in a specific game phase
     *
     * @param player the player that invokes this call
     * @param fromX the starting X coordinate on the board
     * @param fromY the starting Y coordinate on the board
     * @param toX the destination X coordinate on the board
     * @param toY the destination Y coordinate on the board
     * @throws InvalidCommandException depending on implementation
     * @throws InvalidBuildActionException depending on implementation
     */
    default void Build(Player player, int fromX, int fromY, int toX, int toY, int lvl) throws InvalidCommandException, InvalidBuildActionException {
        throw new InvalidCommandException("You cannot perform a build action now");
    }

    /**
     * Handle turn change
     * @param previousPlayer the player ending its turn
     * @param newPlayer the player beginning its turn
     * @param lose has the previous player lost
     * @throws InvalidCommandException depending on implementation
     */
    default void EndTurn(Player previousPlayer, Player newPlayer, boolean lose) throws InvalidCommandException {
        throw new InvalidCommandException("You cannot end your turn now");
    }

    /**
     * Handle setting a god-pool
     *
     * @param player the player performing this action
     * @param godPool the chosen god-pool
     * @throws InvalidCommandException depending on implementation
     */
    default void SetGodPool(Player player, List<String> godPool) throws InvalidCommandException {
        throw new InvalidCommandException("You cannot set a god pool now");
    }

    /**
     * Handle choosing a god
     *
     * @param player the player performing this action
     * @param god the chosen god
     * @throws InvalidCommandException depending on implementation
     */
    default void SetGod(Player player, String god) throws InvalidCommandException {
        throw new InvalidCommandException("You cannot choose your god now");
    }

    /**
     * Handle placing down a worker
     *
     * @param player the player performing this action
     * @param x the x-axis coordinate on the board the worker will be placed at
     * @param y the y-axis coordinate on the board the worker will be placed at
     * @throws InvalidCommandException depending on implementation
     */
    default void PlaceWorker(Player player, int x, int y) throws InvalidCommandException {
        throw new InvalidCommandException("You cannot place down a worker now");
    }

    /**
     * Check if the player should lose the game
     * @param player the player
     * @return true if the player should lose now
     */
    default boolean checkLose(Player player) {
        return false;
    }
}
