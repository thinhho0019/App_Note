package com.example.note.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.note.entities.Notes;

import java.util.List;

@Dao
public interface Notedao {

    @Query("SELECT * FROM notes Order by id Desc")
    List<Notes> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotes(Notes notes);

    @Delete
    void deleteNotes(Notes notes);


}
