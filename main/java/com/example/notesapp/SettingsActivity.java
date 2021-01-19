package com.example.notesapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class SettingsActivity extends AppCompatActivity {
    SettingsFragment mySettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mySettingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, mySettingsFragment)
                .commit();
    }

    public String[] getSortPreferences(){
        String[] sortPreferences = new String[2];
        if(!mySettingsFragment.setDirection.isChecked())
        sortPreferences[0] = "descending";
        else
        sortPreferences[0] = "ascending";
        if(mySettingsFragment.directionTitle.isChecked())
            sortPreferences[1] = "title";
        else if(mySettingsFragment.directionTime.isChecked())
            sortPreferences[1] = "time";
        else
            sortPreferences[1] = "id";
        return sortPreferences;
        }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private final int SET_PASSWORD = 1;
        private final int REQUEST_FOR_SET_PASSWORD = 2;
        private final int REQUEST_STOP = 3;

        private Preference lockPassword;
        private EditTextPreference editPassword;
        private SwitchPreferenceCompat setLock;
        private SwitchPreferenceCompat setDirection;
        private CheckBoxPreference directionID;
        private CheckBoxPreference directionTime;
        private CheckBoxPreference directionTitle;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.root_preferences);// mapari cu preferintele
            lockPassword = findPreference("set_password");
            editPassword = findPreference("password");
            setLock = findPreference("lock");
            setDirection = findPreference("direction");
            directionID = findPreference("sort_ID");
            directionTime = findPreference("sort_time");
            directionTitle = findPreference("sort_title");
            editPassword.setVisible(false);

            refreshPreferences();
            setLock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (!setLock.isChecked() && editPassword.getText().length() == 4) {
                        Intent intent = new Intent(getContext(), Unlock.class);
                        intent.putExtra("context", "request");
                        startActivityForResult(intent, REQUEST_STOP);
                    }
                    else if(!setLock.isChecked()){

                    }
                    refreshPreferences();
                    return true;
                }
            });
            directionID.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (directionID.isChecked()){
                        directionTime.setChecked(false);
                        directionTitle.setChecked(false);
                    }else{
                        directionID.setChecked(true);
                    }
                    return true;
                }
            });
            directionTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (directionTime.isChecked()){
                        directionID.setChecked(false);
                        directionTitle.setChecked(false);
                    }else{
                        directionID.setChecked(true);
                    }
                    return true;
                }
            });
            directionTitle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (directionTitle.isChecked()){
                        directionID.setChecked(false);
                        directionTime.setChecked(false);
                    }
                    else{
                        directionID.setChecked(true);
                    }
                    return true;
                }
            });

            lockPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(preference.getContext(),Unlock.class);
                    if (editPassword.getText().toString().length() != 4){
                        intent.putExtra("context","set");
                        startActivityForResult(intent, SET_PASSWORD);
                    }
                    else{
                        intent.putExtra("context","request");
                        startActivityForResult(intent, REQUEST_FOR_SET_PASSWORD);
                    }

                    return true;
                }
            });
        }

        public void refreshPreferences(){
            if (editPassword.getText().toString().length() != 4){
                lockPassword.setTitle("Set password");
            }
            else{
                lockPassword.setTitle("Change password");
            }
            if (setLock.isChecked()){
                lockPassword.setVisible(true);
            }
            else{
                lockPassword.setVisible(false);
            }
            if (!directionID.isChecked() && !directionTime.isChecked() && !directionTitle.isChecked()){
                directionID.setChecked(true);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_FOR_SET_PASSWORD){
                if(resultCode == RESULT_OK) {
                    Toast.makeText(lockPassword.getContext(), "Unlocked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),Unlock.class);
                    intent.putExtra("context","set");
                    startActivityForResult(intent, SET_PASSWORD);
                }
                else if (resultCode == RESULT_FIRST_USER)
                    Toast.makeText(lockPassword.getContext(),"wrong PIN",Toast.LENGTH_SHORT).show();
                }
            else if (requestCode == SET_PASSWORD){
                if(resultCode == RESULT_OK){
                    String pass = data.getStringExtra("password");
                    if (pass != null)
                        editPassword.setText(pass);
                    if(editPassword.getText().toString().length() == 4) {
                        lockPassword.setTitle("Change password");
                        Toast.makeText(lockPassword.getContext(),"PIN set",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(lockPassword.getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                }
                else if (resultCode == RESULT_FIRST_USER){
                    Toast.makeText(lockPassword.getContext(),"wrong PIN",Toast.LENGTH_SHORT).show();
                }
            }
            else if(requestCode == REQUEST_STOP){
                if (resultCode == RESULT_OK){
                    Toast.makeText(lockPassword.getContext(),"Lock inactive",Toast.LENGTH_SHORT).show();
                    editPassword.setText("0");
                    setLock.setChecked(false);
                }
                else if (resultCode == RESULT_FIRST_USER){
                    Toast.makeText(lockPassword.getContext(),"wrong PIN",Toast.LENGTH_SHORT).show();
                    setLock.setChecked(true);
                    refreshPreferences();
                }
                else if (resultCode == RESULT_CANCELED){
                    setLock.setChecked(true);
                }
            }
        }

    }




}