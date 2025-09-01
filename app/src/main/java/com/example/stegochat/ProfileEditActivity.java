package com.example.stegochat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileedit);
    }

    public void goToProfileEdit(View view) {
        Intent intent = new Intent(this, ProfileEditActivity.class);
        startActivity(intent);
    }
}
