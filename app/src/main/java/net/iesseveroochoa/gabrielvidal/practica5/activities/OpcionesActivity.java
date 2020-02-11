package net.iesseveroochoa.gabrielvidal.practica5.activities;

import android.content.Intent;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.view.MenuItem;

import net.iesseveroochoa.gabrielvidal.practica5.R;


public class OpcionesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }

}