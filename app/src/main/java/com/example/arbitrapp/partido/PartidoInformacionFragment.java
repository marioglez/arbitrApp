package com.example.arbitrapp.partido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.arbitro.ArbitroActivity;
import com.example.arbitrapp.modelos.Arbitro;
import com.example.arbitrapp.modelos.Partido;

public class PartidoInformacionFragment extends Fragment {

    private Partido partido;
    private Arbitro arbitro;
    private TextView estado, local, visitante, lugar, fecha, hora, resultado, sinArbitros;
    private RelativeLayout relativeLayout;
    private TableLayout tablaArbitros;

    public PartidoInformacionFragment(Partido p){
        this.partido = p;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_partido_informacion, container, false);

        estado = view.findViewById(R.id.textView_estado_partido);
        local = view.findViewById(R.id.textView_local_partido);
        visitante = view.findViewById(R.id.textView_visitante_partido);
        lugar = view.findViewById(R.id.textView_lugar_partido);
        fecha = view.findViewById(R.id.textView_fecha_partido);
        hora = view.findViewById(R.id.textView_hora_partido);
        resultado = view.findViewById(R.id.textView_resultado_partido);
        sinArbitros = view.findViewById(R.id.sin_arbitros);

        relativeLayout = view.findViewById(R.id.layout_arbitros);
        tablaArbitros = view.findViewById(R.id.tablaArbitros);

        rellenarInfoPartido();

        return view;
    }

    private void rellenarInfoPartido(){
        estado.setText(partido.getEstadoPartido());
        local.setText(partido.getEquipoLocal().getNombre());
        visitante.setText(partido.getEquipoVisitante().getNombre());
        lugar.setText(partido.getLugar());
        fecha.setText(partido.getFecha());
        hora.setText(partido.getHora());
        String resultadoPartido = partido.getGolesLocal() + " - " +partido.getGolesVisitante();
        resultado.setText(resultadoPartido);

        if (!partido.getArbitros().isEmpty()) {
            rellenarArbitros();
        } else {
            sinArbitros.setVisibility(View.VISIBLE);
        }

    }

    private void rellenarArbitros(){
        tablaArbitros.removeAllViews();
        for(final Arbitro arbitro : partido.getArbitros()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final TableRow row = (TableRow) inflater.inflate(R.layout.tabla_arbitro, relativeLayout, false);

            ImageView imagenArbitro = row.findViewById(R.id.imagen_arbitro);
            try {
                Glide
                        .with(row)
                        .load(Uri.parse(arbitro.getImagen()))
                        .into(imagenArbitro);
            }catch (Exception e){
                Log.d("PARTIDO INFO", "pintarDatos: ERROR AL PINTAR IMAGEN");
            }

            TextView tipoArbitro = row.findViewById(R.id.tipo_arbitro);
            tipoArbitro.setText(arbitro.getCategoria());

            TextView nombreArbitro = row.findViewById(R.id.nombre_arbitro);
            String nombreArbitroCompleto = arbitro.getNombre() + " " + arbitro.getApellidos();
            nombreArbitro.setText(nombreArbitroCompleto);

            //Ir al Arbitro concreto
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    irAArbitro(arbitro);
                }
            });

            tablaArbitros.addView(row);
        }
    }

    private void irAArbitro(final Arbitro arbitro) {
        this.arbitro = arbitro;
        if (arbitro.getPartidos().isEmpty()){
            //ESPERAR POR LOS DATOS
            final ProgressDialog tempDialog;
            int i = 0;

            //Diseñar CUADRO DE DIALOGO MIENTRAS CARGA
            tempDialog = new ProgressDialog(getContext());
            tempDialog.setMessage("Recuperando Arbitro...");
            tempDialog.setCancelable(false);
            tempDialog.setProgress(i);
            tempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            tempDialog.show();
            arbitro.obtenerPartidos();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tempDialog.dismiss();
                    startActivity(new Intent(getContext(), ArbitroActivity.class).putExtra("arbitro", arbitro));
                }
            }, 3000);
        } else {
            startActivity(new Intent(getContext(), ArbitroActivity.class).putExtra("arbitro", arbitro));
        }

    }

    public void cargarPantalla() {
        startActivity(new Intent(getContext(), ArbitroActivity.class).putExtra("arbitro", arbitro));
    }
}
