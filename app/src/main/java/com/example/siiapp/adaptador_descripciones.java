package com.example.siiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class adaptador_descripciones extends RecyclerView.Adapter<adaptador_descripciones.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView os, descripcion, interferencia;

        itemClick click;

        public ViewHolder (View itemView) {
            super(itemView);

            os = (TextView)itemView.findViewById(R.id.modelo_os_descripciones);
            descripcion = (TextView)itemView.findViewById(R.id.modelo_descripcion_descripciones);
            interferencia = (TextView)itemView.findViewById(R.id.modelo_interferencia_descripciones);
        }

    }

    public List<modelo_descripciones_pd> lista;
    public Context c;

    public adaptador_descripciones(Context c, List<modelo_descripciones_pd> lista) {
        this.c = c;
        this.lista = lista;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_descripciones, parent, false);
        ViewHolder vistaHolder = new ViewHolder(vista);

        return vistaHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String interferencia;

        holder.os.setText(lista.get(position).getId());
        holder.descripcion.setText(lista.get(position).getDescripcion());

        if(lista.get(position).getInterferencia().equals("null")) {
            interferencia = "NO REGISTRA";

        } else {
            interferencia = lista.get(position).getInterferencia();

        }

        holder.interferencia.setText(interferencia);

    }

    @Override
    public int getItemCount() {
        return lista.size();

    }
}
