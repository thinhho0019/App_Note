package com.example.note.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note.R;
import com.example.note.database.NotesDatabase;
import com.example.note.entities.Notes;
import com.example.note.util.Constraint;
import com.example.note.util.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private EditText title,decription;
    private ImageView save,image1,image2,image3,image4,image5,ImageNote,deleteUrl;
    private TextView dateTimeNotes;
    private String SelectedNoteColor;
    private String SelectedImagePath;
    private View viewColor;
    private LinearLayout layoutAddimage,layoutWebUrl;
    private TextView textWebUrl;
    private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQUEST_CODE_SELECT_IMAGE=2;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout layoutll;
    private AlertDialog  DialogAddUrl;
    private PreferenceManager preferenceManager ;
    private Notes AfterClickNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        ImageView imageViewBack = findViewById(R.id.backCreateNotes);
        save= findViewById(R.id.checkCreateNotes);
        imageViewBack.setOnClickListener(v->{
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        });
        preferenceManager = new PreferenceManager(getApplicationContext());
        textWebUrl = findViewById(R.id.textWebUrl);
        layoutWebUrl=findViewById(R.id.layoutWebUrl);
        image1=findViewById(R.id.imageNoteColor1);
        image2=findViewById(R.id.imageNoteColor2);
        image3=findViewById(R.id.imageNoteColor3);
        image4=findViewById(R.id.imageNoteColor4);
        image5=findViewById(R.id.imageNoteColor5);
        ImageNote = findViewById(R.id.ImageNotes);
        viewColor = findViewById(R.id.viewSubtext);
        layoutAddimage = findViewById(R.id.layoutAddImage);
        title = findViewById(R.id.EdittexTtitle);

        deleteUrl =findViewById(R.id.deleteUrl);
        decription= findViewById(R.id.inputNotes);
        save.setOnClickListener(v->{
            saveNotes();
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        });
        SelectedImagePath="";
        SelectedNoteColor ="#333333";
        dateTimeNotes= findViewById(R.id.dateTimeNotes);
        dateTimeNotes.setText(new SimpleDateFormat("EEEE, dd MM yyyy HH:mm a ",Locale.getDefault()).format(new Date()));
        initMiscellaneous();

        if(getIntent().getBooleanExtra("isVieworUpdate",false)){
            AfterClickNote = (Notes) getIntent().getSerializableExtra("note");
            setVieworUpdate();
        }
        clickSelectedColor();
        findViewById(R.id.layoutAddUrl).setOnClickListener(
                v->{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showAddDialog();
                }
        );
        deleteUrl.setOnClickListener(v->{
            if(layoutWebUrl.getVisibility()==View.VISIBLE){
                layoutWebUrl.setVisibility(View.GONE);
            }
        });


    }




    private void setVieworUpdate() {
        title.setText(AfterClickNote.getTitle());
        decription.setText(AfterClickNote.getNoteText());
        dateTimeNotes.setText(AfterClickNote.getDatetime());
        if(AfterClickNote.getImagePath() !=null && !AfterClickNote.getImagePath().trim().toString().isEmpty()){
            ImageNote.setImageBitmap(BitmapFactory.decodeFile(AfterClickNote.getImagePath()));
            ImageNote.setVisibility(View.VISIBLE);
            SelectedImagePath = AfterClickNote.getImagePath();
        }
        if(AfterClickNote.getWebLink() !=null && !AfterClickNote.getWebLink().trim().toString().isEmpty()){
            textWebUrl.setText(AfterClickNote.getWebLink());
            layoutWebUrl.setVisibility(View.VISIBLE);
        }

    }

    private void saveNotes(){
        if(title.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"tiêu đề không được để trống",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(decription.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"nội dung không được để trống",Toast.LENGTH_SHORT).show();
            return;
        }
        final Notes notes = new Notes();
        notes.setTitle(title.getText().toString());
        notes.setNoteText(decription.getText().toString());
        notes.setColor(SelectedNoteColor);
        notes.setImagePath(SelectedImagePath);
        notes.setDatetime(new SimpleDateFormat("EEEE, dd MMMM yyyy MM:mm a", Locale.getDefault()).format(new Date()));
        if(layoutWebUrl.getVisibility()==View.VISIBLE){
            notes.setWebLink(textWebUrl.getText().toString());
        }else if(layoutWebUrl.getVisibility()==View.GONE){
            notes.setWebLink(null);
        }
        if(AfterClickNote!=null){
            notes.setId(AfterClickNote.getId());
        }
        notes.setLabel(preferenceManager.getString(Constraint.TEXT_SHOW_LABLE));
        @SuppressLint("StaticFieldLeak")
        class Mainsave extends AsyncTask<Void,Void,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).notedao().insertNotes(notes);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Intent intent= new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }
        new Mainsave().execute();
    }
    private void clickSelectedColor(){
        image1.setOnClickListener(v->{
                SelectedNoteColor="#333333";
                image1.setImageResource(R.drawable.ic_done);
                image2.setImageResource(0);
                image3.setImageResource(0);
                image4.setImageResource(0);
                image5.setImageResource(0);
                setColorNoteCreate();
        });
        image2.setOnClickListener(v->{
            SelectedNoteColor="#FDBE3B";
            image1.setImageResource(0);
            image2.setImageResource(R.drawable.ic_done);
            image3.setImageResource(0);
            image4.setImageResource(0);
            image5.setImageResource(0);
            setColorNoteCreate();
        });
        image3.setOnClickListener(v->{
            SelectedNoteColor="#FF4842";
            image1.setImageResource(0);
            image2.setImageResource(0);
            image3.setImageResource(R.drawable.ic_done);
            image4.setImageResource(0);
            image5.setImageResource(0);
            setColorNoteCreate();
        });
        image4.setOnClickListener(v->{
            SelectedNoteColor="#3A52Fc";
            image1.setImageResource(0);
            image2.setImageResource(0);
            image3.setImageResource(0);
            image4.setImageResource(R.drawable.ic_done);
            image5.setImageResource(0);
            setColorNoteCreate();
        });
        image5.setOnClickListener(v->{
            SelectedNoteColor="#000000";
            image1.setImageResource(0);
            image2.setImageResource(0);
            image3.setImageResource(0);
            image4.setImageResource(0);
            image5.setImageResource(R.drawable.ic_done);
            setColorNoteCreate();
        });
        if(AfterClickNote!=null&& AfterClickNote.getColor()!=null&&!AfterClickNote.getColor().trim().isEmpty()){
            switch (AfterClickNote.getColor()){
                case "#333333":
                    image1.performClick();
                    break;
                case "#FDBE3B":
                    image2.performClick();
                    break;
                case "#FF4842":
                    image3.performClick();
                    break;
                case "#3A52Fc":
                    image4.performClick();
                    break;
                case "#000000":
                    image5.performClick();
                    break;
            }
        }
        layoutAddimage.setOnClickListener(v->{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                        CreateNoteActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION
                );
            }else{
                selectedImage();
            }
        });
    }

    private void selectedImage() {
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectedImage();
            }else{
                Toast.makeText(this,"Permission Denied!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_SELECT_IMAGE&& resultCode==RESULT_OK){
            Uri selectedImageUri = data.getData();
            try{
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ImageNote.setImageBitmap(bitmap);
                SelectedImagePath=getPathFromUri(selectedImageUri);
            }catch(Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri selectedImageUri) {
        String path;
        Cursor cursor =getContentResolver().query(selectedImageUri,null,null,null,null);//đã có data của uri
        if(cursor==null){ //nếu null
            path = selectedImageUri.getPath();
        }else{
            cursor.moveToFirst();//trỏ về vị trí đầu
            int index =cursor.getColumnIndex("_data"); //chọn thứ tự ngay cột _data
            path = cursor.getString(index);//có được đường dẫn content
            cursor.close();
        };
        return path;
    }

    private void setColorNoteCreate(){
        GradientDrawable gradientDrawable =(GradientDrawable) viewColor.getBackground();
        gradientDrawable.setColor(Color.parseColor(SelectedNoteColor));
    }
    private void initMiscellaneous(){
        layoutll= findViewById(R.id.layoutMiscellaneous);
        bottomSheetBehavior= BottomSheetBehavior.from(layoutll);
        layoutll.findViewById(R.id.textMiscellaneous).setOnClickListener(v->{
                if(bottomSheetBehavior.getState() !=BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
        });
    }
    private void showAddDialog(){
        if(DialogAddUrl==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this); //khỏi tạo alertdialog
            View view  = LayoutInflater.from(this).inflate( //khơỉ tạo view và add từ container đã tạo sẵn
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlcontainer)
            );
            builder.setView(view); //container tạo sẵn h đã được thành dialog
            DialogAddUrl = builder.create();//gán đã tạo vào biến alertdialog
            if(DialogAddUrl.getWindow()!=null){
                DialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText input = view.findViewById(R.id.inputTextUrl); //tạo hằng input gọi từ edittext
            input.requestFocus();
            view.findViewById(R.id.textViewThem).setOnClickListener(v->{ //bắt sự kiện khi thêm
                if(input.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Nhập link web",Toast.LENGTH_SHORT).show();
                }else if(!Patterns.WEB_URL.matcher(input.getText().toString()).matches())//xác định đường link web đúng hay không của lớp util
                {
                    Toast.makeText(getApplicationContext(),"Nhập đúng link web",Toast.LENGTH_SHORT).show();
                }else{
                    layoutWebUrl.setVisibility(View.VISIBLE);
                    textWebUrl.setText(input.getText().toString());
                    DialogAddUrl.dismiss();
                }
            });
            view.findViewById(R.id.textViewHuy).setOnClickListener(v->{ //bắt sự kiện khi hủy
                DialogAddUrl.dismiss();
            });

        }
        DialogAddUrl.show();
    }
}