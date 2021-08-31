package com.example.siiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class adaptador_pd_descripciones extends RecyclerView.Adapter<adaptador_pd_descripciones.ViewHolder> {

    OnItemClick mAccion;

    public interface OnItemClick {
        void itemClick(int position);

    }

    public void setOnItemClick(OnItemClick accion) {
        mAccion = accion;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView os, descripcion, interferencia, horasLaboradas;
        private ImageView delete;

        public ViewHolder (View itemView, OnItemClick accion) {
            super(itemView);

            os = (TextView)itemView.findViewById(R.id.modelo_os_descripciones);
            descripcion = (TextView)itemView.findViewById(R.id.modelo_descripcion_descripciones);
            interferencia = (TextView)itemView.findViewById(R.id.modelo_interferencia_descripciones);
            horasLaboradas = (TextView)itemView.findViewById(R.id.modelo_horas_descripciones);
            delete = (ImageView)itemView.findViewById(R.id.img_delete_descripciones_pd);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (accion != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            accion.itemClick(position);

                        }

                    }

                }
            });

        }

    }

    public List<modelo_descripciones_pd> lista;
    public Context c;

    public adaptador_pd_descripciones(Context c, List<modelo_descripciones_pd> lista) {
        this.c = c;
        this.lista = lista;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_descripciones, parent, false);
        ViewHolder vistaHolder = new ViewHolder(vista, mAccion);

        return vistaHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String interferencia, tiempo;

        holder.os.setText(lista.get(position).getId());
        holder.descripcion.setText(lista.get(position).getDescripcion());

        if (lista.get(position).getInterferencia().equals("null")) {
            interferencia = "NO REGISTRA";

        } else {
            interferencia = lista.get(position).getInterferencia();

        }

        holder.interferencia.setText(interferencia);

        if(lista.get(position).getHorasLaboradas().equals("0") && lista.get(position).getMinutosLaborados().equals("0")) {
            tiempo = "NO REGISTRA";

        } else if(lista.get(position).getHorasLaboradas().equals("0")) {
            if(lista.get(position).getMinutosLaborados().equals("1")) {
                tiempo = lista.get(position).getMinutosLaborados() + " minuto";

            } else {
                tiempo = lista.get(position).getMinutosLaborados() + " minutos";

            }

        } else if(lista.get(position).getMinutosLaborados().equals("0")) {
            if(lista.get(position).getHorasLaboradas().equals("1")) {
                tiempo = lista.get(position).getHorasLaboradas() + " hora";

            } else {
                tiempo = lista.get(position).getHorasLaboradas() + " horas";

            }

        } else {
            if(lista.get(position).getHorasLaboradas().equals("1")) {
                tiempo = lista.get(position).getHorasLaboradas() + " hora y ";

            } else {
                tiempo = lista.get(position).getHorasLaboradas() + " horas y ";

            }

            if(lista.get(position).getMinutosLaborados().equals("1")) {
                tiempo += lista.get(position).getMinutosLaborados() + " minuto";

            } else {
                tiempo += lista.get(position).getMinutosLaborados() + " minutos";

            }

        }

        holder.horasLaboradas.setText(tiempo);

    }

    @Override
    public int getItemCount() {
        return lista.size();

    }
}
