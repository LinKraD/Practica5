package net.iesseveroochoa.gabrielvidal.practica5.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DiarioDB {
    private final static int DB_VERSION=1;
    private final static String DB_NOMBRE="diario.db";

    private final static String SQL_CREATE="CREATE TABLE "+
            DiarioContract.DiaDiarioEntries.TABLE_NAME+" ("+
            DiarioContract.DiaDiarioEntries._ID+" integer primary key autoincrement,"+
            DiarioContract.DiaDiarioEntries.FECHA+" TEXT UNIQUE NOT NULL, "+
            DiarioContract.DiaDiarioEntries.VALORACIONDIA+" INTEGER NOT NULL, "+
            DiarioContract.DiaDiarioEntries.RESUMEN+" TEXT NOT NULL, "+
            DiarioContract.DiaDiarioEntries.CONTENIDO+" TEXT NOT NULL, " +
            DiarioContract.DiaDiarioEntries.FOTO_URI+" TEXT NOT NULL, " +
            DiarioContract.DiaDiarioEntries.LATITUD+" TEXT NOT NULL, "+
            DiarioContract.DiaDiarioEntries.LONGITUD+" TEXT NOT NULL);";

    private final static String SQL_DROP="DROP TABLE IF EXISTS "+ DiarioContract.DiaDiarioEntries.TABLE_NAME;

    private final static String SQL_UPDATE = "UPDATE " + DiarioContract.DiaDiarioEntries.TABLE_NAME +
            " SET " +
            DiarioContract.DiaDiarioEntries.FECHA + "=? , " +
            DiarioContract.DiaDiarioEntries.VALORACIONDIA + "=? , " +
            DiarioContract.DiaDiarioEntries.RESUMEN + "=? , " +
            DiarioContract.DiaDiarioEntries.CONTENIDO + "=? , " +
            DiarioContract.DiaDiarioEntries.FOTO_URI + "=? , " +
            DiarioContract.DiaDiarioEntries.LATITUD + "=? , " +
            DiarioContract.DiaDiarioEntries.LONGITUD + "=? WHERE " + DiarioContract.DiaDiarioEntries._ID + " = ?";

    private final static String SQL_INSERT = "INSERT INTO " + DiarioContract.DiaDiarioEntries.TABLE_NAME +
            " VALUES(" +
            DiarioContract.DiaDiarioEntries._ID + "," +
            DiarioContract.DiaDiarioEntries.FECHA + "," +
            DiarioContract.DiaDiarioEntries.VALORACIONDIA + "," +
            DiarioContract.DiaDiarioEntries.RESUMEN + "," +
            DiarioContract.DiaDiarioEntries.CONTENIDO + "," +
            DiarioContract.DiaDiarioEntries.FOTO_URI + "," +
            DiarioContract.DiaDiarioEntries.LATITUD + "," +
            DiarioContract.DiaDiarioEntries.LONGITUD +
            ") VALUES (?,?,?,?,?,?,?,?)";

    private final static String SQL_SELECT = "SELECT * FROM " + DiarioContract.DiaDiarioEntries.TABLE_NAME;


    private final static String SQL_VALORACIONDIA_AVG = "SELECT AVG(" + DiarioContract.DiaDiarioEntries.VALORACIONDIA + ") FROM " + DiarioContract.DiaDiarioEntries.TABLE_NAME;

    private DBHelper dbH;
    private SQLiteDatabase db;

    public DiarioDB(Context context) {

        dbH = new DBHelper(context);

    }

    public void open() {

        db = dbH.getWritableDatabase();

    }

    public void close() {

        db.close();

    }

    public void borraDia(DiaDiario dia) {

        db.delete(DiarioContract.DiaDiarioEntries.TABLE_NAME, DiarioContract.DiaDiarioEntries.FECHA + "= ?", new String[]{fechaToFechaDB(dia.getFecha())});

    }

    public void anyadeActualizaDia(DiaDiario dia) {

        ContentValues values = new ContentValues();
        values.put(DiarioContract.DiaDiarioEntries.FECHA, fechaToFechaDB(dia.getFecha()));
        values.put(DiarioContract.DiaDiarioEntries.VALORACIONDIA, dia.getValoracionDia());
        values.put(DiarioContract.DiaDiarioEntries.RESUMEN, dia.getResumen());
        values.put(DiarioContract.DiaDiarioEntries.CONTENIDO, dia.getContenido());
        values.put(DiarioContract.DiaDiarioEntries.FOTO_URI, dia.getFotoUri());
        values.put(DiarioContract.DiaDiarioEntries.LATITUD, dia.getLatitud());
        values.put(DiarioContract.DiaDiarioEntries.LONGITUD, dia.getLongitud());

        try {

            db.insertOrThrow(DiarioContract.DiaDiarioEntries.TABLE_NAME, null, values);

        } catch (SQLException e) {

            db.update(DiarioContract.DiaDiarioEntries.TABLE_NAME, values, DiarioContract.DiaDiarioEntries.FECHA + "=?", new String[]{fechaToFechaDB(dia.getFecha())});

        }

    }

    public Cursor obtenDiario(String ordenadoPor) {

        Cursor c = db.query(DiarioContract.DiaDiarioEntries.TABLE_NAME, null, null, null, null, null, ordenadoPor);
        return c;

    }

    public float valoraVida() {

        Cursor c = db.rawQuery("SELECT AVG(" + DiarioContract.DiaDiarioEntries.VALORACIONDIA + ") FROM " + DiarioContract.DiaDiarioEntries.TABLE_NAME, null);

        float valoracion = 0;


        if (c.getCount() > 0) {

            c.moveToFirst();
            valoracion = c.getFloat(0);
            String format = String.format("%.02f", valoracion);
            valoracion = Float.parseFloat(format);

        }

        return valoracion;

    }

    public static Date fechaBDtoFecha(String f) {

        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = null;

        try {
            fecha = formatoDelTexto.parse(f);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return fecha;

    }

    public static String fechaToFechaDB(Date fecha) {

        DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(fecha);

    }

    public static DiaDiario cursorADiaDiario(Cursor c) {

        int indiceColumna;

        indiceColumna = c.getColumnIndex(DiarioContract.DiaDiarioEntries.FECHA);
        String fecha = c.getString(indiceColumna);

        indiceColumna = c.getColumnIndex(DiarioContract.DiaDiarioEntries.VALORACIONDIA);
        String valoracionDia = c.getString(indiceColumna);

        indiceColumna = c.getColumnIndex(DiarioContract.DiaDiarioEntries.RESUMEN);
        String resumen = c.getString(indiceColumna);

        indiceColumna = c.getColumnIndex(DiarioContract.DiaDiarioEntries.CONTENIDO);
        String contenido = c.getString(indiceColumna);

        indiceColumna = c.getColumnIndex(DiarioContract.DiaDiarioEntries.FOTO_URI);
        String foto = c.getString(indiceColumna);

        indiceColumna = c.getColumnIndex(DiarioContract.DiaDiarioEntries.LATITUD);
        String latitud = c.getString(indiceColumna);

        indiceColumna = c.getColumnIndex(DiarioContract.DiaDiarioEntries.LONGITUD);
        String longitud = c.getString(indiceColumna);

        return new DiaDiario(fechaBDtoFecha(fecha), Integer.parseInt(valoracionDia), resumen, contenido, foto, latitud, longitud);

    }

    public void cargaDatosPrueba() {

        DiaDiario dia=new DiaDiario(fechaBDtoFecha("2019-01-01"),4,"Examen Mates","Ha salido mal","0","0","0");
        anyadeActualizaDia(dia);
        dia=new DiaDiario(fechaBDtoFecha("2019-01-02"),3,"Mejor que ayer","Ha salido bien","0","0","0");
        anyadeActualizaDia(dia);
        dia=new DiaDiario(fechaBDtoFecha("2019-01-03"),1,"Animado","Estoy animado","0","0","0");
        anyadeActualizaDia(dia);

    }

    private class DBHelper extends SQLiteOpenHelper {


        public DBHelper(Context context) {

            super(context, DB_NOMBRE, null, DB_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(SQL_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL(SQL_DROP);
            db.execSQL(SQL_CREATE);

        }

    }
}
