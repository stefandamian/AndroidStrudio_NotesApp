package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateIntervalInfo;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class AddNote extends AppCompatActivity {
    Switch swLock;
    TextInputEditText txtTitle;
    EditText txtContent;
    Button btnNoteSubmit, btnNoteDelete;
    Calendar c;
    String currentDate, currentTime;
    String actionBarTitleDefault;
    long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        txtTitle = findViewById(R.id.txtNoteTitle);
        txtContent = findViewById(R.id.textNoteContent);
        swLock = findViewById(R.id.swNoteLock);
        btnNoteSubmit = findViewById(R.id.btnNoteSubmit);
        btnNoteDelete = findViewById(R.id.btnNoteDelete);
        c = Calendar.getInstance();
        Intent intent = getIntent();
        Note note = null;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLocked = sharedPreferences.getBoolean("lock",false);
        if (isLocked){
            swLock.setVisibility(View.VISIBLE);
        }
        else{
            swLock.setVisibility(View.GONE);
        }

        noteId = intent.getLongExtra("noteId",-1);
        if (noteId != -1){ // se face diferentierea dintre cazul de edit sau add
            final NotesHandler handler = new NotesHandler(this);
            note = handler.getNote(noteId);
            actionBarTitleDefault = note.getTitle();
            btnNoteSubmit.setText("Save");
            txtTitle.setText(actionBarTitleDefault);
            txtContent.setText(note.getContent());
            if (isLocked){
                if (note.getLocked() == 1)
                    swLock.setChecked(true);
                else
                    swLock.setChecked(false);
            }                                      // daca este folosit pentru edit
            btnNoteDelete.setVisibility(View.VISIBLE);// se poate observa si butonul de delete
            btnNoteDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    if(handler.deleteNote(noteId)) {
                        setResult(RESULT_OK,intent);
                    }
                    else{
                        setResult(RESULT_CANCELED,intent);
                    }
                    finish();
                }
            });

        }
        else{// cazul de adaugare nota
            actionBarTitleDefault = "New Title";
        }
        getSupportActionBar().setTitle(actionBarTitleDefault);

        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    getSupportActionBar().setTitle(s);
                }
                else{
                    if (noteId == -1)
                        getSupportActionBar().setTitle("New Note");
                    else
                        getSupportActionBar().setTitle(actionBarTitleDefault);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    public void OnClickSubmit(android.view.View view){
        String title = txtTitle.getText().toString();
        String content = txtContent.getText().toString();
        if (title.length() == 0 || content.length() == 0){
            String toastText;
            if (title.length() == 0)
                toastText = "Please select a title";
            else
                toastText = "Please type some content";
            Toast.makeText(view.getContext(),toastText,Toast.LENGTH_SHORT).show();
        }
        else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            NotesHandler noteHandler = new NotesHandler(this);
            int lockOn;

            if (sharedPreferences.getBoolean("lock", false)) {
                if (swLock.isChecked())
                    lockOn = 1;
                else
                    lockOn = 0;
            } else {
                lockOn = 0;
            }

            currentDate = myDate(c);
            currentTime = myTime(c);

            Note note = new Note(title, content, currentDate, currentTime, lockOn);

            if (noteId == -1) {
                Intent intent = getIntent();
                if (noteHandler.addNote(note) != -1) {
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_FIRST_USER, intent);
                }
                finish();
            } else {
                Intent intent = getIntent();
                if (noteHandler.editNote(noteId, note)) {
                    Toast.makeText(getApplicationContext(), "Note edited successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Note could not be modified", Toast.LENGTH_SHORT).show();
                }
                setResult(2);
                finish();
            }
        }
    }

    public String myDate(Calendar c){
        int aux = c.get(Calendar.DAY_OF_MONTH);
        String result;
        if (aux < 10)
            result = "0" + aux;
        else
            result = String.valueOf(aux);
        aux = c.get(Calendar.MONTH) + 1 ;
        if (aux < 10)
            result = result + "." + "0" + aux;
        else
            result = result + "." + String.valueOf(aux);

        return result + "." + c.get(Calendar.YEAR);
    }

    public String myTime(Calendar c){
        int hours = c.get(Calendar.HOUR);
        int minutes = c.get(Calendar.MINUTE);
        String sMinutes;
        if (minutes<10)
            sMinutes = "0" + minutes;
        else
            sMinutes = String.valueOf(minutes);
        if (hours > 12) {
            hours = hours - 12;
        }
        String AM_PM = "";
        if (c.get(Calendar.AM_PM) == 1) {
            AM_PM = "PM";
        } else {
            AM_PM = "AM";
        }
        if (hours<10)
            return "0"+hours + ":" + sMinutes + AM_PM;
        else
            return hours + ":" + sMinutes + AM_PM;
    }

}
