package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ViewNote extends AppCompatActivity {
    private final int REQUEST_UNLOCK = 2;
    private final int REQUEST_EDIT = 1;


    boolean unlocked;
    TextView txtTitle, txtContent, txtTime;
    ImageView imgLock;
    FloatingActionButton btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debug_view_note","Creates view note activity");
        unlocked = false;// setare pram pt notite blocabile(daca este cazul)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        Intent intent = getIntent();
        NotesHandler handler = new NotesHandler(this); // conexiune la database
        txtTitle = findViewById(R.id.txtViewTitle);
        txtContent = findViewById(R.id.txtViewContent);
        txtTime = findViewById(R.id.txtViewTime);   //mapare view-uri
        imgLock = findViewById(R.id.imgViewLock);
        btnEdit = findViewById(R.id.btnViewEdit);

        long noteId = intent.getLongExtra("ID",0);// extragere ID notita
        Log.d("debug_view_note","selected id is " + noteId);
        final Note selectedNote = handler.getNote(noteId);
        if (selectedNote != null){ // daca notita exista, se afiseaza content-ul
            Log.d("debug_view_note","The note was found");
            selectedNote.logNote();
            getSupportActionBar().setTitle(selectedNote.getTitle());
            txtTitle.setText(selectedNote.getTitle());
            txtContent.setText(selectedNote.getContent());
            txtTime.setText("Last modified: " + selectedNote.getTime() + " " + selectedNote.getDate());

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isLocked = sharedPreferences.getBoolean("lock",false);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),AddNote.class);
                    intent.putExtra("noteId", selectedNote.getId());
                    startActivityForResult(intent, REQUEST_EDIT);
                }
            });

            if(isLocked && selectedNote.getLocked() == 1){
                imgLock.setVisibility(View.VISIBLE);
                if(!unlocked){
                    if(sharedPreferences.getString("password", "0").length() != 4){
                        Toast.makeText(getApplicationContext(), "Set PIN to open locked notes", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                    else{
                        Intent unlockIntent = new Intent(txtTitle.getContext(),Unlock.class);

                        unlockIntent.putExtra("context","request");
                        startActivityForResult(unlockIntent, REQUEST_UNLOCK);
                    }
                }
            }
        }
        else {
            Log.d("debug_view_note", "The note was not found");
            Toast.makeText(getApplicationContext(), "The selected node cannot be found in the database", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_EDIT){
            int caz = getIntent().getIntExtra("case",0);
            Log.d ("debug_view_note", String.valueOf(caz));
            if(resultCode == RESULT_OK)
                Toast.makeText(getApplicationContext(), "Note deleted successfully", Toast.LENGTH_SHORT).show();
            else if (resultCode == RESULT_FIRST_USER)
                Toast.makeText(getApplicationContext(), "Note could not be deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (requestCode == REQUEST_UNLOCK){
            if(resultCode == RESULT_OK){
                unlocked = true;
            }
            else if (resultCode == RESULT_CANCELED){
                finish();
            }
            else if (resultCode == RESULT_FIRST_USER){
                Toast.makeText(getApplicationContext(), "wrong PIN", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}
