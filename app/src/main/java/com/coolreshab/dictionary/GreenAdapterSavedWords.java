package com.coolreshab.dictionary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coolreshab.dictionary.database.WordsEntity;

import java.util.ArrayList;
import java.util.List;

class GreenAdapterSavedWords extends RecyclerView.Adapter<GreenAdapterSavedWords.WordHolder> implements Filterable {


    private List<WordsEntity>words;
    private List<WordsEntity>filteredWords;
    private ButtonListener bListener;
    private String TAG=GreenAdapterSavedWords.class.getSimpleName();

    public GreenAdapterSavedWords(List<WordsEntity> words, ButtonListener bListener) {
        this.words = words;
        this.filteredWords=words;
        this.bListener = bListener;
    }
    public void setWords(List<WordsEntity>words){
        this.words=words;
        this.filteredWords=words;
        notifyDataSetChanged();
    }
    public void deleteWord(WordsEntity obj){
        words.remove(obj);
        filteredWords.remove(obj);
        notifyDataSetChanged();
    }
    public  WordsEntity getItem(int id){
        return filteredWords.get(id);
    }
    public  String getItemWord(int id){
        return GoogleFetchInfoFull.undoConvert(filteredWords.get(id).getWordName());
    }
    @Override
    public WordHolder onCreateViewHolder(ViewGroup viewGroup, int id) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_word_view,viewGroup,false);
        return new WordHolder(v);
    }

    @Override
    public void onBindViewHolder(WordHolder wordHolder, int id) {
        WordsEntity obj=filteredWords.get(id);
        wordHolder.wordName.setText(GoogleFetchInfoFull.undoConvert(obj.getWordName()));
        wordHolder.isStarred.setVisibility(View.VISIBLE);
        wordHolder.isStarred.setChecked(obj.getIsStarred());
    }

    @Override
    public int getItemCount() {
        return filteredWords.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                //Log.d(TAG,charString);
                if (charString.isEmpty()) {
                    filteredWords = words;
                } else {
                    List<WordsEntity> filteredList = new ArrayList<>();
                    for (WordsEntity row : words) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getWordName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }

                    filteredWords = filteredList;
                    //Log.d(TAG,Integer.toString(filteredWords.size()));
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredWords;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredWords = (ArrayList<WordsEntity>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
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
