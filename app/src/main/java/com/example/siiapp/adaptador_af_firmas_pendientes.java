package com.example.siiapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class adaptador_af_firmas_pendientes extends RecyclerView.Adapter<adaptador_af_firmas_pendientes.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nombreColaborador, codigo, consecutivo, almacen;
        private ImageView imagenCola;

        itemClickListener click;

        public ViewHolder (View itemView) {
            super(itemView);

            nombreColaborador = (TextView)itemView.findViewById(R.id.texto_nombre_af_firmapp);
            codigo = (TextView)itemView.findViewById(R.id.texto_codigo_af_firmapp);
            consecutivo = (TextView)itemView.findViewById(R.id.texto_consecutivo_af_firmapp);
            almacen = (TextView)itemView.findViewById(R.id.texto_almacen_af_firmapp);
            imagenCola = (ImageView)itemView.findViewById(R.id.img_colaborador_af_firmapp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.click.onItemClickListener(v, getLayoutPosition());

        }

        public void setClick(itemClickListener ic) {
            this.click = ic;

        }

    }

    public List<modelo_af_firmapp_pendientes> pendienteLista;
    public Context c;

    public adaptador_af_firmas_pendientes(Context c, List<modelo_af_firmapp_pendientes> pendienteLista) {
        this.c = c;
        this.pendienteLista = pendienteLista;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_af_firmapp_firmantes, parent, false);
        ViewHolder vistaHolder = new ViewHolder(vista);

        return vistaHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.nombreColaborador.setText(pendienteLista.get(position).getNombreColaborador());
        holder.codigo.setText(pendienteLista.get(position).getCodigo());
        holder.consecutivo.setText(pendienteLista.get(position).getConsecutivo());
        holder.imagenCola.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.persona));
        holder.almacen.setText(pendienteLista.get(position).getAlmacen());

        holder.setClick(new itemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                String codigo, consecutivo, nombreColaborador, nombreAlmacen;

                codigo = pendienteLista.get(position).getCodigo();
                consecutivo = pendienteLista.get(position).getConsecutivo();
                nombreColaborador = pendienteLista.get(position).getNombreColaborador();
                nombreAlmacen = pendienteLista.get(position).getAlmacen();

                Intent intento = new Intent(c, af_firmapp_firma.class);
                intento.putExtra("cf", codigo.substring(8));
                intento.putExtra("conse", consecutivo.substring(13));
                intento.putExtra("nombreC", nombreColaborador);
                intento.putExtra("nombreAlmacen", nombreAlmacen.substring(3));
                c.startActivity(intento);
                ((Home_af_firmapp)c).finish();

            }
        });

    }

    @Override
    public int getItemCount() {
        return pendienteLista.size();

    }
}
