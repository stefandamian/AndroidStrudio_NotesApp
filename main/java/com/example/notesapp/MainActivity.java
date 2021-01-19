package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    Button  btnSettings;
    RecyclerView viewListNotes;
    Adapter adapter;
    List<Note> notes;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_options, menu);
        btnSettings = findViewById(R.id.btnOptions);
        // buton care face acces la optiuni
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debug_main","Creates main activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // creaza legatura cu baza de date
        NotesHandler handler = new NotesHandler(this);
        notes = sortByPreferences(handler.getNotes()); // extrage din baza de date notitele existente
        //si le sorteaza dupa preferintele setate in optiuni
        viewListNotes = findViewById(R.id.viewListNotes);
        viewListNotes.setLayoutManager(new LinearLayoutManager(this));//loc unde o sa apara notite
        adapter = new Adapter(this, notes);// se creaza un layout pentru notite
        viewListNotes.setAdapter(adapter);// se incarca layout-ul

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.btnOptions){
            Log.d("debug_main","Selected settings");
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.btnAbout){
            Toast.makeText(this,"App developed by Stefan Damian", Toast.LENGTH_SHORT).show();
        }
        else
            return super.onOptionsItemSelected(item);
        return true;
    }

    public void addNoteOnClick(android.view.View view){
        Intent intent = new Intent(getApplicationContext(),AddNote.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotesHandler handler = new NotesHandler(this);
        viewListNotes = findViewById(R.id.viewListNotes);
        viewListNotes.setLayoutManager(new LinearLayoutManager(this));
        notes = sortByPreferences(handler.getNotes());
        adapter = new Adapter(this, notes);
        viewListNotes.setAdapter(adapter);
    }

    List<Note> sortByPreferences(List<Note> notes){
        List<Note> sortNotes;
        String[] sortPreferences = new String[2];
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(!sharedPreferences.getBoolean("direction",true))
            sortPreferences[0] = "descending";
        else
            sortPreferences[0] = "ascending";
        if(sharedPreferences.getBoolean("sort_title",false))
            sortPreferences[1] = "title";
        else if(sharedPreferences.getBoolean("sort_time",false))
            sortPreferences[1] = "time";
        else
            sortPreferences[1] = "id";

        if(sortPreferences[1].equals("title")){
            for(int i=0; i < notes.size(); i++){
                for(int j=1; j < (notes.size() - i); j++){
                    if(notes.get(j-1).getTitle().compareToIgnoreCase(notes.get(j).getTitle()) > 0 ){
                        Note temp = notes.get(j - 1);
                        notes.set(j - 1, notes.get(j));
                        notes.set(j, temp);
                    }
                }
            }
        }else if(sortPreferences[1].equals("time")){
            for(int i=0; i < notes.size(); i++){
                for(int j=1; j < (notes.size() - i); j++){
                    if(notes.get(j-1).compareTime(notes.get(j)) > 0 ){
                        Note temp = notes.get(j - 1);
                        notes.set(j - 1, notes.get(j));
                        notes.set(j, temp);
                    }
                }
            }
        }

        if(sortPreferences[0].equals("ascending")){
            return notes;
        }
        else{
            sortNotes = new ArrayList<>();;
            for(int i = notes.size() - 1 ; i >= 0; i--){
                Note note = notes.get(i);
                sortNotes.add(note);
            }
            return sortNotes;
        }

    }

}
