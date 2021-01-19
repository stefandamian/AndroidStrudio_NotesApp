package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class Unlock extends AppCompatActivity {
    EditText txtPassword;
    Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtPassword = findViewById(R.id.txtPassword);
        btnSubmit = findViewById(R.id.btnPinSubmit);
    }

    public void onClickNumber(android.view.View view){
        Button selectedButton = (Button) view;
        String password = txtPassword.getText().toString();
        password = password + selectedButton.getText().toString();
        txtPassword.setText(password);
        if (txtPassword.getText().toString().length() == 4){
            btnSubmit.setEnabled(true);
        }
        else{
            btnSubmit.setEnabled(false);
        }
    }

    public void onClickDel(android.view.View view){
        String password = txtPassword.getText().toString();
        if (password.length() > 1){
            password = password.substring(0, password.length() - 1);
        }
        else if (password.length() == 1){
            password = "";
        }
        txtPassword.setText(password);
        if (txtPassword.getText().toString().length() == 4){
            btnSubmit.setEnabled(true);
        }
        else{
            btnSubmit.setEnabled(false);
        }
    }

    public void onClickSubmit(android.view.View view){
        Intent intent = getIntent();
        String context = intent.getStringExtra("context");
        if(context == null){
            setResult(RESULT_CANCELED);
            finish();
        }
        else if (context.equals("set")){
            intent.putExtra("password", txtPassword.getText().toString());
            setResult(RESULT_OK,intent);
            finish();
        }
        else if (context.equals("request")){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String password = sharedPreferences.getString("password","0");
            if (password.equals(txtPassword.getText().toString()))
                setResult(RESULT_OK);
            else
                setResult(RESULT_FIRST_USER);
            finish();
        }




    }
}
