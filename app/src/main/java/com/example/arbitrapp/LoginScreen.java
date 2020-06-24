package com.example.arbitrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.arbitrapp.home.HomeScreen;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import static com.example.arbitrapp.FirebaseData.USUARIO_INVITADO_CLAVE;
import static com.example.arbitrapp.FirebaseData.USUARIO_INVITADO_EMAIL;
import static com.example.arbitrapp.FirebaseData.currentUser;

public class LoginScreen extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$Z)(?=.{8,20})");

    private EditText usuario, contrasena;
    private Button botonLogin, botonInvitado;
    private ProgressBar progressBarLogin;
    private ImageView imagen_logo, imagen_error;
    private TextView texto_error1, texto_error2;


    private FirebaseAuth mAuth;
    private CountDownLatch countDownLatchUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        usuario = findViewById(R.id.usuario);
        contrasena = findViewById(R.id.contraseña);
        botonLogin = findViewById(R.id.button_login);
        botonInvitado = findViewById(R.id.button_invitado);
        progressBarLogin = findViewById(R.id.progressBar_login);
        imagen_logo = findViewById(R.id.logoApp);
        imagen_error = findViewById(R.id.image_fail);
        texto_error1 = findViewById(R.id.txt_fail1);
        texto_error2 = findViewById(R.id.txt_fail2);

        botonInvitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(USUARIO_INVITADO_EMAIL,USUARIO_INVITADO_CLAVE);
            }
        });
    }

    public void iniciarSesion (View view){
        String nombre = usuario.getText().toString();
        String clave = contrasena.getText().toString();

        if(!validarEmail(nombre) || !validarPassword(clave)){
            return;
        } else {
            botonLogin.setVisibility(View.INVISIBLE);
            progressBarLogin.setVisibility(View.VISIBLE);
            signIn(nombre, clave);
        }
    }

    @Override
    public void onBackPressed() {}

    private boolean validarEmail(String email){
        if(email.isEmpty()){
            usuario.setError("Introduzca el email");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //Formato de email no valido
            usuario.setError("Email no válido");
            return false;
        }
        //SI NO FALLA
        return true;
    }

    private boolean validarPassword(String password){
        if(password.isEmpty()){
            contrasena.setError("Introduzca la contraseña");
            return false;
        } /*else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            //Formato de contraseña no valido
            contrasena.setError("Contraseña Débil");
            return false;
        }*/
        //SI NO FALLA
        return true;
    }

    private void irAHome() {
        startActivity(new Intent(LoginScreen.this, HomeScreen.class));
    }

    public void signIn(String nombre, String clave) {
        mAuth.signInWithEmailAndPassword(nombre, clave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = new Usuario(countDownLatchUsuario);
                            currentUser.start();
                            try{
                                countDownLatchUsuario.await();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            irAHome();
                        } else {
                            botonLogin.setVisibility(View.VISIBLE);
                            progressBarLogin.setVisibility(View.INVISIBLE);
                            imagen_logo.setVisibility(View.INVISIBLE);
                            botonInvitado.setVisibility(View.INVISIBLE);
                            imagen_error.setVisibility(View.VISIBLE);
                            texto_error1.setVisibility(View.VISIBLE);
                            texto_error2.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
