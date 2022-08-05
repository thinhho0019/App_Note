package com.example.note.Adapter;


import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.R;
import com.example.note.entities.Notes;
import com.example.note.listener.noteListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class noteAdapter extends RecyclerView.Adapter<noteAdapter.noteViewHolder> {
    private List<Notes> notesList;
    private noteListener nlistener;
    private Timer timer;
    private List<Notes> notesListSearch;
    public noteAdapter(List<Notes> notesList,noteListener n) {
        this.notesList = notesList;
        this.nlistener=n;
        notesListSearch=notesList; //luc dau giu co dinh du lieu
    }

    @NonNull
    @Override
    public noteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new noteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contaner_notes, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull noteViewHolder holder, int position) {
        holder.setNote(notesList.get(position));
        holder.colorBackground.setOnClickListener(
                v->{
                    nlistener.onClicknote(notesList.get(position),position);

                }
        );
        holder.colorBackground.setOnLongClickListener(v->{
            nlistener.onLongClickNotes(notesList.get(position),position);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class noteViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView textTile,textSubtitle,textTime;
        LinearLayout colorBackground;
        RoundedImageView imageNote;
        noteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTile=itemView.findViewById(R.id.textTitle);
            textSubtitle= itemView.findViewById(R.id.textSubtitle);
            textTime=itemView.findViewById(R.id.textTime);
            imageNote= itemView.findViewById(R.id.ImageNotesItem);
            colorBackground=itemView.findViewById(R.id.layoutNote);
            itemView.setOnCreateContextMenuListener(this);

        }

        void setNote(Notes notes){
            textTile.setText(notes.getTitle());
            GradientDrawable gradientDrawable = (GradientDrawable) colorBackground.getBackground();
            gradientDrawable.setColor(Color.parseColor(notes.getColor()));
            if(notes.getNoteText().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }else{
                if(notes.getNoteText().length()>30){
                    textSubtitle.setText(notes.getNoteText().substring(0,30)+"....");
                }else{
                    textSubtitle.setText(notes.getNoteText());
                }

            }

            textTime.setText(notes.getDatetime());
            if(notes.getImagePath()!=null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(notes.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            }else{
                imageNote.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE,R.id.option_2,Menu.NONE,"Xóa");
        }



    }
    public void SearchNotes(final String keySearch){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(keySearch.trim().isEmpty()){
                    notesList = notesListSearch; //tro ve ban dau
                }else{
                    ArrayList<Notes> temp = new ArrayList<>();
                    for(Notes n : notesListSearch){
                        if(n.getTitle().trim().contains(keySearch.toLowerCase())||n.getNoteText().trim().contains(keySearch.toLowerCase())){
                            temp.add(n);
                        }
                    }
                    notesList =temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged(); //update ui
                    }
                });
            }
        },500);
    }
    public void cancelTimer(){
        if(timer!=null){
            timer.cancel();
        }
    }
    public void CheckLable(String keySearch){
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//
//                    ArrayList<Notes> temp = new ArrayList<>();
//                    for(Notes n : notesListSearch) {
//                        if(keySearch.equals("ALL")){
//                            temp.add(n);
//                        }else if(!keySearch.equals("ALL")){
//                            if(n.getLabel().equals(keySearch)){
//                                temp.add(n);
//                            }
//                        }
//                    }
//                    notesList =temp;
//                     notifyDataSetChanged();
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        notifyDataSetChanged(); //update ui
//                    }
//                });
//            }
//        },500);
        ArrayList<Notes> temp = new ArrayList<>();
        for(Notes n : notesListSearch) {
            if(keySearch.equals("ALL")){
                temp.add(n);
            }else if(!keySearch.equals("ALL")){
                if(n.getLabel().equals(keySearch)){
                    temp.add(n);
                }
            }
        }

        notesList =temp;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged(); //update ui
                    }
                });
    }

}
