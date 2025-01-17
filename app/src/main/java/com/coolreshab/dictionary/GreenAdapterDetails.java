package com.coolreshab.dictionary;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


public class GreenAdapterDetails extends RecyclerView.Adapter<GreenAdapterDetails.MeanHolder> {

    private ArrayList<GoogleFetchInfo>meaning;
    public GreenAdapterDetails(ArrayList<GoogleFetchInfo>meaning){
        this.meaning=meaning;
    }
    @NonNull
    @Override
    public MeanHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int id) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meaning_view,viewGroup,false);
        return new MeanHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MeanHolder meanHolder, int id) {
        GoogleFetchInfo obj=meaning.get(id);
        if(!TextUtils.isEmpty(obj.partOfSpeech)){
            meanHolder.mean1.setVisibility(View.VISIBLE);
            meanHolder.mean1.setText(GoogleFetchInfoFull.addEffect(GoogleFetchInfoFull.convertFirstToUpper(obj.partOfSpeech),"<i><u>","</i></u>"));
        }
        if(!TextUtils.isEmpty(obj.definition)) {
            meanHolder.mean2.setVisibility(View.VISIBLE);
            meanHolder.mean2.setText(GoogleFetchInfoFull.addEffect("Definition:","<b>","</b>"));
            meanHolder.mean2.append("  "+obj.definition);
        }
        if(!TextUtils.isEmpty(obj.example)) {
            meanHolder.mean3.setVisibility(View.VISIBLE);
            meanHolder.mean3.setText(GoogleFetchInfoFull.addEffect("Example:","<b>","</b>"));
            meanHolder.mean3.append("  "+obj.example);
        }
        if(!obj.synonyms.isEmpty()){
            meanHolder.mean4.setVisibility(View.VISIBLE);
            meanHolder.mean4.setText(GoogleFetchInfoFull.addEffect("Synonyms:","<b>","</b>"));
            meanHolder.mean4.append("  ");
            for(int i=0;i<obj.synonyms.size();++i){
                meanHolder.mean4.append(obj.synonyms.get(i));
                if(i!=obj.synonyms.size()-1)
                    meanHolder.mean4.append(" , ");
            }
        }
    }

    @Override
    public int getItemCount() {
        return meaning.size();
    }

    public GoogleFetchInfo getItem(int idx){
        return meaning.get(idx);
    }

    public class MeanHolder extends RecyclerView.ViewHolder {

        private TextView mean1;
        private TextView mean2;
        private TextView mean3;
        private TextView mean4;
        public MeanHolder(@NonNull View itemView) {
            super(itemView);
            mean1 = itemView.findViewById(R.id.mean1);
            mean2 = itemView.findViewById(R.id.mean2);
            mean3 = itemView.findViewById(R.id.mean3);
            mean4 = itemView.findViewById(R.id.mean4);
        }
    }
}

