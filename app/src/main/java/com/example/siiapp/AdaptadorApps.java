package com.example.siiapp;

import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptadorApps extends ArrayAdapter<Modelo_modulos> {

    public AdaptadorApps(Context c, int recurso, List<Modelo_modulos> objetos) {
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

        Modelo_modulos modelo = getItem(position);

        TextView nombreApp = (TextView)v.findViewById(R.id.modelo_texto_nombreApp);
        ImageView iconApp = (ImageView)v.findViewById(R.id.modelo_img_iconApp);

        nombreApp.setText(modelo.getNombreApp());
        iconApp.setImageResource(modelo.getImg_app());

        return v;

    }

}
