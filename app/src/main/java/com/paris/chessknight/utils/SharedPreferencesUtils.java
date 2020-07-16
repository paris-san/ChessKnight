package com.paris.chessknight.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesUtils {

    private static final String CHESS_STATE = "CHESS_STATE";
    private static final String BOARD_SIZE = "BOARD_SIZE";
    private static final String POSITIONS = "POSITIONS";
    private static final String SOLUTIONS = "SOLUTIONS";
    private static final String NUMBER_OF_MOVES = "NUMBER_OF_MOVES";

    /**
     * Write to a shared preferences file, overloaded.
     **/
    public static void writeToSharedPreferences(Context context, String preferencesName,
                                                String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(preferencesName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void writeToSharedPreferences(Context context, String preferencesName,
                                                String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(preferencesName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public static void saveBoardSizeInSharedPreferences(Context context, int boardSize) {
        writeToSharedPreferences(context, CHESS_STATE, BOARD_SIZE, boardSize);
    }


    public static int getBoardSizeFromSharedPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CHESS_STATE, MODE_PRIVATE);
        return sharedPref.getInt(BOARD_SIZE, 8);
    }


    public static void savePositionsInSharedPreferences(Context context, String start, String end) {
        writeToSharedPreferences(context, CHESS_STATE, POSITIONS, start + "," + end);
    }


    public static String getPositionsFromSharedPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CHESS_STATE, MODE_PRIVATE);
        return sharedPref.getString(POSITIONS, null);
    }

    public static void saveSolutionsInSharedPreferences(Context context, ArrayList<ArrayList<String>> pathsArrayList) {
        if (pathsArrayList == null) return;
        StringBuilder paths = new StringBuilder();
        for (ArrayList<String> singlePathAndCounter : pathsArrayList) {
            paths.append(singlePathAndCounter.get(0)).append(" : ").append(singlePathAndCounter.get(1)).append("+");
        }
        writeToSharedPreferences(context, CHESS_STATE, SOLUTIONS, paths.toString());
    }


    public static String getSolutionsFromSharedPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CHESS_STATE, MODE_PRIVATE);
        return sharedPref.getString(SOLUTIONS, "");
    }

    public static void saveNumberOfMovesInSharedPreferences(Context context, int numberOfMoves) {
        writeToSharedPreferences(context, CHESS_STATE, NUMBER_OF_MOVES, numberOfMoves);
    }


    public static int getNumberOfMovesFromSharedPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CHESS_STATE, MODE_PRIVATE);
        return sharedPref.getInt(NUMBER_OF_MOVES, 3);
    }

}
