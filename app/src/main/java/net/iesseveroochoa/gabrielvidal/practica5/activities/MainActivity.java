package net.iesseveroochoa.gabrielvidal.practica5.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.iesseveroochoa.gabrielvidal.practica5.R;
import net.iesseveroochoa.gabrielvidal.practica5.fragments.DiaFragment;
import net.iesseveroochoa.gabrielvidal.practica5.fragments.ListaFragment;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiaDiario;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiarioContract;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiarioDB;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  SharedPreferences.OnSharedPreferenceChangeListener{

    private final static int DIA = 0;
    public final static String EXTRA_DIA_ENVIADO = "dia_enviado";
    public static final String EXTRA_VER_DIA = "ver_dia";
    public static final String EXTRA_COLOR = "color_fondo";
    public final static int EXTRA_DIA = 1;

    SharedPreferences preferencias;

    private DiarioDB diarioDB;
    private List<DiaDiario> dias = new ArrayList<>();
    private ListaFragment listaFragment;
    private boolean esPantallaGrande;
    private DiaFragment diaFragment;
    private FrameLayout frameContenedorDinamico;
    private TextView tvNoDia;

    private boolean cambiadaConfiguracionChicoChica;
    private String colorFondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this,R.xml.preferencias,false);

        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        preferencias.registerOnSharedPreferenceChangeListener(this);

        frameContenedorDinamico = (FrameLayout) findViewById(R.id.fl_grande);
        if (frameContenedorDinamico == null) {
            esPantallaGrande = false;
        } else {
            esPantallaGrande = true;
        }

        tvNoDia=findViewById(R.id.tv_NoDia);

        listaFragment = (ListaFragment) getSupportFragmentManager().findFragmentById(R.id.fgm_Lista);

        if (esPantallaGrande) {
            colorFondo = preferencias.getString("genero_preference", "");
            if (colorFondo.equals("chico")){
                frameContenedorDinamico.setBackgroundColor(ContextCompat.getColor(this, R.color.chico));
            } else if (colorFondo.equals("chica")){
                frameContenedorDinamico.setBackgroundColor(ContextCompat.getColor(this, R.color.chica));
            }
        }

        listaFragment.setListaDiarioListener(new ListaFragment.OnListaDiarioListener() {
            @Override
            public void onDiaSeleccionado(DiaDiario dia) {
                if (esPantallaGrande) {
                    tvNoDia.setVisibility(View.INVISIBLE);
                    crearFragment(dia);
                    guardaDiaPreferencias();
                } else {
                    colorFondo=preferencias.getString("genero_preference","");
                    Intent i = new Intent(MainActivity.this, VerDiaActivity.class);
                    i.putExtra(VerDiaActivity.EXTRA_VER_DIA, dia);
                    i.putExtra(VerDiaActivity.EXTRA_COLOR,colorFondo);
                    startActivityForResult(i, EXTRA_DIA);
                }
            }
        });

        diarioDB = new DiarioDB(this);
        diarioDB.open();
        diarioDB.cargaDatosPrueba();
    }

    private void crearFragment(DiaDiario dia) {
        diaFragment = DiaFragment.newInstance(dia);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_grande, diaFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(esPantallaGrande){
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }else{
            getMenuInflater().inflate(R.menu.menu_main_pequenyo, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_anyadir:
                anyadirRegistro();
                return true;
            case R.id.action_borrar:
                borrarRegistro(dias);
                return true;
            case R.id.action_ordenar:
                ordenar();
                return true;
            case R.id.action_valorarVida:
                valorarVida();
                return true;
            case R.id.action_opciones:
                startActivity(new Intent(MainActivity.this, OpcionesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public List<DiaDiario> meterDia(DiaDiario dia) {
        List<DiaDiario> dias = new ArrayList<>();
        dias.add(dia);
        return dias;
    }

    public void anyadirRegistro() {
        Intent i = new Intent(MainActivity.this, EdicionDiaActivity.class);
        startActivityForResult(i, DIA);

    }

    public void borrarRegistro(List<DiaDiario> dias) {
        AlertDialog.Builder dialogBorrar = new AlertDialog.Builder(MainActivity.this);

        dialogBorrar.setTitle(R.string.aviso).setMessage(R.string.avisoBorrar).setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {

                    listaFragment.borraDia(diaFragment.getDia());

                    FragmentManager manager = getSupportFragmentManager();

                    if (manager.getBackStackEntryCount() > 0) {

                        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        tvNoDia.setVisibility(View.VISIBLE);

                    }

                } catch (Exception ex) {

                    Toast t = Toast.makeText(getApplicationContext(), R.string.no_seleccionado, Toast.LENGTH_SHORT);
                    t.show();

                }
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        }).show();
    }

    public void ordenar() {
        final CharSequence[] items = {"Fecha", "Valoración", "Resumen"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Ordenar por");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast t = Toast.makeText(getApplicationContext(), "Ordenado por: " + items[item], Toast.LENGTH_SHORT);
                t.show();
                dialog.dismiss();

                switch (item) {
                    case 0:
                        listaFragment.ordenar(DiarioContract.DiaDiarioEntries.FECHA);
                        break;
                    case 1:
                        listaFragment.ordenar(DiarioContract.DiaDiarioEntries.VALORACIONDIA);
                        break;
                    case 2:
                        listaFragment.ordenar(DiarioContract.DiaDiarioEntries.RESUMEN);
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    public void valorarVida() {

        float valorVida = diarioDB.valoraVida();

        Toast t = Toast.makeText(getApplicationContext(), "Valor vida: " + valorVida, Toast.LENGTH_SHORT);
        t.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DIA && resultCode == RESULT_CANCELED) {

            Toast.makeText(this, R.string.resultadocancelado, Toast.LENGTH_SHORT).show();

        }

        if (requestCode == DIA && resultCode == RESULT_OK) {

            DiaDiario diaRecibido = data.getParcelableExtra(EXTRA_DIA_ENVIADO);
            listaFragment.actualizaListaConDia(diaRecibido);

        }

        if (requestCode == EXTRA_DIA && resultCode == RESULT_OK) {
            AlertDialog.Builder dialogBorrar = new AlertDialog.Builder(MainActivity.this);
            dialogBorrar.setTitle(R.string.aviso).setMessage(R.string.avisoBorrar)
                    .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DiaDiario diaRecibidoVerDia = data.getParcelableExtra(EXTRA_VER_DIA);
                            listaFragment.borraDia(diaRecibidoVerDia);

                            FragmentManager manager = getSupportFragmentManager();

                            if (manager.getBackStackEntryCount() > 0) {
                                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }
    }

    private void mostrarDiaSesionAnterior(){
        SharedPreferences pref=getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String json =pref.getString("Ultimodia","");
        if(json!="") {
            DiaDiario dia = gson.fromJson(json, DiaDiario.class);
            crearFragment(dia);
        }
    }

    private void guardaDiaPreferencias(){
        SharedPreferences pref=getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor=pref.edit();
        String jsonDia="";
        if (diaFragment!=null){
            Gson gson=new Gson();
            jsonDia=gson.toJson(diaFragment.getDia());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (esPantallaGrande){
            mostrarDiaSesionAnterior();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "nombre_preference":
                if (esPantallaGrande){
                    this.setTitle("Diario de "+preferencias.getString(key,""));
                }
            case "genero_preference":
                if (preferencias.getString(key,"").equals("chico")){
                    if (esPantallaGrande){
                        if (diaFragment==null) {
                            frameContenedorDinamico.setBackgroundColor(ContextCompat.getColor(this, R.color.chico));
                        } else {
                            diaFragment.setColorFondo("chico");
                        }
                    } else {
                        colorFondo="chico";
                    }
                } else if (preferencias.getString(key,"").equals("chica")){
                    if (esPantallaGrande){
                        if (diaFragment==null) {
                        frameContenedorDinamico.setBackgroundColor(ContextCompat.getColor(this,R.color.chica));
                        } else {
                            diaFragment.setColorFondo("chica");
                        }
                    } else {
                        colorFondo="chica";
                    }
                }
        }
    }
}