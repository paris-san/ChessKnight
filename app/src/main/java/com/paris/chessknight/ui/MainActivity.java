package com.paris.chessknight.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paris.chessknight.R;
import com.paris.chessknight.databinding.ActivityMainBinding;
import com.paris.chessknight.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements ChessRecyclerAdapter.ItemClickListener {

    private RecyclerView chessRecycler;
    private ChessRecyclerAdapter chessRecyclerAdapter;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityAboutBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        activityAboutBinding.setMainViewModel(mainViewModel);
        activityAboutBinding.setMainActivity(this);
        activityAboutBinding.setLifecycleOwner(this);
        chessRecycler = findViewById(R.id.chess_recycler);

        mainViewModel.getBoardSize().observe(this, this::setupChessRecycler);
    }


    private void setupChessRecycler(int boardSize) {
        chessRecycler.setLayoutManager(new GridLayoutManager(this, boardSize + 1));
        if (chessRecyclerAdapter == null) {
            chessRecyclerAdapter = new ChessRecyclerAdapter(this, mainViewModel, this);
            chessRecycler.setAdapter(chessRecyclerAdapter);
        } else {
            chessRecyclerAdapter.update();
        }
    }


    public void onChangeSizeClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change board size");
        builder.setMessage("Please choose a number between 6 and 16.");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            int number = input.getText().toString().equals("") ?
                    0 : Integer.parseInt(input.getText().toString());
            if (number >= 6 && number <= 16) {
                mainViewModel.setBoardSize(number);
            } else {
                Toast.makeText(this, getString(R.string.invalid_chess_size), Toast.LENGTH_LONG).show();
            }

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void onResetClicked() {
        mainViewModel.reset();
        chessRecyclerAdapter.update();
    }

    @Override
    public void onItemClick(View view, int position) {
        mainViewModel.recyclerItemSelected(view, position);
    }
}