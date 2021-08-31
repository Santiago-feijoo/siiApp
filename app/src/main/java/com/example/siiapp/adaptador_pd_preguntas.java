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

public class adaptador_pd_preguntas extends RecyclerView.Adapter<adaptador_pd_preguntas.ViewHolder> {

    OnItemClick mAccion;

    public interface OnItemClick {
        void itemClick(int position);

    }

    public void setOnItemClick(OnItemClick accion) {
        mAccion = accion;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView id, pregunta, respuesta;
        private ImageView imgEditar;

        public ViewHolder (View itemView, OnItemClick accion) {
            super(itemView);

            id = (TextView)itemView.findViewById(R.id.texto_os_descripciones);
            pregunta = (TextView)itemView.findViewById(R.id.modelo_os_descripciones);
            respuesta = (TextView)itemView.findViewById(R.id.modelo_respuesta_respuesta);
            imgEditar = (ImageView)itemView.findViewById(R.id.img_editar_respuesta_pd);

            imgEditar.setOnClickListener(new View.OnClickListener() {
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

    public List<modelo_preguntas> lista;
    public Context c;

    public adaptador_pd_preguntas(Context c, List<modelo_preguntas> lista) {
        this.c = c;
        this.lista = lista;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_respuestas, parent, false);
        ViewHolder vistaHolder = new ViewHolder(vista, mAccion);

        return vistaHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String calificacion = "";

        holder.id.setText(lista.get(position).getId() + ".");
        holder.pregunta.setText(lista.get(position).getPregunta());

        if(lista.get(position).getRespuesta().trim().equals("2")) {
            calificacion = "SI";

        } else if(lista.get(position).getRespuesta().trim().equals("1")) {
            calificacion = "NO";

        } else if(lista.get(position).getRespuesta().trim().equals("0")) {
            calificacion = "NO APLICA";

        } else {
            calificacion = "SIN RESPONDER";

        }

        holder.respuesta.setText(calificacion);

    }

    @Override
    public int getItemCount() {
        return lista.size();

    }

}
