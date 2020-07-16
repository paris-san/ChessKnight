package com.paris.chessknight.observables;

import android.content.Context;

import com.paris.chessknight.models.ChessCell;
import com.paris.chessknight.models.Coordinates;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.paris.chessknight.utils.SharedPreferencesUtils.saveSolutionsInSharedPreferences;

public class CalculatePathsObservable {

    private Context context;
    private Observable<ArrayList<ArrayList<String>>> mCalculatePathsObservable;
    private int boardSize, movesNumber;
    private String chessEndPosition;
    private HashMap<String, ChessCell> chessMap;

    public CalculatePathsObservable(Context context, int boardSize, int startPosition, int endPosition, int movesNumber) {
        this.context = context;
        this.boardSize = boardSize;
        this.movesNumber = movesNumber;
        chessEndPosition = endPosition / boardSize + "," + (endPosition % boardSize);
        mCalculatePathsObservable = Observable
                .fromCallable(() -> {
                    chessMap = createMapOfCells();
                    String chessStartPosition = startPosition / boardSize + "," + (startPosition % boardSize);
                    calculatePaths(chessStartPosition, 0, null);
                    ArrayList<ArrayList<String>> paths = createHashMapOfPaths();
                    saveSolutionsInSharedPreferences(context, paths);
                    return paths;
                }).cache()
                .subscribeOn(Schedulers.computation());
    }


    private ArrayList<ArrayList<String>> createHashMapOfPaths() {
        ArrayList<ArrayList<String>> counterAndPaths = new ArrayList<>();
        //For every item in the lastCellList
        for (ArrayList<Object> singleCellAndCounter : chessMap.get(chessEndPosition).getLastCellList()) {
            ArrayList<String> singlePathAndCounter = new ArrayList<>();
            //Add the counter in the first item
            int counter = (int) singleCellAndCounter.get(0);
            singlePathAndCounter.add(String.valueOf(counter));
            //Create a String backtracking all the previous Cells
            StringBuilder wholePath = new StringBuilder();
            //Insert at the start of StringBuilder so the path will be reversed at the end of the parsing
            wholePath.insert(0, chessEndPosition);
            //Make a string of the previous cell coordinates eg "1,2"
            String previousCellCoordinates = ((Coordinates) singleCellAndCounter.get(1)).getRow()
                    + "," + ((Coordinates) singleCellAndCounter.get(1)).getColumn();
            ChessCell previousCell = chessMap.get(previousCellCoordinates);
            //Repeat process until we reach starting cell
            int flag = 1;
            do {
                previousCellCoordinates = previousCell.getCellCoordinates().getRow()
                        + "," + previousCell.getCellCoordinates().getColumn();
                wholePath.insert(0, " -> ");
                wholePath.insert(0, previousCellCoordinates);
                if (previousCell.getPreviousCellsAndCounters() == null) {
                    break;
                }
                previousCell = chessMap.get(previousCell.getPreviousCellsAndCounters().getRow()
                        + "," + previousCell.getPreviousCellsAndCounters().getColumn());
                flag++;
            } while (previousCell != null && previousCell.getBestReach() >= 0);
            singlePathAndCounter.add(wholePath.toString());
            if (flag == counter) {
                counterAndPaths.add(singlePathAndCounter);
            }
        }
        return counterAndPaths;
    }


    private void calculatePaths(String chessPosition, int currentStep, Coordinates previousCellCoordinates) {
        ChessCell currentCell = chessMap.get(chessPosition);
        //if the current cell doesn't exist in the chess map we are of board limits.
        if (currentCell == null) return;
        //if this is the fastest or equal to smallest step in which we reach this cell
        if (currentCell.getBestReach() == -1 || currentStep < currentCell.getBestReach() || chessPosition.equals(chessEndPosition)) {
            if (!chessPosition.equals(chessEndPosition)) {
                currentCell.setBestReach(currentStep);
                //If this is not our starting position
                if (currentStep > 0) {
                    currentCell.addPreviousCellCoordinates(previousCellCoordinates);
                }
            } else {
                ArrayList<Object> lastCell = new ArrayList<>();
                lastCell.add(currentStep);
                lastCell.add(previousCellCoordinates);
                currentCell.getLastCellList().add(lastCell);
            }
            //If we have not reached the max moves and we are not in the end position, continue moving
            if (currentStep < movesNumber && !chessPosition.equals(chessEndPosition)) {
                moveUpRight(currentCell, currentStep);
                moveUpLeft(currentCell, currentStep);
                moveRightUp(currentCell, currentStep);
                moveRightDown(currentCell, currentStep);
                moveDownLeft(currentCell, currentStep);
                moveDownRight(currentCell, currentStep);
                moveLeftUp(currentCell, currentStep);
                moveLeftDown(currentCell, currentStep);
            }
        }
    }

    private void moveUpRight(ChessCell currentCell, int currentStep) {
        int nextX = currentCell.getCellCoordinates().getRow() - 2;
        int nextY = currentCell.getCellCoordinates().getColumn() + 1;
        String firstMoveCell = nextX + "," + nextY;
        calculatePaths(firstMoveCell, ++currentStep, currentCell.getCellCoordinates());
    }

    private void moveUpLeft(ChessCell currentCell, int currentStep) {
        int nextX = currentCell.getCellCoordinates().getRow() - 2;
        int nextY = currentCell.getCellCoordinates().getColumn() - 1;
        String firstMoveCell = nextX + "," + nextY;
        calculatePaths(firstMoveCell, ++currentStep, currentCell.getCellCoordinates());
    }

    private void moveRightUp(ChessCell currentCell, int currentStep) {
        int nextX = currentCell.getCellCoordinates().getRow() - 1;
        int nextY = currentCell.getCellCoordinates().getColumn() + 2;
        String firstMoveCell = nextX + "," + nextY;
        calculatePaths(firstMoveCell, ++currentStep, currentCell.getCellCoordinates());
    }

    private void moveRightDown(ChessCell currentCell, int currentStep) {
        int nextX = currentCell.getCellCoordinates().getRow() + 1;
        int nextY = currentCell.getCellCoordinates().getColumn() + 2;
        String firstMoveCell = nextX + "," + nextY;
        calculatePaths(firstMoveCell, ++currentStep, currentCell.getCellCoordinates());
    }

    private void moveDownLeft(ChessCell currentCell, int currentStep) {
        int nextX = currentCell.getCellCoordinates().getRow() + 2;
        int nextY = currentCell.getCellCoordinates().getColumn() - 1;
        String firstMoveCell = nextX + "," + nextY;
        calculatePaths(firstMoveCell, ++currentStep, currentCell.getCellCoordinates());
    }

    private void moveDownRight(ChessCell currentCell, int currentStep) {
        int nextX = currentCell.getCellCoordinates().getRow() + 2;
        int nextY = currentCell.getCellCoordinates().getColumn() + 1;
        String firstMoveCell = nextX + "," + nextY;
        calculatePaths(firstMoveCell, ++currentStep, currentCell.getCellCoordinates());
    }

    private void moveLeftUp(ChessCell currentCell, int currentStep) {
        int nextX = currentCell.getCellCoordinates().getRow() - 1;
        int nextY = currentCell.getCellCoordinates().getColumn() - 2;
        String firstMoveCell = nextX + "," + nextY;
        calculatePaths(firstMoveCell, ++currentStep, currentCell.getCellCoordinates());
    }

    private void moveLeftDown(ChessCell currentCell, int currentStep) {
        int nextX = currentCell.getCellCoordinates().getRow() + 1;
        int nextY = currentCell.getCellCoordinates().getColumn() - 2;
        String firstMoveCell = nextX + "," + nextY;
        calculatePaths(firstMoveCell, ++currentStep, currentCell.getCellCoordinates());
    }


    private HashMap<String, ChessCell> createMapOfCells() {
        HashMap<String, ChessCell> chessMap = new HashMap<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                String key = i + "," + j;
                chessMap.put(key, new ChessCell(i, j));
            }
        }
        return chessMap;
    }


    /**
     * Getter for the Observable.
     * This is called only by an activity/fragment that's why we call observeOn(mainThread)
     * before returning.
     */
    public Observable<ArrayList<ArrayList<String>>> getCalculatePathsObservable() {
        return mCalculatePathsObservable.observeOn(AndroidSchedulers.mainThread());
    }
}
