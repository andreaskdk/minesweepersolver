package minesweeper;

import java.util.Random;

public class RandomGame implements Game {

    Random random=new Random(4);
    int[][] state;
    int[][] knowledge;
    int height;
    int width;
    double density=0.08;
    boolean dead;
    boolean solved;


    public RandomGame(int height, int width) {
        this.height=height;
        this.width=width;
        this.state=new int[height][width];
        for(int i =0;i<this.height; i++) {
            for(int j=0;j<this.width; j++) {
                if(random.nextDouble()<density) {
                    this.state[i][j] = -1;
                } else {
                    this.state[i][j]=0;
                }
            }
        }

        for(int i =0;i<this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                int count=0;
                for(int k=Math.max(0,i-1); k<=Math.min(i+1, this.height-1); k++){
                    for(int l=Math.max(0,j-1); l<=Math.min(j+1, this.width-1); l++) {
                        if(i!=k || j!=l) {
                            if(this.state[k][l]==-1) {
                                count++;
                            }
                        }
                    }
                }
                if(this.state[i][j]==0) {
                    this.state[i][j]=count;
                }

            }
        }
        this.knowledge=new int[height][width];
        for(int i =0;i<this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                this.knowledge[i][j]=-2;
            }
        }
        this.dead=false;
        this.solved=false;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public void markMine(int x, int y) {
        this.knowledge[x][y]=-1;
    }

    public boolean hasWon() {
        for(int i=0; i<this.height; i++) {
            for(int j=0; j<this.width; j++) {
                if(knowledge[i][j]==-2) {
                    solved=false;
                    return false;
                }
            }
        }
        solved=true;
        return true;
    }

    public boolean isDead() {
        return false;
    }

    public int check(int x, int y) {
        return knowledge[x][y];
    }

    public boolean open(int x, int y) {
        if(this.state[x][y]==-1) {
            this.dead=true;
            return true;
        }
        this.knowledge[x][y]=this.state[x][y];

        return false;
    }

    public void printState() {
        for(int i=0; i<this.height; i++) {
            for(int j=0; j<this.width; j++) {
                if(this.state[i][j]==-1) {
                    System.out.printf("*");
                } else {
                    System.out.printf(""+this.state[i][j]);
                }
            }
            System.out.println();
        }
    }

    static public void main(String[] args) {
        RandomGame g=new RandomGame(14,18);
        Solver s=new Solver(g);
        s.solve();
        if(s.solved) {
            System.out.println("Solved");
        }
        if(s.dead) {
            System.out.println("Dead");
        }
        s.printKnowledge();
        g.printState();
    }
}
