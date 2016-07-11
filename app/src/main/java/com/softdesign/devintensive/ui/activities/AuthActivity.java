package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.softdesign.devintensive.R;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mLoginButton = (Button)findViewById(R.id.auth_login_button);
        mLoginButton.setOnClickListener(this);

        EditText editText = (EditText) findViewById(R.id.auth_login_email);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.requestFocus();

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
