package minesweeper;

public interface Game {

    public int getHeight();
    public int getWidth();
    public void markMine(int x, int y);
    public boolean hasWon();
    public boolean isDead();
    public int check(int x, int y);
    public boolean open(int x, int y);
}
