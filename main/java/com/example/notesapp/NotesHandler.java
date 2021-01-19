package com.example.notesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;


public class NotesHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "NotesAppDatabase";
    private static final String DATABASE_NOTES_TABLE = "notes";
    private static final String CRYPT_ALGORITHM = "AES/ECB/PKCS5Padding";

    //column
    private static final String KEY_ID = "id";
    private static final String KEY_LOCK = "isLocked";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";



    NotesHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase handler){
        Log.d("debug_handler","handler constructor");
        String createQuery = "CREATE TABLE " + DATABASE_NOTES_TABLE + "( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_LOCK + " INT, " +
                KEY_TITLE + " TEXT, " +
                KEY_CONTENT + " TEXT, " +
                KEY_DATE + " TEXT, "+
                KEY_TIME +" TEXT " + " );";
        handler.execSQL(createQuery); // se executa cu succes doar daca DATABASE_NOTES_TABLE
                                      // nu exista in database
                                      // daca insa table-ul exista, codul adaugat este irelevant
    }

    @Override
    public void onUpgrade(SQLiteDatabase handler, int oldVersion, int newVersion){
        if(oldVersion >= newVersion)
            return;
        Log.d("debug_handler","Destroy table");
        handler.execSQL("DROP TABLE IF EXISTS " + DATABASE_NOTES_TABLE);
        onCreate(handler);
    }

    public long addNote(Note note){
        SQLiteDatabase handler = this.getWritableDatabase();
        ContentValues c = new ContentValues(); // dictionar pentru valorile notitei
        c.put(KEY_LOCK, note.getLocked());
        c.put(KEY_TITLE, note.getTitle());
        c.put(KEY_CONTENT, note.getContent());
        c.put(KEY_DATE, note.getDate());
        c.put(KEY_TIME, note.getTime());

        note.logNote();

        long id = handler.insert(DATABASE_NOTES_TABLE, null, c);
        if ( id != -1 )
            Log.d("debug_handler", "Creates note with id " + id);
        else {
            Log.d("debug_handler", "Could not create an node");
            onUpgrade(handler,0,1); // daca nu s-a reusit adaugarea unei notite
        }                                   // din cauze generice (table alterata, id-uri modificare)
                                            // se distruge continutul tablei
        return id;
    }

    public Note getNote(long id){
        SQLiteDatabase handler = this.getReadableDatabase();
        Note foundNote;
        String searchQuery = " select * from " + DATABASE_NOTES_TABLE +
                " where " + KEY_ID + " = " + id + ";";
        Cursor cursor = null;

        try {
            cursor = handler.rawQuery(searchQuery, null);
            Log.d("debug_handler", "created cursor from query; searched for id " + id);
            if (cursor != null && cursor.moveToFirst()) {
                foundNote = new Note(cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_CONTENT)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TIME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_LOCK)));
                foundNote.logNote();
                Log.d("debug_handler", "is found " + foundNote.getId());

            } else {
                foundNote = null;
                Log.d("debug_handler", "note not found");
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return  foundNote;
    }

    public boolean deleteNote(long id){
        SQLiteDatabase handler = this.getWritableDatabase();
        int numberOfNotes = handler.delete(DATABASE_NOTES_TABLE, KEY_ID +" = ?",
                new String[]{String.valueOf(id)});
        if (numberOfNotes < 1)
            return false;
        else if (numberOfNotes == 1)
            Log.d("debug_handler", "note with id: " + id + " is deleted");
        else
            Log.d("debug_handler", "More than 1 notes where deleted," +
                    " something went wrong");
        return true;
    }

    public boolean editNote(long id, Note note){
        SQLiteDatabase handler = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(KEY_TITLE,note.getTitle());
        content.put(KEY_CONTENT,note.getContent());
        content.put(KEY_DATE,note.getDate());
        content.put(KEY_TIME,note.getTime());
        content.put(KEY_LOCK,note.getLocked());

        int numberOfNotes = handler.update(DATABASE_NOTES_TABLE,content,KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        if (numberOfNotes < 1)
            return false;
        else if (numberOfNotes == 1)
            Log.d("debug_handler", "note with id: " + id + " is deleted");
        else
            Log.d("debug_handler", "More than 1 notes where deleted");
        return true;
    }

    public List<Note> getNotes(){
        SQLiteDatabase handler = this.getReadableDatabase();
        List<Note> allNotes = new ArrayList<>();
        String searchQuery = "select * from " + DATABASE_NOTES_TABLE;
        Cursor cursor = null;

        try {
            cursor = handler.rawQuery(searchQuery, null);
            if (cursor != null && cursor.moveToNext()) {
                Log.d("debug", "There are elements in the database");
                do {
                    Note note = new Note(cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(KEY_CONTENT)),
                            cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                            cursor.getString(cursor.getColumnIndex(KEY_TIME)),
                            cursor.getInt(cursor.getColumnIndex(KEY_LOCK)));
                    note.logNote();
                    Log.d("debug_handler", "found in database note with id: " + note.getId());
                    allNotes.add(note);
                }
                while (cursor.moveToNext());
            } else {
                Log.d("debug", "There are NO elements in the database");
            }
        }finally {
            if (cursor != null)
                cursor.close();
        }
        return  allNotes;
    }

}
