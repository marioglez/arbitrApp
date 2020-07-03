package com.example.arbitrapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.arbitrapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        editTextEmail = findViewById(R.id.editText_resetPassword);
        btnResetPassword = findViewById(R.id.button_resetPassword);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = editTextEmail.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.introResetPass),Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setMessage(getResources().getString(R.string.resetSending));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.setLanguageCode("es");
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.resetOk),Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.resetError),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}


