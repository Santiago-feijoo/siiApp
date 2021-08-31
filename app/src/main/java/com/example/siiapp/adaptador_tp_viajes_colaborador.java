package com.example.siiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class adaptador_tp_viajes_colaborador extends RecyclerView.Adapter<adaptador_tp_viajes_colaborador.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView permiso, placa, ruta, despacho, fecha;

        itemClick click;

        public ViewHolder (View itemView) {
            super(itemView);

            permiso = (TextView)itemView.findViewById(R.id.texto_permiso_viajes_colaborador);
            placa = (TextView)itemView.findViewById(R.id.texto_placa_viajes_colaborador);
            ruta = (TextView)itemView.findViewById(R.id.texto_rutas_viajes_colaborador);
            despacho = (TextView)itemView.findViewById(R.id.texto_despacho_viajes_colaborador);
            fecha = (TextView)itemView.findViewById(R.id.texto_fecha_viajes_colaborador);

        }

    }

    public List<modelo_viajes_colaborador> lista;
    public Context c;

    public adaptador_tp_viajes_colaborador(Context c, List<modelo_viajes_colaborador> lista) {
        this.c = c;
        this.lista = lista;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_viajes_colaborador, parent, false);
        ViewHolder vistaHolder = new ViewHolder(vista);

        return vistaHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.permiso.setText(lista.get(position).getPermiso());
        holder.placa.setText(lista.get(position).getPlaca());
        holder.ruta.setText(lista.get(position).getRuta());
        holder.despacho.setText(lista.get(position).getDespacho());
        holder.fecha.setText(lista.get(position).getFecha());

    }

    @Override
    public int getItemCount() {
        return lista.size();

    }
}
