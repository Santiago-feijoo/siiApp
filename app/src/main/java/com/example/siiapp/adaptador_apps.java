package com.example.siiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class adaptador_apps extends ArrayAdapter<modelo_modulos> {

    public adaptador_apps(Context c, int recurso, List<modelo_modulos> objetos) {
        super(c, recurso, objetos);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if(null == v) {
            LayoutInflater inflar = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflar.inflate(R.layout.gridview_modelo, null);

        }

        modelo_modulos modelo = getItem(position);

        TextView nombreApp = (TextView)v.findViewById(R.id.modelo_texto_nombreApp);
        ImageView iconApp = (ImageView)v.findViewById(R.id.modelo_img_iconApp);

        nombreApp.setText(modelo.getNombreApp());
        iconApp.setImageResource(modelo.getImg_app());

        return v;

    }

}
