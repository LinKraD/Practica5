package net.iesseveroochoa.gabrielvidal.practica5.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import net.iesseveroochoa.gabrielvidal.practica5.R;
import net.iesseveroochoa.gabrielvidal.practica5.fragments.DiaFragment;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiaDiario;

public class VerDiaActivity extends AppCompatActivity {

    public static final String EXTRA_VER_DIA = "ver_dia";

    private DiaDiario dia;
    private DiaFragment fragmento;

    private boolean esPantallaGrande;
    private FrameLayout frameContenedorDinamico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verdia);

        frameContenedorDinamico = (FrameLayout) findViewById(R.id.fl_grande);
        if (frameContenedorDinamico == null) {
            esPantallaGrande = false;
        } else {
            esPantallaGrande = true;
        }

        dia = getIntent().getParcelableExtra(EXTRA_VER_DIA);
        fragmento = (DiaFragment) getSupportFragmentManager().findFragmentById(R.id.frmDia);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fragmento.setDia(dia);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(esPantallaGrande){
            return true;
        }else{
            getMenuInflater().inflate(R.menu.menu_ver_pequenyo, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_borrar:
                Intent i = getIntent();
                i.putExtra(EXTRA_VER_DIA, dia);
                setResult(VerDiaActivity.RESULT_OK, i);
                finish();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }

    }
}
