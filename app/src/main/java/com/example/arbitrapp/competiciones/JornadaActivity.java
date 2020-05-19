package com.example.arbitrapp.competiciones;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Jornada;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.partido.PartidoActivity;

import static com.example.arbitrapp.FirebaseData.PARTIDO_FINALIZADO;

public class JornadaActivity extends AppCompatActivity {

    private Jornada jornada;
    private RelativeLayout relativeLayout;
    private TableLayout tablaPartidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jornada);

        jornada = (Jornada) getIntent().getSerializableExtra("jornada");

        //ACTION BAR
        setTitle(jornada.getNombre());
        //boton flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        relativeLayout = findViewById(R.id.competicion_jornada_Layout);
        tablaPartidos = findViewById(R.id.tabla_competicion_jornada);

        pintarPartidos();
    }

    private void pintarPartidos(){
        for(final Partido p : jornada.getPartidos()){
            LayoutInflater inflater = LayoutInflater.from(JornadaActivity.this);
            TableRow row = (TableRow) inflater.inflate(R.layout.tabla_partido, relativeLayout, false);

            TextView tituloCaja =  row.findViewById(R.id.textView_equipos);
            String nombrePartido = p.getEquipoLocal().getNombre() + " Vs " + p.getEquipoVisitante().getNombre();
            tituloCaja.setText(nombrePartido);

            TextView competicion = row.findViewById(R.id.textView_liga);
            competicion.setText(p.getLiga());

            TextView lugar = row.findViewById(R.id.textView_lugar);
            lugar.setText(p.getLugar());

            TextView fecha = row.findViewById(R.id.textView_fecha);
            fecha.setText(p.getFecha());

            if (p.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                ImageView completado = row.findViewById(R.id.imageview_completado);
                completado.setVisibility(View.VISIBLE);
            }

            TableLayout tableLayout = row.findViewById(R.id.tabla_partido);
            tableLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(JornadaActivity.this, PartidoActivity.class).putExtra("partido", p));
                }
            });

            RelativeLayout comoLlegar = row.findViewById(R.id.comoLlegar);
            comoLlegar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + p.getEquipoLocal().getCampo().getUbicacion());
                    final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });

            tablaPartidos.addView(row);
        }
    }

    //Boton flecha atras
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
