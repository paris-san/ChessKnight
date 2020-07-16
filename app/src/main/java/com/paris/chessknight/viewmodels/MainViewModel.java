package com.paris.chessknight.viewmodels;

import android.app.Application;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.paris.chessknight.R;
import com.paris.chessknight.observables.CalculatePathsObservable;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

import static com.paris.chessknight.utils.SharedPreferencesUtils.getBoardSizeFromSharedPreferences;
import static com.paris.chessknight.utils.SharedPreferencesUtils.getNumberOfMovesFromSharedPreferences;
import static com.paris.chessknight.utils.SharedPreferencesUtils.getPositionsFromSharedPreferences;
import static com.paris.chessknight.utils.SharedPreferencesUtils.getSolutionsFromSharedPreferences;
import static com.paris.chessknight.utils.SharedPreferencesUtils.saveBoardSizeInSharedPreferences;
import static com.paris.chessknight.utils.SharedPreferencesUtils.saveNumberOfMovesInSharedPreferences;
import static com.paris.chessknight.utils.SharedPreferencesUtils.savePositionsInSharedPreferences;
import static com.paris.chessknight.utils.SharedPreferencesUtils.saveSolutionsInSharedPreferences;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";
    public final static int STATE_NEW = 0;
    public final static int STATE_START_POSITION_SELECTED = 1;
    public final static int STATE_END_POSITION_SELECTED = 2;
    public final static int STATE_CALCULATING = 3;
    public final static int STATE_FINISHED = 4;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Integer> boardSize = new MutableLiveData<>();
    private MutableLiveData<Integer> stateOfBoard = new MutableLiveData<>();
    private MutableLiveData<String> startPosition = new MutableLiveData<>();
    private MutableLiveData<String> endPosition = new MutableLiveData<>();
    private MutableLiveData<String> hint = new MutableLiveData<>();
    private MutableLiveData<String> solutions = new MutableLiveData<>();
    //Using String instead of Integer to connect with DataBinding
    private MutableLiveData<String> movesNumber = new MutableLiveData<>();
    private Application application;


    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        setupBoard();
    }

    private void setupBoard() {
        boardSize.setValue(getBoardSizeFromSharedPreferences(application));
        movesNumber.setValue(String.valueOf(getNumberOfMovesFromSharedPreferences(application)));
        if (getPositionsFromSharedPreferences(application) == null || getSolutionsFromSharedPreferences(application) == null) {
            stateOfBoard.setValue(STATE_NEW);
            hint.setValue(application.getString(R.string.select_start));
        } else {
            stateOfBoard.setValue(STATE_FINISHED);
            String positions = getPositionsFromSharedPreferences(application);
            startPosition.setValue(positions.substring(0, positions.indexOf(",")));
            endPosition.setValue(positions.substring(positions.indexOf(",") + 1));
            hint.setValue(application.getString(R.string.reset_the_board));
            showSolutions();
        }
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }


    public void onFindPathsClicked() {
        if (movesNumber.getValue() != null && !movesNumber.getValue().equals("")) {
            if (Integer.parseInt(movesNumber.getValue()) == 0) {
                Toast.makeText(application, application.getString(R.string.invalid_moves_number), Toast.LENGTH_LONG).show();
            } else {
                stateOfBoard.setValue(STATE_CALCULATING);
                saveNumberOfMovesInSharedPreferences(application, Integer.parseInt(movesNumber.getValue()));
                int startingCell = calculatePositionFromRecycler(startPosition.getValue());
                int endingCell = calculatePositionFromRecycler(endPosition.getValue());
                savePositionsInSharedPreferences(application, startPosition.getValue(), endPosition.getValue());
                CalculatePathsObservable cpObservable = new CalculatePathsObservable(application, boardSize.getValue(),
                        startingCell, endingCell, Integer.parseInt(movesNumber.getValue()));
                disposables.add(cpObservable.getCalculatePathsObservable()
                        .subscribe(this::showSolutions,
                                this::onCalculateError));
            }
        }
    }

    private void showSolutions(ArrayList<ArrayList<String>> pathsArrayList) {
        stateOfBoard.setValue(STATE_FINISHED);
        String message = pathsArrayList.size() != 0 ? application.getString(R.string.found_path) : application.getString(R.string.not_found_path);
        Toast.makeText(application, message, Toast.LENGTH_LONG).show();
        showSolutions();
    }

    private void showSolutions() {
        String solutionsFromSharedPreferences = getSolutionsFromSharedPreferences(application);
        if (solutionsFromSharedPreferences != null && !solutionsFromSharedPreferences.equals("")) {
            StringBuilder builder = new StringBuilder();
            String[] paths = solutionsFromSharedPreferences.split("\\+");
            for (String path : paths) {
                String counter = path.substring(0, path.indexOf(":"));
                String strippedPath = path.substring(path.indexOf(":") + 1);
                String[] cells = strippedPath.split(" -> ");
                StringBuilder pathBuilder = new StringBuilder();
                for (int i = 0; i < cells.length; i++) {
                    String cell = cells[i];
                    cell = cell.replaceAll("\\s+", "");
                    char c1 = (char) ('a' + Integer.parseInt(cell.substring(cell.indexOf(",") + 1)));
                    int a = Integer.parseInt(cell.substring(0, cell.indexOf(","))) + 1;
                    String c2 = String.valueOf(a);
                    pathBuilder.append(c1).append(c2);
                    if (i != cells.length - 1) {
                        pathBuilder.append(" -> ");
                    }
                }
                builder.append(counter).append(" steps : ").append(pathBuilder.toString()).append("\n");
            }
            solutions.setValue(builder.toString());
        }
    }

    private void onCalculateError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(application, application.getString(R.string.error_message), Toast.LENGTH_LONG).show();
        stateOfBoard.setValue(STATE_FINISHED);
    }

    private int calculatePositionFromRecycler(String position) {
        //Remove the first row which is letters a, b, c ...
        int p = Integer.parseInt(position) - (boardSize.getValue() + 1) - 1;
        //Remove 1 for each new row whisch is the left column of the board (1, 2, 3...)
        p = p - (p / (boardSize.getValue() + 1));
        return p;
    }


    public MutableLiveData<Integer> getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(Integer newBoardSize) {
        saveBoardSizeInSharedPreferences(application, newBoardSize);
        reset();
        boardSize.setValue(newBoardSize);
    }

    public MutableLiveData<String> getHint() {
        return hint;
    }

    public void setHint(MutableLiveData<String> hint) {
        this.hint = hint;
    }

    public MutableLiveData<String> getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(MutableLiveData<String> startPosition) {
        this.startPosition = startPosition;
    }

    public void setEndPosition(MutableLiveData<String> endPosition) {
        this.endPosition = endPosition;
    }

    public MutableLiveData<String> getEndPosition() {
        return endPosition;
    }

    public MutableLiveData<Integer> getStateOfBoard() {
        return stateOfBoard;
    }

    public void recyclerItemSelected(View view, int position) {
        if (stateOfBoard.getValue() == STATE_NEW) {
            startPosition.setValue(String.valueOf(position));
            stateOfBoard.setValue(STATE_START_POSITION_SELECTED);
            hint.setValue(application.getString(R.string.select_end));
        } else if (stateOfBoard.getValue() == STATE_START_POSITION_SELECTED) {
            endPosition.setValue(String.valueOf(position));
            stateOfBoard.setValue(STATE_END_POSITION_SELECTED);
            hint.setValue(application.getString(R.string.start_calculations));
        }
    }

    public void reset() {
        startPosition.setValue(String.valueOf(-1));
        endPosition.setValue(String.valueOf(-1));
        stateOfBoard.setValue(STATE_NEW);
        hint.setValue(application.getString(R.string.select_start));
        solutions.setValue("");
        saveSolutionsInSharedPreferences(application, null);
    }

    public MutableLiveData<String> getMovesNumber() {
        return movesNumber;
    }

    public void setMovesNumber(MutableLiveData<String> movesNumber) {
        this.movesNumber = movesNumber;
    }

    public MutableLiveData<String> getSolutions() {
        return solutions;
    }

    public void setSolutions(MutableLiveData<String> solutions) {
        this.solutions = solutions;
    }
}
