package com.example.arbitrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import static com.example.arbitrapp.FirebaseData.COMPETICIONES;
import static com.example.arbitrapp.FirebaseData.PARTIDOS;
import static com.example.arbitrapp.FirebaseData.PARTIDO_ESTADO;
import static com.example.arbitrapp.FirebaseData.PARTIDO_FINALIZADO;
import static com.example.arbitrapp.FirebaseData.TEMPORADA_ACTUAL;
import static com.example.arbitrapp.FirebaseData.currentUser;

public class LoginScreen extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$Z)(?=.{8,20})");

    private EditText usuario, contrasena;
    private Button botonLogin;
    private ProgressBar progressBarLogin;
    private ImageView imagen_logo, imagen_error;
    private TextView texto_error1, texto_error2;
    private ArrayList<Partido> proximosPartidos;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        proximosPartidos = new ArrayList<>();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        usuario = findViewById(R.id.usuario);
        contrasena = findViewById(R.id.contraseña);
        botonLogin = findViewById(R.id.button_login);
        progressBarLogin = findViewById(R.id.progressBar_login);
        imagen_logo = findViewById(R.id.logoApp);
        imagen_error = findViewById(R.id.image_fail);
        texto_error1 = findViewById(R.id.txt_fail1);
        texto_error2 = findViewById(R.id.txt_fail2);
    }

    public void iniciarSesion (View view){
        String nombre = usuario.getText().toString();
        String clave = contrasena.getText().toString();

        botonLogin.setVisibility(View.INVISIBLE);
        progressBarLogin.setVisibility(View.VISIBLE);

        /*COMPROBAR AMBOS CAMPOS A LA VEZ
        boolean emailOK = validarEmail(nombre);
        boolean passwordOK = validarPassword(clave);

        if(!emailOK || !passwordOK){
            return;
        }
        */

        if(!validarEmail(nombre) || !validarPassword(clave)){
            return;
        }

        mAuth.signInWithEmailAndPassword(nombre, clave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = new Usuario();
                            obtenerProximosPartidos();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(LoginScreen.this, HomeScreen.class)
                                            .putExtra("proximosPartidos",proximosPartidos));
                                }
                            }, 3000);

                        } else {
                            botonLogin.setVisibility(View.VISIBLE);
                            progressBarLogin.setVisibility(View.INVISIBLE);
                            imagen_logo.setVisibility(View.INVISIBLE);
                            imagen_error.setVisibility(View.VISIBLE);
                            texto_error1.setVisibility(View.VISIBLE);
                            texto_error2.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {}

    private boolean validarEmail(String email){
        if(email.isEmpty()){
            usuario.setError("Email no puede estar vacío");
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
            contrasena.setError("Contraseña vacía");
            return false;
        } /*else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            //Formato de contraseña no valido
            contrasena.setError("Contraseña Débil");
            return false;
        }*/
        //SI NO FALLA
        return true;
    }

    private void obtenerProximosPartidos() {
        Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_YEAR);
        final int year = cal.get(Calendar.YEAR);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(TEMPORADA_ACTUAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int partidosEncontrados = 0;
                    int diasBuscados = 0;
                    do {
                        final int dia = day + diasBuscados;
                        final String idPartidos = dayOfYear(year, dia);
                        for (DataSnapshot sede : dataSnapshot.getChildren()) {
                            for (DataSnapshot categoria : sede.getChildren()) {
                                for (DataSnapshot jornada : categoria.child(PARTIDOS).getChildren()) {
                                    for (DataSnapshot diaPartido : jornada.getChildren()) {
                                        if (diaPartido.getKey().equals(idPartidos)) {
                                            for (DataSnapshot partido : diaPartido.getChildren()) {
                                                if (!partido.child(PARTIDO_ESTADO).getValue().toString().equals(PARTIDO_FINALIZADO)) {
                                                    proximosPartidos.add(new Partido(TEMPORADA_ACTUAL, sede.getKey(), categoria.getKey(), idPartidos, partido.getKey()));
                                                    partidosEncontrados++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        diasBuscados++;
                    } while (partidosEncontrados<3 && diasBuscados<7);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String dayOfYear(int year, int dia){
        //Comprobar si pasa del dia maximo del año
        Calendar cal = new GregorianCalendar(year, 11, 31);
        int totalDias = cal.get(Calendar.DAY_OF_YEAR);

        if(dia>totalDias){
            year += + 1;
            dia -= totalDias;
            return (String.valueOf(year) + String.valueOf(dia));
        } else {
            return (String.valueOf(year) + String.valueOf(dia));
        }
    }
}
