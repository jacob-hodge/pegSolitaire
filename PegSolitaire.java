import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

class PegSolitaire {
    // Structure of board
    // -1 -1  1  1  1 -1 -1
    // -1 -1  1  1  1 -1 -1
    //  1  1  1  1  1  1  1
    //  1  1  1  0  1  1  1
    //  1  1  1  1  1  1  1
    // -1 -1  1  1  1 -1 -1
    // -1 -1  1  1  1 -1 -1
    class Move implements Comparable<Move>{
        int from;
        int hole;
        int to;
        Move(int from, int hole, int to) {
            this.from = from;
            this.hole = hole;
            this.to = to;
        }
        public String toString()
        {
            return("(" + this.from + "," + this.hole + "," + this.to + ")");
        }
        @Override
        public int compareTo(Move  m) {
            //See if from location of each move is less than from location from other moves
            return Integer.valueOf(this.from).compareTo(Integer.valueOf(m.from));
        }
    }

    ArrayList<ArrayList<Integer>> grid;
    ArrayList<Move> movesList;
    ArrayList<ArrayList<ArrayList<Integer>>> unsuccessfulGrid;
    int gridSize;

    PegSolitaire(ArrayList<ArrayList<Integer>> grid) {
        this.grid = grid;
        this.gridSize = grid.size();
        movesList = new ArrayList<>();
        unsuccessfulGrid = new ArrayList<>();
    }
    private void printOutput(){
        for(Move move : movesList) {
            System.out.println(move.toString());
        }
    }
    private void displayGrid() {
        for(ArrayList<Integer> line: grid) {
            for(int i : line) {
                if(i == -1) {
                    System.out.print("- ");
                } else {
                    System.out.print(Integer.toString(i) + " ");
                }
            }
            System.out.println();
        }
    }
    private void makeMove(Move move) {
        grid.get(move.from/gridSize).set(move.from %gridSize, 0);
        grid.get(move.hole/gridSize).set(move.hole %gridSize, 0);
        grid.get(move.to/gridSize).set(move.to %gridSize, 1);
        movesList.add(move);
        //Moves defined as:
        //00 01 02 03 04 05 06
        //07 08 09 10 11 12 13
        //14 15 16 17 18 19 20
        //21 22 23 24 25 26 27
        //28 29 30 31 32 33 34
        //35 36 37 38 39 40 41
        //42 43 44 45 46 47 48
    }

    private void undoMove(Move move) {
        grid.get(move.from/gridSize).set(move.from %gridSize, 1);
        grid.get(move.hole/gridSize).set(move.hole %gridSize, 1);
        grid.get(move.to/gridSize).set(move.to %gridSize, 0);
        movesList.remove(movesList.size()-1); //removing last move added
    }
    private ArrayList<Move> computePossibilities() {
        ArrayList<Move> possibilities = new ArrayList<>();
        //For every 0, need to see if there are marbles 2 steps ahead in all directions
        //If so, need to see if there is a marble 1 step away so it can jump
        for(int i=0; i<grid.size(); i++) {
            for(int j=0;j<grid.get(i).size(); j++) {
                if(grid.get(i).get(j) == 0) {
                    //Check if there is no boundary (-1) or hole (0):
                    if(i-2 >= 0){
                        if((grid.get(i-2).get(j) == 1) && (grid.get(i-1).get(j) == 1)) {
                            possibilities.add(new Move((i - 2)*gridSize + j,(i-1)*gridSize + j,i*gridSize+j));
                        }
                    }
                    if(j-2 >=0) {
                        if((grid.get(i).get(j-2) == 1) && (grid.get(i).get(j-1) == 1)) {
                            possibilities.add(new Move(i*gridSize + j-2, i*gridSize + j-1, i*gridSize+j));
                        }
                    }
                    if (i+2 <= gridSize - 1) {
                        if ((grid.get(i+2).get(j) == 1) && (grid.get(i+1).get(j) == 1)) {
                            possibilities.add(new Move((i+2)*gridSize+j, (i+1)*gridSize+j, i*gridSize+j));
                        }
                    }
                    if (j+2 <= gridSize - 1) {
                        if ((grid.get(i).get(j+2) == 1)&& (grid.get(i).get(j+1) == 1)) {
                            possibilities.add(new Move(i*gridSize+j+2, i*gridSize+j+1, i*gridSize+j));
                        }
                    }

                }
            }
        }
        return possibilities;
    }
    private int getCount() {
        int count = 0;
        for(int i=0;i<gridSize;i++){
            for (int j =0;j<grid.get(i).size();j++) {
                if(grid.get(i).get(j) == 1) {
                    count++;
                }
            }
        }
        return count;
    }
    private ArrayList<ArrayList<Integer>> deepCopy(ArrayList<ArrayList<Integer>> input) {
        ArrayList<ArrayList<Integer>> newGrid = new ArrayList<>();
        for(ArrayList<Integer> line : input) {
            ArrayList<Integer> cpLine = new ArrayList<>();
            for(Integer i : line) {
                cpLine.add(Integer.valueOf(i));
            }
            newGrid.add(cpLine);
        }
        return newGrid;
    }
    public boolean solve() {
        //Checking for if we either can't progress or if we've already seen that board before

        if(unsuccessfulGrid.contains(grid)) {
            return false;
        }
        if(getCount() == 1) {
            displayGrid();
            printOutput();
            return true;
        } else {
            ArrayList<Move> moves = computePossibilities();
            //Algorithm works better if you start from the top and move down
            Collections.sort(moves);

            for(Move move : moves) {
                makeMove(move);
                if(solve()){
                    return true;
                } else {
                    undoMove(move);
                }
            }
        }
        if(!unsuccessfulGrid.contains(grid)){
            unsuccessfulGrid.add(deepCopy(grid));
        }
        return false;
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter the size of the grid (e.g., 8 for an 8x8 grid): ");
        int gridSize = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        ArrayList<ArrayList<Integer>> grid = new ArrayList<>();

        System.out.println("Please enter the grid shape row by row (-1 for empty, 0 for hole, 1 for peg):");
        for (int i = 0; i < gridSize; i++) {
            System.out.print("Enter row " + (i + 1) + " (separate " + gridSize + " integers by spaces): ");
            String[] inputRow = scanner.nextLine().trim().split("\\s+");
            
            ArrayList<Integer> row = new ArrayList<>();
            for (String s : inputRow) {
                row.add(Integer.parseInt(s));
            }
            grid.add(row);
        }
        scanner.close();

        PegSolitaire pegSolitaire = new PegSolitaire(grid);
        pegSolitaire.solve();
    }


}