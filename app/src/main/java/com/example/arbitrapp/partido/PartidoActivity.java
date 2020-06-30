package com.example.arbitrapp.partido;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import static com.example.arbitrapp.FirebaseData.*;

import com.example.arbitrapp.arbitrar.ArbitrarActivity;
import com.example.arbitrapp.modelos.Arbitro;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PartidoActivity extends AppCompatActivity {

    Partido partido;
    BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partido);

        partido = (Partido) getIntent().getSerializableExtra("partido");

        //ACTION BAR
        setTitle("PARTIDO");
        //boton flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingActionButton = findViewById(R.id.floating_action_button_partido);
        //Barra de navegacion en partido
        bottomNavigationView = findViewById(R.id.partido_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //Fragment inicial
        bottomNavigationView.setSelectedItemId(R.id.nav_informacion);

        isArbitrable();
    }

    private void isArbitrable() {
        if (!currentUser.getTipoUsuario().equals(USUARIO_INVITADO) || !currentUser.getTipoUsuario().equals(USUARIO_ADMIN)){
            if (!partido.getEstadoPartido().equals(PARTIDO_FINALIZADO) && currentUser.getTipoUsuario().equals(ARBITRO)){
                for (Arbitro arbitro : partido.getArbitros()) {
                    if (arbitro.getUid().equals(currentUser.getUid())) {
                        floatingActionButton.setVisibility(View.VISIBLE);
                        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivityForResult(new Intent(PartidoActivity.this, ArbitrarActivity.class)
                                        .putExtra("partido",partido),0);
                            }
                        });
                        return;
                    }
                }
            }
        }
    }

    //Boton flecha atras
    @Override
    public boolean onSupportNavigateUp(){
        volverAtras();
        return true;
    }

    @Override
    public void onBackPressed() {
        volverAtras();
    }

    private void volverAtras() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    //METODO PARA MOSTRAR OCULTAR MENU
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar_partido, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.ver_acta){
            if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                startActivity(new Intent(PartidoActivity.this, ActaActivity.class).putExtra("partido", partido));
            } else {
                Toast.makeText(PartidoActivity.this, "Acta no disponible", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_informacion:
                    selectedFragment = new PartidoInformacionFragment(partido);
                    break;
                case R.id.nav_local:
                    selectedFragment = new PartidoEquipoFragment(partido.getEquipoLocal(), partido);
                    break;
                case R.id.nav_visitante:
                    selectedFragment = new PartidoEquipoFragment(partido.getEquipoVisitante(), partido);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_partido_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            partido = (Partido) data.getSerializableExtra("partido");
            bottomNavigationView.setSelectedItemId(R.id.nav_informacion);
            if (partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)) {
                floatingActionButton.setVisibility(View.GONE);
            }
        }
    }
}
