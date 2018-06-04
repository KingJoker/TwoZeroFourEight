package game;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.stream.Stream;



public class BoardState {

    public static final int DEPTH = 2;

    public static int getDepthDivisor() {
        return depthDivisor;
    }

    static int depthDivisor = 100;
    int score;
    private static Map<Board,BoardState> cache = new ConcurrentHashMap<>();
    private static Map<Byte,BoardState> directions = new ConcurrentHashMap<>();
    static int cacheHits = 0;

    private Board board;

    public BoardState(){
        board = new Board();
        score = 0;
    }

    public BoardState(Board board) {
        this();
        this.board = board;
    }

    //TODO:: score should not be 0 if board is not clean
    public BoardState(Board board, int depthDivisor){
        this(board);
        this.depthDivisor = depthDivisor;
        this.score = 0;
    }

    public static BoardState getOrCreate(Board board){
        if(cache.containsKey(board)){
            cacheHits++;
            return cache.get(board);
        }
        BoardState temp = new BoardState(board);
        cache.put(board,temp);
        return temp;

    }

    public static BoardState getOrCreate(Board board, int score){
        BoardState temp = getOrCreate(board);
        temp.setScore(score);
        return temp;
    }

    public byte findNextMove() {
        
        BoardState moveLeft = moveLeft();
        BoardState moveRight = moveRight();
        BoardState moveUp = moveUp();
        BoardState moveDown = moveDown();

        Collection<BoardState> left =equals(moveLeft)? Collections.synchronizedSet(new HashSet<BoardState>()):moveLeft.permutate();
        Collection<BoardState> right = equals(moveRight)? Collections.synchronizedSet(new HashSet<BoardState>()):moveRight.permutate();
        Collection<BoardState> up = equals(moveUp)? Collections.synchronizedSet(new HashSet<BoardState>()):moveUp.permutate();
        Collection<BoardState> down = equals(moveDown)? Collections.synchronizedSet(new HashSet<BoardState>()):moveDown.permutate();

        /*int depth = 1; //(int)Math.floor((board.SIZE*board.SIZE - board.emptyLocations().size())/4);
        if(board.emptyLocations().size() <3) depth = 5;
        else if(board.emptyLocations().size() <5) depth = 4;
        else if(board.emptyLocations().size() <7) depth = 3;
        else if (board.emptyLocations().size() <9) depth = 2;
        else if(board.emptyLocations().size() < 9 ) depth = 2;*/

        int depth = getDepth();

        //int depth = (int)Math.pow(board.sum(),1/6);

        //else if(board.emptyLocations().size()<10) depth = 4;

        for (int i = 0; i < depth; i++) {
            if(left.size()>20000 || right.size() > 20000 || up.size() >20000 || down.size() > 20000)
                break;
            /*
                ArrayList<Collection<BoardState>> list = new ArrayList<>();
            list.add(left);
            list.add(right);
            list.add(up);
            list.add(down);

            list.parallelStream().forEach(p -> p = permutateDirections(p));
             */
            left = permutateDirections(left);
            right = permutateDirections(right);
            up = permutateDirections(up);
            down = permutateDirections(down);
        }

        

        /*double leftAvg = left.size()==0? 0: left.parallelStream().mapToDouble(bs -> bs.board.emptyLocations().size()).sum()/left.size();
        double rightAvg = right.size()==0? 0: right.parallelStream().mapToDouble(bs -> bs.board.emptyLocations().size()).sum()/right.size();
        double upAvg = up.size()==0? 0: up.parallelStream().mapToDouble(bs -> bs.board.emptyLocations().size()).sum()/up.size();
        double downAvg = down.size()==0? 0: down.parallelStream().mapToDouble(bs -> bs.board.emptyLocations().size()).sum()/down.size();
          */
       double leftScoreAvg = left.size()==0? 0: left.parallelStream().mapToDouble(bs -> bs.getScore()).sum()/left.size();
        double rightScoreAvg = right.size()==0? 0: right.parallelStream().mapToDouble(bs -> bs.getScore()).sum()/right.size();
        double upScoreAvg = up.size()==0? 0: up.parallelStream().mapToDouble(bs -> bs.getScore()).sum()/up.size();
        double downScoreAvg = down.size()==0? 0: down.parallelStream().mapToDouble(bs -> bs.getScore()).sum()/down.size();


        // All of this is in case two directions have equal averages, choose a random direction
        ArrayList<Byte> leftMapList= new ArrayList<>();
        leftMapList.add(Board.LEFT);

        Map<Double,ArrayList<Byte>> sizes = new TreeMap<>(Collections.reverseOrder());
        sizes.put(leftScoreAvg,leftMapList);

        if(sizes.containsKey(rightScoreAvg)){
            sizes.get(rightScoreAvg).add(Board.RIGHT);
        }
        else{
            ArrayList<Byte> rightMapList= new ArrayList<>();
            rightMapList.add(Board.RIGHT);
            sizes.put(rightScoreAvg,rightMapList);
        }

        if(sizes.containsKey(upScoreAvg)){
            sizes.get(upScoreAvg).add(Board.UP);
        }
        else{
            ArrayList<Byte> upMapList= new ArrayList<>();
            upMapList.add(Board.UP);
            sizes.put(upScoreAvg,upMapList);
        }

        if(sizes.containsKey(downScoreAvg)){
            sizes.get(downScoreAvg).add(Board.DOWN);
        }
        else{
            ArrayList<Byte> downMapList= new ArrayList<>();
            downMapList.add(Board.DOWN);
            sizes.put(downScoreAvg,downMapList);
        }


        /*ArrayList<Byte> leftScoreList= new ArrayList<>();
        leftScoreList.add(Board.LEFT);
        Map<Double,ArrayList<Byte>> scores = new TreeMap<>(Collections.reverseOrder());
        scores.put(leftScoreAvg,leftScoreList);

        if(scores.containsKey(rightScoreAvg)){
            scores.get(rightScoreAvg).add(Board.RIGHT);
        }
        else{
            ArrayList<Byte> rightScoreList= new ArrayList<>();
            rightScoreList.add(Board.RIGHT);
            scores.put(rightScoreAvg,rightScoreList);
        }

        if(scores.containsKey(upScoreAvg)){
            scores.get(upScoreAvg).add(Board.UP);
        }
        else{
            ArrayList<Byte> upScoreList= new ArrayList<>();
            upScoreList.add(Board.UP);
            scores.put(upScoreAvg,upScoreList);
        }

        if(scores.containsKey(downScoreAvg)){
            scores.get(downScoreAvg).add(Board.DOWN);
        }
        else{
            ArrayList<Byte> downScoreList= new ArrayList<>();
            downScoreList.add(Board.DOWN);
            scores.put(downScoreAvg,downScoreList);
        }*/


       // ArrayList<Byte> bestMovesScore = scores.get(scores.keySet().toArray()[0]);
        ArrayList<Byte> bestMovesSize = sizes.get(sizes.keySet().toArray()[0]);
       // if(bestMovesScore.size() > 1) return bestMovesScore.get(Board.random.nextInt(bestMovesScore.size()));
        return bestMovesSize.get(Board.random.nextInt(bestMovesSize.size()));
    }

    public BoardState move(byte direction){
        Board tempBoard = board.clone();
        int tempScore = tempBoard.move(direction);
        tempBoard.fillRandom();
        return getOrCreate(tempBoard,score+tempScore);
    }

    private BoardState moveDown() {
        if(directions.containsKey(Board.DOWN))
            return directions.get(Board.DOWN);

        Board tempBoard = board.clone();
        int tempScore = tempBoard.moveDown();
        BoardState tempState = getOrCreate(tempBoard,score+tempScore);
        directions.put(Board.DOWN,tempState);
        return tempState;
    }

    private BoardState moveUp() {

        if(directions.containsKey(Board.UP))
            return directions.get(Board.UP);
        Board tempBoard = board.clone();
        int tempScore = tempBoard.moveUp();
        BoardState tempState = getOrCreate(tempBoard,score+tempScore);
        directions.put(Board.UP,tempState);
        return tempState;
    }

    private BoardState moveRight() {

        if(directions.containsKey(Board.RIGHT))
            return directions.get(Board.RIGHT);

        Board tempBoard = board.clone();
        int tempScore = tempBoard.moveRight();
        BoardState tempState = getOrCreate(tempBoard,score+tempScore);
        directions.put(Board.RIGHT,tempState);
        return tempState;
    }

    private BoardState moveLeft() {

        if(directions.containsKey(Board.LEFT))
            return directions.get(Board.LEFT);
        Board tempBoard = board.clone();
        int tempScore = tempBoard.moveLeft();
        BoardState tempState = getOrCreate(tempBoard,score+tempScore);
        directions.put(Board.LEFT,tempState);
        return tempState;
    }

    public int sum(){
        return board.sum();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDepth(){
        return 1 + board.sum() / depthDivisor;
    }

    public boolean isFinished(){
       return ! board.hasNextMoves();
    }
    
    public Collection<BoardState> permutateDirections(Collection<BoardState> states){
        Collection<BoardState> ret = Collections.synchronizedSet(new HashSet<>());//Collections.synchronizedCollection(new ArrayList<BoardState>());

        states.parallelStream().forEach(bs -> {{BoardState temp = moveLeft(); if(bs.equals(temp)) ret.add(bs); else ret.addAll(temp.permutate());}
                                                {BoardState temp = moveRight(); if(bs.equals(temp)) ret.add(bs); else ret.addAll(temp.permutate());}
                                                {BoardState temp = moveUp(); if(bs.equals(temp)) ret.add(bs); else ret.addAll(temp.permutate());}
                                                {BoardState temp = moveDown(); if(bs.equals(temp)) ret.add(bs); else ret.addAll(temp.permutate());}});
        return ret;
    }

    public Collection<BoardState> permutate(){



        Collection<BoardState> newStates = /*Collections.synchronizedList(new ArrayList<BoardState>());*/Collections.synchronizedSet(new HashSet<BoardState>());
        List<Board.Location> emptyLocs = board.emptyLocations();
        if(emptyLocs.size() == 0) {newStates.add(this); return newStates;}


        Board.Location nextLoc = emptyLocs.get(Board.random.nextInt(emptyLocs.size()));

        //newStates.add(new BoardState(board.clone().fillLocation(nextLoc, Board.VALUE_ONE)));
        //newStates.add(new BoardState(board.clone().fillLocation(nextLoc, Board.VALUE_TWO)));

        board.emptyLocations().parallelStream()
                                .forEach(p -> {newStates.add(getOrCreate(board.clone().fillLocation(p, Board.VALUE_ONE)));
                                                newStates.add(getOrCreate(board.clone().fillLocation(p, Board.VALUE_TWO)));});
        
        /*board.emptyLocations().parallelStream()
                .forEach(p -> newStates.add(new BoardState(board.clone().fillLocation(p, Board.VALUE_TWO))));*/

        /*List<BoardState> ret = Collections.synchronizedList(new ArrayList<BoardState>());
        ret.addAll(newStates);

        newStates.parallelStream().forEach(b -> ret.addAll(b.permutate(depth - 1)));*/

/*        for (BoardState boardState : newStates) {
            ret.addAll(boardState.permutate(depth - 1));
        }*/

        return newStates;
    }



    public BoardState clone(){
        return getOrCreate(board.clone(),score);
    }

    public String toString(){
        return board.toString();
    }

    public boolean equals(Object o){
        if(!(o instanceof BoardState)) return false;
        return hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return board.hashCode();
    }


    /* public Stream<BoardState> fillAll(){

    }*/
}
