package de.herolabs.square.game;

/**
 * Created by jakoblange on 02.05.2015.
 */
public class pathTile {

    private boolean passable;

    public pathTile(boolean passable) {
        this.passable = passable;
    }

    public boolean isPassable() {
        return passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }


}
