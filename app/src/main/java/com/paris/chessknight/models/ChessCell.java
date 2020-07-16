package com.paris.chessknight.models;

import java.util.ArrayList;

public class ChessCell {

    private int bestReach = -1;
    private Coordinates cellCoordinates;
    private Coordinates previousCellsAndCounters;
    //A list used in the end cell to hold all second to last cell coordinates and counters
    private ArrayList<ArrayList<Object>> lastCellList = new ArrayList<>();


    public ChessCell(int column, int row) {
        cellCoordinates = new Coordinates(column, row);
    }


    public void addPreviousCellCoordinates(Coordinates previousCellCoordinates) {
        this.previousCellsAndCounters = previousCellCoordinates;
    }

    public void setCellCoordinates(Coordinates cellCoordinates) {
        this.cellCoordinates = cellCoordinates;
    }

    public Coordinates getPreviousCellsAndCounters() {
        return previousCellsAndCounters;
    }

    public Coordinates getCellCoordinates() {
        return cellCoordinates;
    }

    public int getBestReach() {
        return bestReach;
    }

    public void setBestReach(int bestReach) {
        this.bestReach = bestReach;
    }

    public ArrayList<ArrayList<Object>> getLastCellList() {
        return lastCellList;
    }

    public void setLastCellList(ArrayList<ArrayList<Object>> lastCellList) {
        this.lastCellList = lastCellList;
    }
}
