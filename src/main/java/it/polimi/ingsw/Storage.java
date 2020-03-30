package it.polimi.ingsw;

public class Storage {
    private static final int MAX_LVL1 = 22;
    private static final int MAX_LVL2 = 18;
    private static final int MAX_LVL3 = 14;
    private static final int MAX_DOME = 14;

    private int pieceAmt[] = new int[] { MAX_LVL1, MAX_LVL2, MAX_LVL3, MAX_DOME };

    /**
     * Retrieve a piece from the storage
     * @param piece the piece type
     * @return false if no available piece of the given type, true otherwise
     */
    public boolean retrieve(int piece) {
        if (pieceAmt[piece - 1] > 0) {
            pieceAmt[piece - 1]--;
            return true;
        }
        return false;
    }

    /**
     * Get a piece type availability for display purposes
     * @param piece the piece type
     * @return the number of available pieces of `piece`
     */
    public int getAvailable(int piece) {
        return pieceAmt[piece - 1];
    }
}