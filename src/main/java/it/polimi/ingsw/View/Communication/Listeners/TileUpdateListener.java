package it.polimi.ingsw.View.Communication.Listeners;

import it.polimi.ingsw.Game.Tile;

public interface TileUpdateListener {
    // TODO: Should this be a TileMessage instead?
    void onTileUpdate(Tile message);
}
