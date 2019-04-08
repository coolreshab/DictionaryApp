package com.example.dictionary;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


public class GreenAdapter extends RecyclerView.Adapter<GreenAdapter.WordHolder> {

    private ArrayList<String>words;
    private ButtonListener bListener;
    public GreenAdapter(ArrayList<String>words,ButtonListener bListener){
        this.words=words;
        this.bListener=bListener;
    }
    @NonNull
    @Override
    public WordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int id) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.word_view,viewGroup,false);
        return new WordHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WordHolder wordHolder, int id) {
        wordHolder.wordTextView.setText(words.get(id));
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class WordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView wordTextView;
        public WordHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView=itemView.findViewById(R.id.wordTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos=getAdapterPosition();
            bListener.onCLickButton(pos);
        }
    }
    public interface ButtonListener{
        void onCLickButton(int id);
    }
}
