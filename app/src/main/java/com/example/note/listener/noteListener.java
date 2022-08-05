package com.example.note.listener;

import com.example.note.entities.Notes;

public interface noteListener {
    void onClicknote(Notes note,int position);
    void onLongClickNotes(Notes note,int position);
    void onClickLable(String note,int position);
}
