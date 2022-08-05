package com.example.note.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.R;
import com.example.note.entities.Notes;
import com.example.note.listener.noteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class labelAdapter extends RecyclerView.Adapter<labelAdapter.labelViewHolder> {

    private List<String> notesList;
    private noteListener NoteListener;

    public labelAdapter(List<String> notesList,noteListener NoteListener) {
        this.notesList = notesList;
        this.NoteListener=NoteListener;
    }

    @NonNull
    @Override
    public labelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new labelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_label,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull labelViewHolder holder, int position) {
        holder.setTextItem(notesList.get(position));
        holder.layoutLabel.setOnClickListener(v->{
            NoteListener.onClickLable(notesList.get(position),position);
        });

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    static class labelViewHolder extends RecyclerView.ViewHolder{
        TextView textView ;
        LinearLayout layoutLabel;
        public labelViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.textItemLabel);
            layoutLabel = itemView.findViewById(R.id.layoutItemLabel);
        }
        void setTextItem(String notes){
            textView.setText(notes);
        }
    }

}
