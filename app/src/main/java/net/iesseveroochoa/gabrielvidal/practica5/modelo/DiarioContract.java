package net.iesseveroochoa.gabrielvidal.practica5.modelo;

import android.provider.BaseColumns;

public class DiarioContract {
    public static abstract class DiaDiarioEntries{
        public static final String TABLE_NAME="diadiario";
        public static final String _ID= BaseColumns._ID;

        public static final String FECHA="fecha";
        public static final String VALORACIONDIA="valoracionDia";
        public static final String RESUMEN="resumen";
        public static final String CONTENIDO="contenido";
        public static final String FOTO_URI="fotoUri";
        public static final String LATITUD="latitud";
        public static final String LONGITUD="longitud";
    }
}
