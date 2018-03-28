package minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Solver {

    Random random=new Random(4);
    int[][] knowledge;
    int height;
    int width;
    Game game;
    boolean dead;
    boolean solved;


    public Solver(Game game) {
        this.game=game;
        this.width=game.getWidth();
        this.height=game.getHeight();
        this.dead=false;
        this.solved=false;
        this.knowledge=new int[height][width];
        for(int i =0;i<this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                this.knowledge[i][j]=-2;
            }
        }
    }

    public void markMine(int x, int y) {
        this.game.markMine(x,y);
        this.knowledge[x][y]=-1;
    }

    public boolean open(int x, int y) {

        boolean open = this.game.open(x, y);
        if(!open) {

            int bombCount = this.game.check(x, y);
            this.knowledge[x][y]=bombCount;
            if (this.knowledge[x][y] == 0) {
                for (int i = Math.max(0, x - 1); i <= Math.min(this.height - 1, x + 1); i++) {
                    for (int j = Math.max(0, y - 1); j <= Math.min(this.width - 1, y + 1); j++) {
                        if (this.knowledge[i][j] == -2 && (i != x || j != y)) {
                            dead=open(i, j);

                        }
                    }
                }
            }
        } else {
            this.dead=true;
        }
        return this.dead;
    }



    public void printKnowledge() {
        for(int i=0; i<this.height; i++) {
            for(int j=0; j<this.width; j++) {
                if(this.knowledge[i][j]==-2) {
                    System.out.printf(" # ");
                } else if(this.knowledge[i][j]==-1) {
                    System.out.printf(" * ");
                } else if(this.knowledge[i][j]==0) {
                    System.out.printf("   ");
                } else {
                    System.out.printf(" "+this.knowledge[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    class MineGuess {
        int x;
        int y;
        boolean mine;
        public MineGuess(int x, int y, boolean mine) {
            this.x=x;
            this.y=y;
            this.mine=mine;
        }
    }

    public boolean isConsistent(List<MineGuess> mineGuesses) {
        int boundingXmin=mineGuesses.get(0).x;
        int boundingXmax=mineGuesses.get(0).x;
        int boundingYmin=mineGuesses.get(0).y;
        int boundingYmax=mineGuesses.get(0).y;

        for(MineGuess guess:mineGuesses) {
            if(guess.x-1<boundingXmin) {
                boundingXmin=Math.max(0,guess.x-1);
            }
            if(guess.x+1>boundingXmax) {
                boundingXmax=Math.min(this.height-1, guess.x+1);
            }
            if(guess.y-1<boundingYmin) {
                boundingYmin=Math.max(0,guess.y-1);
            }
            if(guess.y+1>boundingYmax) {
                boundingYmax=Math.min(this.width-1, guess.y+1);
            }
        }

        for(int i=boundingXmin; i<=boundingXmax; i++) {
            for(int j=boundingYmin; j<=boundingYmax; j++) {
                if(this.knowledge[i][j]>0) {
                    int unknown=0;
                    int known=0;
                    for(int l=Math.max(i-1,0);l<=Math.min(this.height-1,i+1); l++) {
                        for(int k=Math.max(j-1,0); k<=Math.min(this.width-1,j+1); k++) {
                            if(l!=i || k!=j) {
                                if(this.knowledge[l][k]==-1) {
                                    known++;
                                } else if(this.knowledge[l][k]==-2) {
                                    MineGuess mineGuess=null;
                                    for(MineGuess m:mineGuesses) {
                                        if(m.x==l && m.y==k) {
                                            mineGuess=m;
                                        }
                                    }
                                    if(mineGuess==null) {
                                        unknown++;
                                    } else if(mineGuess.mine) {
                                        known++;
                                    }
                                }

                            }
                        }
                    }
                    if(this.knowledge[i][j]<known || this.knowledge[i][j]>known+unknown) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isCheckCandidate(int x, int y) {
        if(this.knowledge[x][y]!=-2) {
            return false;
        }

        for(int i=Math.max(0,x-1); i<=Math.min(this.height-1,x+1); i++) {
            for(int j=Math.max(0,y-1); j<=Math.min(this.width-1, y+1); j++) {
                if(this.knowledge[i][j]>0) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<List<MineGuess>> generateSinglePertubation(int x, int y) {
        List<List<MineGuess>> pertubations=new ArrayList<List<MineGuess>>();
        List<MineGuess> mineGuessesPos=new ArrayList<MineGuess>();
        mineGuessesPos.add(new MineGuess(x,y,true));
        pertubations.add(mineGuessesPos);
        List<MineGuess> mineGuessesNeg=new ArrayList<MineGuess>();
        mineGuessesNeg.add(new MineGuess(x,y,false));
        pertubations.add(mineGuessesNeg);
        return pertubations;
    }

    private List<List<MineGuess>> expandPertubations(List<List<MineGuess>> p, int x, int y) {
        List<List<MineGuess>> pertubations=new ArrayList<List<MineGuess>>();
        MineGuess mgPos=new MineGuess(x,y,true);
        for(List<MineGuess> mineGuesses:p) {
            List<MineGuess> newMineGuesses=new ArrayList<MineGuess>();
            for(MineGuess mineGuess:mineGuesses) {
                newMineGuesses.add(mineGuess);
            }
            newMineGuesses.add(mgPos);
            pertubations.add(newMineGuesses);
        }

        MineGuess mgNeg=new MineGuess(x,y,false);
        for(List<MineGuess> mineGuesses:p) {
            List<MineGuess> newMineGuesses=new ArrayList<MineGuess>();
            for(MineGuess mineGuess:mineGuesses) {
                newMineGuesses.add(mineGuess);
            }
            newMineGuesses.add(mgNeg);
            pertubations.add(newMineGuesses);
        }

        return pertubations;
    }

    private List<List<MineGuess>> generatePertubations(int x, int y) {
        List<List<MineGuess>> pertubations=generateSinglePertubation(x, y);
        for(int i=Math.max(0,x-2); i<=Math.min(height-1,x+2); i++) {
            for(int j=Math.max(0,y-2); j<=Math.min(width-1,y+2); j++) {
                if(isCheckCandidate(i,j) && (i!=x || j!=y)) {
                    pertubations=expandPertubations(pertubations, i,j);
                }
            }
        }
        return pertubations;
    }

    private boolean solveLogic() {
        boolean performedStep=false;
        for(int i=0; i<this.height; i++) {
            for(int j=0; j<this.width; j++) {
                if(!performedStep && this.isCheckCandidate(i,j)) {
                    List<List<MineGuess>> pertubations = generatePertubations(i, j);
                    List<List<MineGuess>> consitentList=new ArrayList<List<MineGuess>>();
                    for(List<MineGuess> mineGuesses: pertubations) {
                        if(isConsistent(mineGuesses)) {
                            consitentList.add(mineGuesses);
                        }
                    }
                    boolean allTrue=true;
                    boolean allFalse=true;
                    for(List<MineGuess> mineGuesses:consitentList) {
                        for(MineGuess mineGuess:mineGuesses) {
                            if(mineGuess.x==i && mineGuess.y==j) {
                                if(mineGuess.mine) {
                                    allFalse=false;
                                } else {
                                    allTrue=false;
                                }
                            }
                        }
                    }

                    if(allTrue) {
                        this.markMine(i,j);
                        performedStep=true;
                    }
                    if(allFalse) {
                        this.open(i,j);
                        performedStep=true;
                    }

                }
            }
        }
        return performedStep;
    }

    public void solveRandom() {
        int countOptions=0;
        for(int i=0; i<this.height; i++) {
            for(int j=0; j<this.width; j++) {
                if(knowledge[i][j]==-2) {
                    countOptions++;
                }
            }
        }
        int target=random.nextInt(countOptions);

        boolean done=false;
        for(int i=0; i<this.height; i++) {
            for(int j=0; j<this.width; j++) {
                if(!done && knowledge[i][j]==-2) {
                    if(target==0) {
                        this.open(i,j);
                        done=true;
                    }
                    target--;
                }
            }
        }
    }

    public void checkSolved() {
        this.solved=this.game.hasWon();
    }

    public void solve() {
        while(!dead && !solved) {
            printKnowledge();
            boolean performedStep=solveLogic();
            if(!performedStep) {
                solveRandom();
            }
            checkSolved();
        }
    }



}
