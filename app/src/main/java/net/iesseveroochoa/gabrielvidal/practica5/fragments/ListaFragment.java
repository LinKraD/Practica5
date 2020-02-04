package net.iesseveroochoa.gabrielvidal.practica5.fragments;


import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import net.iesseveroochoa.gabrielvidal.practica5.R;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiaDiario;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiarioContract;
import net.iesseveroochoa.gabrielvidal.practica5.modelo.DiarioDB;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListaFragment extends Fragment {

    private DiarioDBAdapter diarioAdapter;
    private DiarioDB diarioDB;
    private ListView lvDiario;
    private OnListaDiarioListener listaDiarioListener;
    private String orden;

    public interface OnListaDiarioListener{
        void onDiaSeleccionado(DiaDiario dia);
    }

    public void setListaDiarioListener(OnListaDiarioListener listaDiarioListener) {
        this.listaDiarioListener = listaDiarioListener;
    }

    public ListaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_lista, container, false);

    }

    public void borraDia(DiaDiario dia){
        diarioDB.borraDia(dia);
        leeAdaptador();
    }

    public void ordenar(String orden){
        Cursor c = diarioDB.obtenDiario(orden);
        diarioAdapter.changeCursor(c);
        diarioAdapter.notifyDataSetChanged();
    }

    public void resumenTotalVida(){
        diarioDB.valoraVida();
    }

    public void actualizaListaConDia(DiaDiario dia){
        diarioDB.anyadeActualizaDia(dia);
        leeAdaptador();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        diarioDB=new DiarioDB(getContext());
        diarioDB.open();

        lvDiario=getView().findViewById(R.id.lvListado);
        Cursor cursor = diarioDB.obtenDiario(orden);
        diarioAdapter = new DiarioDBAdapter(getContext(), cursor);
        lvDiario.setAdapter(diarioAdapter);

        lvDiario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listaDiarioListener != null) {
                    Cursor cur = (Cursor) adapterView.getItemAtPosition(i);
                    DiaDiario dia = DiarioDB.cursorADiaDiario(cur);
                    listaDiarioListener.onDiaSeleccionado(dia);
                }
            }
        });

        //diarioDB.close();
    }

    private void leeAdaptador(){
        Cursor cursor=diarioDB.obtenDiario(DiarioContract.DiaDiarioEntries.FECHA);
        diarioAdapter.changeCursor(cursor);
        diarioAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        this.setRetainInstance(true);
    }
}
