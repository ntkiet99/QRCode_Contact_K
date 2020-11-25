package com.example.contactkapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.contactkapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Intent HomeActivity;

    private EditText userEMail, userPassword;
    private Button btnLogin;
    private ProgressBar loginProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();

        userEMail = findViewById(R.id.txt_login_email);
        userPassword = findViewById(R.id.txt_login_password);
        btnLogin = findViewById(R.id.btn_login);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgressBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
                final String email = userEMail.getText().toString();
                final String password = userPassword.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    ShowMessage("Hãy điền đầy đủ thông tin!");
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                }
                else{
                    SignIn(email, password);
                }
            }
        });

        // firebase
        mAuth = FirebaseAuth.getInstance();

        HomeActivity = new Intent(this, MainActivity.class);
        findViewById(R.id.register_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void SignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    ShowMessage("Đăng nhập thành công!");
                    UpdateUI();
                }
                else{
                    ShowMessage(task.getException().getMessage());
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private  void ShowMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_login);
    }

    private void UpdateUI() {
        startActivity(HomeActivity);
        finish();
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            UpdateUI();
        }
    }

    private  long backPressTime;
    @Override
    public  void onBackPressed(){
        if (backPressTime + 2000 > System.currentTimeMillis()){
            this.finishAffinity();
            System.exit(1);
            return;
        }else{
            Toast.makeText(getBaseContext(),"Bạn muốn thoát khỏi ứng dụng!",Toast.LENGTH_LONG).show();
        }
        backPressTime = System.currentTimeMillis();
    }
}