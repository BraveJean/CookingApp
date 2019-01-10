package com.cookingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ComAdapter extends RecyclerView.Adapter<ComAdapter.ViewHolder> {

    Context context;
    ArrayList<Comment> commentInfo;

    public ComAdapter(Context c, ArrayList<Comment> r) {
        this.context = c;
        this.commentInfo = r;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_view,viewGroup,false);
        //context = viewGroup.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        int j = i+1;
        String comment = j + ": "+commentInfo.get(i).getComment();
        viewHolder.coms.setText(comment);

    }

    @Override
    public int getItemCount() {

        return commentInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView coms;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            coms = itemView.findViewById(R.id.comment);
        }
    }
}
