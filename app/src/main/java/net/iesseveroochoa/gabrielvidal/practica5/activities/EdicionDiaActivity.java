package net.iesseveroochoa.gabrielvidal.practica5.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.iesseveroochoa.gabrielvidal.practica5.R;
import net.iesseveroochoa.gabrielvidal.practica5.fragments.DialogoAlerta;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiaDiario;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiarioDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EdicionDiaActivity extends AppCompatActivity {

    public final static String EXTRA_DIA_ENVIADO = "net.iesseveroochoa.gabrielvidal.practica5.activities.EdicionDiaActivity.enviado";

    private List<Integer> valores;
    private ArrayAdapter<Integer> adaptador;
    private Spinner spnValorVida;

    private TextView tvFecha;
    private Calendar c;
    private DatePickerDialog dpd;

    private DiaDiario dia;
    private String fecha;
    private int valoracionDia;
    private String resumen;
    private String contenido;
    private String fotoUri;
    private String latitud;
    private String longitud;

    private EditText etResumen;
    private EditText etContenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicion_dia);

        rellenarSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edicion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_guardar:
                rellenarDia();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void rellenarSpinner() {

        valores = new ArrayList<>();

        for (int i = 0; i <= 10; i++) {
            valores.add(i);
        }

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, valores);
        spnValorVida = findViewById(R.id.spnValorVida);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnValorVida.setAdapter(adaptador);
        spnValorVida.setSelection(5);
        spnValorVida.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valoracionDia = (int) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void darFecha(View view) {
        tvFecha = findViewById(R.id.tvFecha);

        c = Calendar.getInstance();
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int mes = c.get(Calendar.MONTH);
        int anyo = c.get(Calendar.YEAR);

        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fecha = year + "-" + (month + 1) + "-" + dayOfMonth;
                tvFecha.setText(fecha);
            }
        }, anyo, mes, dia);
        dpd.show();
    }

    public void rellenarDia() {

        etResumen = findViewById(R.id.etResumen);
        etContenido = findViewById(R.id.etContenido);
        resumen = etResumen.getText().toString();
        contenido = etContenido.getText().toString();

        if (fecha == null || resumen.equals("") || contenido.equals("")) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            DialogoAlerta dialogo = new DialogoAlerta();
            dialogo.show(fragmentManager, "tagAlerta");

        } else {

            dia = new DiaDiario(DiarioDB.fechaBDtoFecha(fecha), valoracionDia, resumen, contenido, "0", "0", "0");
            enviarDia(dia);

        }
    }

    public void enviarDia(DiaDiario dia) {

        Intent i = getIntent();
        i.putExtra(EXTRA_DIA_ENVIADO, dia);
        setResult(EdicionDiaActivity.RESULT_OK, i);
        finish();
    }
}
