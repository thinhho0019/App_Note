package com.example.note.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note.Adapter.labelAdapter;
import com.example.note.Adapter.noteAdapter;
import com.example.note.R;
import com.example.note.activity.CreateNoteActivity;
import com.example.note.database.NotesDatabase;
import com.example.note.entities.Notes;
import com.example.note.listener.noteListener;
import com.example.note.util.Constraint;
import com.example.note.util.PreferenceManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements noteListener {
    public static final int REQUEST_CODE_ADD_NOTES =1;
    public static final int REQUEST_CODE_UPDATE_NOTES =2;
    public static final int REQUEST_CODE_SHOW_NOTES=3;
    public static final int REQUEST_CODE_DELETE_NOTES=4;
    private List<Notes> notesList;
    private RecyclerView recyclerView,recyclerViewLabel;
    private noteAdapter nAdapter;
    private int clickedPosition=-1;
    private TextView lableShow;
    private Notes deleteNotes;
    private AlertDialog alertDialogLabel;
    private ArrayList<String> listLabel ;
    private labelAdapter LabelAdapter;
    private String labelClicked;
    private int labelClickedPosition=-1;
    private PreferenceManager preferenceManager;
    private int Flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageViewAddNotes = findViewById(R.id.mainImageAddNotes);
        recyclerView = findViewById(R.id.recycleView);
        recyclerViewLabel=findViewById(R.id.RecycleViewLabel);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );
        preferenceManager= new PreferenceManager(getApplicationContext());
        notesList= new ArrayList<>();
        nAdapter = new noteAdapter(notesList,this);
        recyclerView.setAdapter(nAdapter);
        listLabel= new ArrayList<>();
        LabelAdapter = new labelAdapter(listLabel,this);
        getNotes(REQUEST_CODE_SHOW_NOTES);
        recyclerViewLabel.setAdapter(LabelAdapter);
        imageViewAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), CreateNoteActivity.class);
                intent.putExtra("Lable",labelClicked);
                intent.putExtra("CheckLable",true);
                startActivityForResult(intent,REQUEST_CODE_ADD_NOTES);
            }
        });
        EditText searchT = findViewById(R.id.inputSearch);
        lableShow = findViewById(R.id.labelShowStart);

        searchT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(notesList.size()!=0){
                    nAdapter.SearchNotes(s.toString());
                }
            }
        });


        ImageView imageViewLable = findViewById(R.id.imageAddLabel);


        imageViewLable.setOnClickListener(v->{
            showDialogLabel();
        });





    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        labelClicked= preferenceManager.getString(Constraint.TEXT_SHOW_LABLE);
//        if(labelClicked!=null){
//            lableShow.setText(labelClicked);
//            lableShow.setVisibility(View.VISIBLE);
//            nAdapter.CheckLable(labelClicked);
//        }else{
//            labelClicked="ALL";
//            lableShow.setText("ALL");
//            lableShow.setVisibility(View.VISIBLE);
//        }
//    }


    @Override
    protected void onStop() {
        super.onStop();
        preferenceManager.putString(Constraint.TEXT_SHOW_LABLE,labelClicked);
    }

    private void showDialogLabel(){
        if(alertDialogLabel==null){
            AlertDialog.Builder builder =  new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_add_lable,
                    (ViewGroup) findViewById(R.id.layoutItemLabel),false);
            builder.setView(view);
            alertDialogLabel =builder.create();
            if(alertDialogLabel.getWindow()!=null){ //làm trong suốt hộp thoai
                alertDialogLabel.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText editTextLable = view.findViewById(R.id.inputTextLable);
            editTextLable.requestFocus();
            view.findViewById(R.id.textViewThemLable).setOnClickListener(v->{
                if(editTextLable.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"chủ đề không được để trống!",Toast.LENGTH_SHORT).show();
                    alertDialogLabel.dismiss();
                }
                listLabel.add(1,editTextLable.getText().toString());
                LabelAdapter.notifyItemInserted(1);
                recyclerViewLabel.smoothScrollToPosition(0);
                editTextLable.setText("");
                alertDialogLabel.dismiss();
            });
            view.findViewById(R.id.textViewHuyLable).setOnClickListener(v->{
                alertDialogLabel.dismiss();
            });

        }
        alertDialogLabel.show();
    }
    private void loadListLabel(List<Notes> notes){
        ArrayList<String> temp=new ArrayList<>();
        temp.add(0,"ALL");
        for(Notes n:notes){
            if(!n.getLabel().isEmpty()){
                int flag=1;
                for(String a:temp){
                    if(a.equals(n.getLabel())){
                        flag=0;
                        break;
                    }
                }
                if(flag==1){
                    temp.add(n.getLabel());
                }

            }
        }
        listLabel.addAll(temp);
        LabelAdapter.notifyDataSetChanged();
    }
    private void getNotes(final int requestCode){

        @SuppressLint("StaticFieldLeak")
        class getMynotes extends AsyncTask<Void,Void,List<Notes>>{

            @Override
            protected List<Notes> doInBackground(Void... voids) {
                    return NotesDatabase.getDatabase(getApplicationContext()).notedao().getAllNotes();
            };

            @Override
            protected void onPostExecute(List<Notes> notes) {
                super.onPostExecute(notes);
                if(requestCode==REQUEST_CODE_SHOW_NOTES){
                    loadListLabel(notes);
                    notesList.addAll(notes);
                    labelClicked= preferenceManager.getString(Constraint.TEXT_SHOW_LABLE);
                    if(labelClicked!=null){
                        lableShow.setText(labelClicked);
                        lableShow.setVisibility(View.VISIBLE);
                        nAdapter.CheckLable(labelClicked);
                    }else{
                        labelClicked="ALL";
                        lableShow.setText("ALL");
                        lableShow.setVisibility(View.VISIBLE);
                    }
                    nAdapter.CheckLable(labelClicked);

                    //nAdapter.notifyDataSetChanged();
                }else if(requestCode==REQUEST_CODE_ADD_NOTES){
                    notesList.add(0,notes.get(0));
                    nAdapter.notifyItemInserted(0);
                }else if(requestCode==REQUEST_CODE_UPDATE_NOTES){
                    notesList.remove(clickedPosition);
                    notesList.add(clickedPosition, notes.get(clickedPosition));
                    nAdapter.notifyItemInserted(clickedPosition);

                }else if(requestCode==REQUEST_CODE_DELETE_NOTES){
                    notesList.remove(deleteNotes);
//                    nAdapter.notifyItemRemoved(clickedPosition);
                    nAdapter.CheckLable(labelClicked);
                }
                recyclerView.smoothScrollToPosition(0);
            }
        }
        new getMynotes().execute();
    }

    @Override
    public void onClicknote(Notes note, int position) {
        clickedPosition=position;
        Intent intent = new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isVieworUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTES);
    }

    @Override
    public void onLongClickNotes(Notes note, int position) {
        clickedPosition=position;
        deleteNotes =notesList.get(position);
    }

    @Override
    public void onClickLable(String note, int position) {
        labelClicked=note;
        labelClickedPosition=position;
        preferenceManager.putString(Constraint.TEXT_SHOW_LABLE,labelClicked);
        lableShow.setText(note);
        lableShow.setVisibility(View.VISIBLE);
        if(labelClicked!=null){
            nAdapter.CheckLable(labelClicked);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_ADD_NOTES&& resultCode==RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTES);
        }else if(requestCode==REQUEST_CODE_UPDATE_NOTES&& resultCode==RESULT_OK){
            if(data!=null){
                getNotes(REQUEST_CODE_UPDATE_NOTES);
            }

        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.option_2:
                class deleteNote extends AsyncTask<Void,Void,Void>{
                    @Override
                    protected Void doInBackground(Void... voids) {
                         NotesDatabase.getDatabase(getApplicationContext()).notedao().deleteNotes(deleteNotes);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void unused) {
                        super.onPostExecute(unused);
                        getNotes(REQUEST_CODE_DELETE_NOTES);

                    }
                }
                new deleteNote().execute();
                break;
        }
        return super.onContextItemSelected(item);
    }


}