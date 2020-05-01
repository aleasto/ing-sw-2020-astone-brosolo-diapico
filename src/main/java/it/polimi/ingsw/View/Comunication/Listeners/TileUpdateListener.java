package it.polimi.ingsw.View.Comunication.Listeners;

import it.polimi.ingsw.Game.Tile;

public interface TileUpdateListener {
    // TODO: Should this be a TileMessage instead?
    void onTileUpdate(Tile message);
}
