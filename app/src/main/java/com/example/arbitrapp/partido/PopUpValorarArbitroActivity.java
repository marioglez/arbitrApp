package com.example.arbitrapp.partido;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.arbitrapp.R;

public class PopUpValorarArbitroActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private RelativeLayout aceptar;
    private String arbitroUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_valorar_arbitro);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.5));

        arbitroUid = getIntent().getStringExtra("arbitro");

        ratingBar = findViewById(R.id.ratingbarArbitro);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                aceptar.setBackground(getResources().getDrawable(R.drawable.boton_verde));
                aceptar.setClickable(true);
            }
        });

        aceptar = findViewById(R.id.valorar_aceptar);
        aceptar.setClickable(false);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishPopUp();
            }
        });

        RelativeLayout cancelar = findViewById(R.id.valorar_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //FINALIZAR POPUP Y ENVIAR DATOS A PARTIDO
    private void finishPopUp(){
        Intent intent = new Intent();
        intent.putExtra("valoracion",ratingBar.getRating());
        intent.putExtra("arbitro",arbitroUid);
        setResult(0,intent);
        finish();
    }


    @Override
    public void onBackPressed() {
    }
}
