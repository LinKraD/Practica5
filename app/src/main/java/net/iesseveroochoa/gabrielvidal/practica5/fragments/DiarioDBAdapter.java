package net.iesseveroochoa.gabrielvidal.practica5.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuView;
import androidx.constraintlayout.widget.ConstraintLayout;

import net.iesseveroochoa.gabrielvidal.practica5.R;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiaDiario;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiarioDB;

public class DiarioDBAdapter extends CursorAdapter {

    public DiarioDBAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_diario,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        DiaDiario dia= DiarioDB.cursorADiaDiario(cursor);

        TextView tvResumen=(TextView) view.findViewById(R.id.tv_Resumen);
        TextView tvFecha=(TextView) view.findViewById(R.id.tv_Fecha);

        ImageView ivImagenValor=(ImageView) view.findViewById(R.id.iv_ImagenValor);

        LinearLayout lytItem=(LinearLayout) view.findViewById(R.id.lyt_Item);

        tvResumen.setText(dia.getResumen());
        tvFecha.setText(dia.getFechaFormatoLocal());

        int valor=dia.getValoracionResumida();

        if (valor==1){
            ivImagenValor.setImageResource(R.drawable.sadp);
        }else if (valor==2){
            ivImagenValor.setImageResource(R.drawable.neutralp);
        } else if (valor==3){
            ivImagenValor.setImageResource(R.drawable.smilep);
        }

        if ((cursor.getPosition()%2)==0){
            lytItem.setBackgroundResource(R.color.colorFondoLista);
        } else{
            lytItem.setBackgroundColor(Color.TRANSPARENT);
        }
    }

}
