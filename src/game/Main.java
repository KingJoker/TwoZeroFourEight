package game;


import java.util.ArrayList;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args){
        short board[][] = new short[][]{{0,0,0,0},
                                        {0,0,0,0},
                                        {0,0,0,0},
                                        {0,0,0,0}};
       /* Board myBoard = Board.newBoard();
        BoardState state = new BoardState(myBoard);*/
       // System.out.println("Initial:\n"+state+"\n");

        long start = System.currentTimeMillis();


        for(int depthDiv = 250; depthDiv >= 150; depthDiv -= 50){
            ArrayList<Integer> sums = new ArrayList<>();

            for(int i = 1; i<=5; i++){
                int counter = 0;
                BoardState state = new BoardState(Board.newBoard(),depthDiv);
                long startIteration = System.currentTimeMillis();
                while(!state.isFinished()) {
                    long startMove = System.currentTimeMillis();
                    byte nextDirection = state.findNextMove();
                    long endMove = System.currentTimeMillis();
                    state = state.move(nextDirection);
                    counter++;
                    if(true){//counter % 25 == 0){
                        System.out.println("Depth Divisor: " + BoardState.getDepthDivisor());
                        System.out.println("Iteration #: " + i);
                        System.out.println("Move #: "+ counter);
                        System.out.println("Direction: " + nextDirection);
                        System.out.println("Score: "+state.getScore());
                        System.out.println("Search Depth: "+ state.getDepth());
                        System.out.println("Move Execution Time (ms): " + ((endMove-startMove)));
                        System.out.println("Iteration execution time (s): " +  ((System.currentTimeMillis()-startIteration)/1000));
                        System.out.println("Total execution time (s): " + ((System.currentTimeMillis()-start)/1000));
                        System.out.println(state + "\n");
                    }

                }
                sums.add(state.sum());
                System.out.printf(".%d.\n",sums.get(i-1));
            }
           //System.out.printf(".%d.\n",sums.get(i-1));
            double sum = sums.stream().mapToInt(p -> p).sum();
            System.out.printf("Divisor: %d \nAvg: %f \n\n",depthDiv,(sum/5));
        }

        /*while(!state.isFinished()) {
            long startLoop = System.currentTimeMillis();
            byte nextDirection = state.findNextMove();
            state.move(nextDirection);
            System.out.println("Move #: "+ ++counter);
            System.out.println("Direction: " + nextDirection);
            System.out.println("Execution Time: " + ((System.currentTimeMillis()-startLoop)/1000));
            System.out.println("Total execution time: " + ((System.currentTimeMillis()-start)/1000));
            System.out.println(state + "\n");
        }*/

        System.out.println("Total execution time: " + ((System.currentTimeMillis()-start)/1000));
    }
}
