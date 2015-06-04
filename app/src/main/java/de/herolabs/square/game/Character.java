package de.herolabs.square.game;

import de.herolabs.square.GameActivity;

/**
 * Created by jakoblange on 03.05.2015.
 */
public class Character {

    int posX, posY;


    public Character(int x, int y) {
        posX = x;
        posY = y;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int posX) {
        if (posX < 0 || posX > GameActivity.SIZE - 1) {
        } else {
            this.posX = posX;
        }
    }

    public void setPosY(int posY) {
        if (posY < 0 || posY > GameActivity.SIZE - 1) {
        } else {
            this.posY = posY;
        }
    }

    public void moveUp() {
        setPosY(getPosY() - 1);
    }

    public void moveRight() {
        setPosX(getPosX() + 1);
    }
}
