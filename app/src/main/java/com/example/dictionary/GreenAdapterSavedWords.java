package com.example.dictionary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.dictionary.database.WordsEntity;
import java.util.List;

class GreenAdapterSavedWords extends RecyclerView.Adapter<GreenAdapterSavedWords.WordHolder> {


    private List<WordsEntity> words;
    private ButtonListener bListener;

    public GreenAdapterSavedWords(List<WordsEntity> words, ButtonListener bListener) {
        this.words = words;
        this.bListener = bListener;
    }

    public void setWords(List<WordsEntity>words){
        this.words=words;
        notifyDataSetChanged();
    }
    public  WordsEntity getItem(int id){
        return words.get(id);
    }
    public  String getItemWord(int id){
        return GoogleFetchInfoFull.undoConvert(words.get(id).getWordName());
    }
    @Override
    public WordHolder onCreateViewHolder(ViewGroup viewGroup, int id) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_word_view,viewGroup,false);
        return new WordHolder(v);
    }

    @Override
    public void onBindViewHolder(WordHolder wordHolder, int id) {
        WordsEntity obj=words.get(id);
        wordHolder.wordName.setText(GoogleFetchInfoFull.undoConvert(obj.getWordName()));
        wordHolder.isStarred.setVisibility(View.VISIBLE);
        wordHolder.isStarred.setChecked(obj.getIsStarred());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class WordHolder extends RecyclerView.ViewHolder{

        private TextView wordName;
        private ToggleButton isStarred;

        public WordHolder(View itemView) {
            super(itemView);
            wordName=(TextView)itemView.findViewById(R.id.savedWordsTextView);
            isStarred=(ToggleButton)itemView.findViewById(R.id.savedWordsFavourite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bListener.onClickButton(getAdapterPosition());
                }
            });
            isStarred.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bListener.onClickFavourite(getAdapterPosition());
                }
            });
        }

    }
    public interface ButtonListener{
        void onClickButton(int id);
        void onClickFavourite(int id);
    }
}
