package com.paris.chessknight.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paris.chessknight.R;
import com.paris.chessknight.viewmodels.MainViewModel;

import static java.lang.Math.sqrt;

public class ChessRecyclerAdapter extends RecyclerView.Adapter<ChessRecyclerAdapter.ViewHolder> {

    private int boardSize;
    private LayoutInflater mInflater;
    private Context context;
    private ItemClickListener mListener;
    private MainViewModel mainViewModel;
    private int startPosition = -1, endPosition = -1;


    public ChessRecyclerAdapter(Context context, MainViewModel mainViewModel, ItemClickListener mListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mListener = mListener;
        this.mainViewModel = mainViewModel;
        setupAdapter();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.chess_recycler_row, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int chessLength = (int) sqrt(boardSize);
        if (position > 0) {
            if (position < chessLength) {
                char c = (char) ('a' + position - 1);
                holder.myTextView.setText(String.valueOf(c));
                holder.imageView.setImageDrawable(null);
                int color = context.getResources().getColor(R.color.theme_default_background);
                holder.rootView.setBackgroundColor(color);
                holder.itemView.setOnClickListener(null);
            } else if (position % chessLength == 0) {
                holder.myTextView.setText(String.valueOf(position / chessLength));
                holder.imageView.setImageDrawable(null);
                int color = context.getResources().getColor(R.color.theme_default_background);
                holder.rootView.setBackgroundColor(color);
                holder.itemView.setOnClickListener(null);
            } else if (((position / chessLength) + (position % chessLength)) % 2 == 0) {
                holder.myTextView.setText("");
                int color = context.getResources().getColor(R.color.colorChessWhite);
                holder.rootView.setBackgroundColor(color);
                setupClickListener(holder, position);
            } else {
                holder.myTextView.setText("");
                int color = context.getResources().getColor(R.color.colorChessGray);
                holder.rootView.setBackgroundColor(color);
                setupClickListener(holder, position);
            }
        } else {
            holder.myTextView.setText("");
            int color = context.getResources().getColor(R.color.theme_default_background);
            holder.rootView.setBackgroundColor(color);
            holder.itemView.setOnClickListener(null);
        }
    }

    private void setupClickListener(ViewHolder holder, int position) {
        if (startPosition == position) {
            Drawable placeholder = holder.imageView.getContext().getResources().getDrawable(R.drawable.knight);
            holder.imageView.setImageDrawable(placeholder);
        } else if (endPosition == position) {
            Drawable placeholder = holder.imageView.getContext().getResources().getDrawable(R.drawable.tick);
            holder.imageView.setImageDrawable(placeholder);
        } else {
            holder.itemView.setOnClickListener(view -> {
                if (mainViewModel.getStateOfBoard().getValue() == MainViewModel.STATE_NEW) {
                    Drawable placeholder = holder.imageView.getContext().getResources().getDrawable(R.drawable.knight);
                    holder.imageView.setImageDrawable(placeholder);
                    mListener.onItemClick(holder.rootView, position);
                } else if (mainViewModel.getStateOfBoard().getValue() == MainViewModel.STATE_START_POSITION_SELECTED
                        //Make sure that this position has not already been selected as starting position
                        && mainViewModel.getStartPosition().getValue() != null && !mainViewModel.getStartPosition().getValue().equals(String.valueOf(position))) {
                    Drawable placeholder = holder.imageView.getContext().getResources().getDrawable(R.drawable.tick);
                    holder.imageView.setImageDrawable(placeholder);
                    mListener.onItemClick(holder.rootView, position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return boardSize;
    }

    private void setupAdapter() {
        if (mainViewModel.getBoardSize().getValue() != null) {
            int size = mainViewModel.getBoardSize().getValue();
            //Add on column/row for notations (eg a1)
            this.boardSize = (size + 1) * (size + 1);
        }
        if (mainViewModel.getStartPosition().getValue() != null && !mainViewModel.getStartPosition().getValue().equals("")) {
            startPosition = Integer.parseInt(mainViewModel.getStartPosition().getValue());
        }
        if (mainViewModel.getEndPosition().getValue() != null && !mainViewModel.getEndPosition().getValue().equals("")) {
            endPosition = Integer.parseInt(mainViewModel.getEndPosition().getValue());
        }
    }

    public void update() {
        setupAdapter();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        View rootView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.chess_row_text);
            imageView = itemView.findViewById(R.id.chess_row_image);
            rootView = itemView.getRootView();
        }
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}