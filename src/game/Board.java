package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board implements Comparable<Board>{

    static final byte SIZE = 4;
    static final short VALUE_ONE = 2;
    static final short VALUE_TWO = 4;
    static final byte LEFT = 0;
    static final byte RIGHT = 1;
    static final byte UP = 2;
    static final byte DOWN = 3;
    static final Random random = new Random();

    short[][] board;

    public Board() {
        board = new short[SIZE][SIZE];
    }

    public Board(short[][] board){
        this.board = board.clone();
    }

    public static Board newBoard(){
        return (new Board().fillRandom().fillRandom());
    }


    public int move(byte direction){
        int score = 0;
        switch(direction) {
            case LEFT:
                score = moveLeft();
                break;
            case RIGHT:
                score = moveRight();
                break;
            case UP:
                score = moveUp();
                break;
            case DOWN:
                score = moveDown();
                break;
        }
        return score;
    }

    public int moveLeft(){
        int score = 0;
nextRow:for (byte r = 0; r < SIZE; r++) {
            for (byte c = 1; c < SIZE; c++) {
                if(board[r][c-1] == 0){
                    boolean empty = true;
                    for (byte c1 = c; c1 < SIZE; c1++) {
                        board[r][c1-1] = board[r][c1];
                        empty = board[r][c1]==0 && empty;
                        board[r][c1] = 0;
                    }
                    if(empty) continue nextRow;
                    c--;
                }
                else if(board[r][c] == 0){
                    boolean empty = true;
                    for (byte c1 = (byte)(c+1); c1 < SIZE; c1++) {
                        board[r][c1-1] = board[r][c1];
                        empty = board[r][c1]==0 && empty;
                        board[r][c1] = 0;
                    }
                    if(empty) continue nextRow;
                    c--;
                }
                else if(board[r][c] == board[r][c-1]){
                    board[r][c-1] = (short) (board[r][c] + board[r][c-1]);
                    score += board[r][c-1];
                    board[r][c] = 0;
                }
            }
        }

        return score;
    }

    public int moveRight(){
        int score = 0;
nextRow:for (byte r = 0; r < SIZE; r++) {
            for (byte c = SIZE-2; c >= 0; c--) {
                if(board[r][c+1] == 0){
                    boolean empty = true;
                    for (byte c1 = c; c1 >= 0; c1--) {
                        board[r][c1+1] = board[r][c1];
                        empty = board[r][c1]==0 && empty;
                        board[r][c1] = 0;
                    }
                    if(empty) continue nextRow;
                    c++;
                }
                else if(board[r][c] == 0){
                    boolean empty = true;
                    for (byte c1 = (byte)(c-1); c1 >= 0; c1--) {
                        board[r][c1+1] = board[r][c1];
                        empty = board[r][c1]==0 && empty;
                        board[r][c1] = 0;
                    }
                    if(empty) continue nextRow;
                    c++;
                }
                else if(board[r][c] == board[r][c+1]){
                    board[r][c+1] = (short) (board[r][c] + board[r][c+1]);
                    score += board[r][c+1];
                    board[r][c] = 0;
                }
            }
        }
        return score;
    }

    public int moveUp(){
       int score = 0;

nextCol:for (byte c = 0; c < SIZE; c++) {
            for (byte r = 1; r < SIZE; r++) {
                if(board[r-1][c] == 0){
                    boolean empty = true;
                    for (byte r1 = r; r1 < SIZE; r1++) {
                        board[r1-1][c] = board[r1][c];
                        empty = board[r1][c]==0 && empty;
                        board[r1][c] = 0;
                    }
                    if(empty) continue nextCol;
                    r--;
                }
                else if(board[r][c] == 0){
                    boolean empty = true;
                    for (byte r1 = (byte)(r+1); r1 < SIZE; r1++) {
                        board[r1-1][c] = board[r1][c];
                        empty = board[r1][c]==0 && empty;
                        board[r1][c] = 0;
                    }
                    if(empty) continue nextCol;
                    r--;
                }
                else if(board[r][c] == board[r-1][c]){
                    board[r-1][c] = (short) (board[r][c] + board[r-1][c]);
                    score +=  board[r-1][c];
                    board[r][c] = 0;
                }
            }
        }
        return score;
    }

    public int moveDown(){
        int score = 0;
nextCol:for (byte c = 0; c < SIZE; c++) {
            for (byte r = SIZE-2; r >= 0; r--) {
                if(board[r+1][c] == 0){
                    boolean empty = true;
                    for (byte r1 = r; r1 >= 0; r1--) {
                        board[r1+1][c] = board[r1][c];
                        empty = board[r1][c]==0 && empty;
                        board[r1][c] = 0;
                    }
                    if(empty) continue nextCol;
                    r++;
                }
                else if(board[r][c] == 0){
                    boolean empty = true;
                    for (byte r1 = (byte)(r-1); r1 >= 0; r1--) {
                        board[r1+1][c] = board[r1][c];
                        empty = board[r1][c]==0 && empty;
                        board[r1][c] = 0;
                    }
                    if(empty) continue nextCol;
                    r++;
                }
                else if(board[r][c] == board[r+1][c]){
                    board[r+1][c] = (short) (board[r][c] + board[r+1][c]);
                    score +=  board[r+1][c];
                    board[r][c] = 0;
                }
            }
        }
        return score;
    }

    public boolean hasNextMoves(){
        Board left = clone();
        Board right = clone();
        Board up = clone();
        Board down = clone();

        left.moveLeft();
        right.moveRight();
        up.moveUp();
        down.moveDown();

        if(equals(left) && equals(right) && equals(up) && equals(down)) return false;

        return true;
    }

    public Board fillRandom(){
        if(emptyLocations().size()==0) return this;
        Location newLoc = randomEmptyLocation();
        short value = random.nextBoolean()?VALUE_ONE:VALUE_TWO;
        board[newLoc.row][newLoc.col] = value;
        return this;
    }

    public Board fillLocation(Location loc, short value){
        board[loc.row][loc.col] = value;
        return this;
    }

    Location randomEmptyLocation() {
        List<Location> emptyLocs = emptyLocations();
        return emptyLocs.get(random.nextInt(emptyLocs.size()));
    }

    List<Location> emptyLocations(){
        ArrayList<Location> emptyLocs = new ArrayList<>();
        for (byte r = 0; r < SIZE; r++) {
            for (byte c = 0; c < SIZE; c++) {
                if(board[r][c] == 0){
                    emptyLocs.add(new Location(r,c));
                }
            }
        }
        return emptyLocs;
    }

    List<Location> filledLocations(){
        ArrayList<Location> filledLocs= new ArrayList<>();
        for (byte r = 0; r < SIZE; r++) {
            for (byte c = 0; c < SIZE; c++) {
                if(board[r][c] != 0){
                    filledLocs.add(new Location(r, c));
                }
            }
        }
        return filledLocs;
    }

    short valueAt(Location location){
        return board[location.row][location.col];
    }

    //Naive implementation
    //TODO:: implement better heuristic comparison
    //TODO:: make consistent with equals() like it is supposed to be
    @Override
    public int compareTo(Board other) {
        List<Location> filledLocs = filledLocations();
        List<Location> othersFilledLocs = other.filledLocations();

        if(filledLocs.size() != othersFilledLocs.size()){
            return filledLocs.size()-othersFilledLocs.size();
        }

        return sum() - other.sum();
    }

    public boolean equals(Object o){
        if(! (o instanceof Board)) return false;
        return hashCode() == o.hashCode();
    }

    public int sum(){
        return filledLocations().parallelStream().mapToInt(loc -> valueAt(loc)).sum();
    }

    @Override
    public String toString(){
        String str = "";
        for (byte r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                str += String.format("%6d",board[r][c]);
            }
            str+= "\n";
        }
        return str.trim();
    }

    public Board clone(){
        short[][] newBoard = new short[SIZE][SIZE];
        for (byte r = 0; r < SIZE; r++) {
            newBoard[r]=board[r].clone();
        }
        return new Board(newBoard);
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    class Location{
        public byte row;
        public byte col;
        public Location(byte row, byte col){
            this.row = row;
            this.col = col;
        }
    }
}
