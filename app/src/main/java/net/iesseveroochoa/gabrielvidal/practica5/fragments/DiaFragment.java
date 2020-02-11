package net.iesseveroochoa.gabrielvidal.practica5.fragments;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import net.iesseveroochoa.gabrielvidal.practica5.R;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiaDiario;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiarioDB;

public class DiaFragment extends Fragment {
    private static final String ARG_DIA = "dia";

    private TextView tvFecha;
    private TextView tvResumen;
    private TextView tvValoracion;
    private TextView tvDescripcion;

    private DiaDiario dia;

    private static FrameLayout ltDia;

    public DiaFragment() {
    }

    public static DiaFragment newInstance(DiaDiario dia) {
        DiaFragment fragment = new DiaFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DIA, dia);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dia_fragment, container, false);

        ltDia = view.findViewById(R.id.lt_Dia);

        this.setRetainInstance(true);
        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvFecha = getView().findViewById(R.id.tv_Fecha);
        tvResumen = getView().findViewById(R.id.tv_Resumen);
        tvValoracion = getView().findViewById(R.id.tv_Valoracion);
        tvDescripcion = getView().findViewById(R.id.tv_Descripcion);

        if (getArguments() != null) {

            dia = getArguments().getParcelable(ARG_DIA);

        } else {

            dia = new DiaDiario(DiarioDB.fechaBDtoFecha(""), 0, "", "", "", "", "");
            return;

        }

        visualizaDia();

    }

    private void visualizaDia() {
        tvFecha.setText(dia.getFechaFormatoLocal());
        tvResumen.setText(dia.getResumen());
        tvValoracion.setText(dia.getValoracionDia()+"");
        tvDescripcion.setText(dia.getContenido());
    }

    public void setDia(DiaDiario dia) {
        this.dia = dia;
        visualizaDia();
    }

    public DiaDiario getDia(){
        return this.dia;
    }

    public void setColorFondo(String colorFondo){
        if (colorFondo.equals("chico")) {
            ltDia.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.chico));
        } else if (colorFondo.equals("chica")){
            ltDia.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.chica));
        }
    }
}
