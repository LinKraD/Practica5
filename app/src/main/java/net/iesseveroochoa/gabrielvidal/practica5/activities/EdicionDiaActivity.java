package net.iesseveroochoa.gabrielvidal.practica5.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import net.iesseveroochoa.gabrielvidal.practica5.R;
import net.iesseveroochoa.gabrielvidal.practica5.fragments.DialogoAlerta;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiaDiario;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiarioDB;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class EdicionDiaActivity extends AppCompatActivity {

    public final static String EXTRA_DIA_ENVIADO = "net.iesseveroochoa.gabrielvidal.practica5.activities.EdicionDiaActivity.enviado";

    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE=300;

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

    private ImageView ivImagen;
    private ConstraintLayout lyEdicion;

    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicion_dia);
        ivImagen=(ImageView) findViewById(R.id.ivImagen);
        lyEdicion = (ConstraintLayout) findViewById(R.id.lyEdicion);

        mayRequestStoragePermission();

        rellenarSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edicion, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_guardar:
                rellenarDia();
                return true;

            case R.id.action_img:

                showOptions();

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void rellenarDia() {

        etResumen = findViewById(R.id.etResumen);
        etContenido = findViewById(R.id.etContenido);
        resumen = etResumen.getText().toString();
        contenido = etContenido.getText().toString();
        fotoUri=ivImagen.getTransitionName();

        if (fecha == null || resumen.equals("") || contenido.equals("")) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            DialogoAlerta dialogo = new DialogoAlerta();
            dialogo.show(fragmentManager, "tagAlerta");

        } else {

            dia = new DiaDiario(DiarioDB.fechaBDtoFecha(fecha), valoracionDia, resumen, contenido, fotoUri, "0", "0");
            enviarDia(dia);

        }
    }

    public void enviarDia(DiaDiario dia) {

        Intent i = getIntent();
        i.putExtra(EXTRA_DIA_ENVIADO, dia);
        setResult(EdicionDiaActivity.RESULT_OK, i);
        finish();
    }

    private boolean mayRequestStoragePermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(lyEdicion, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    private void showOptions(){
        final CharSequence[] opcion={"Tomar foto","Galeria","Cancelar"};
        final AlertDialog.Builder builder=new AlertDialog.Builder(EdicionDiaActivity.this);

        builder.setTitle("Elegir una opción");
        builder.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(opcion[which] == "Tomar foto"){
                    openCamera();
                }else if (opcion[which]=="Galeria"){
                    Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,"Selecciona app de la imagen"),SELECT_PICTURE);

                } else {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    private void openCamera(){
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if(isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){

            switch (requestCode) {
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });


                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    ivImagen.setImageBitmap(bitmap);
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    ivImagen.setImageURI(path);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(EdicionDiaActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EdicionDiaActivity.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }
}
