package com.example.siiapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menu extends AppCompatActivity {

    /// OBJETOS ///

    Conectar sql = new Conectar();
    Sesion sesion = new Sesion();
    cargar_proceso carga = new cargar_proceso(this);

    /// ATRIBUTOS ///

    private TextView nombreUser;
    private TextView cargoUser;

    private ImageView imgUser;

    private LinearLayout ventanaE;

    private ViewStub vistaApps;
    private GridView tablero;

    private AdaptadorApps adaptador;
    private List<Modelo_modulos> listaModelos;

    private TextView texto_cerrarSesion;
    private ImageView img_cerrarSesion;

    private int currentViewMode = 0;
    private RequestQueue mQueue;

    String urlModulos = sql.api + "modulosAPP/";
    String urlImgColaborador = sql.api + "imgColaboradores/";
    String link;

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        obtenerSesion();
        mQueue = Volley.newRequestQueue(this);

        nombreUser = (TextView)findViewById(R.id.info_nombre_menu);
        nombreUser.setText(sesion.getNombreUsuario());

        cargoUser = (TextView)findViewById(R.id.info_cargo_menu);
        cargoUser.setText(sesion.getCargo());

        imgUser = (ImageView)findViewById(R.id.img_user_menu);
        obtenerImg();

        ventanaE = (LinearLayout)findViewById(R.id.ventanaE);

        vistaApps = (ViewStub)findViewById(R.id.apps);
        vistaApps.inflate();

        tablero = (GridView)findViewById(R.id.tablero);
        tablero.setOnItemClickListener(itemClick);

        img_cerrarSesion = (ImageView)findViewById(R.id.img_cerrarSesion_menu);
        img_cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();

            }
        });

        texto_cerrarSesion = (TextView)findViewById(R.id.texto_cerrarSesion_menu);
        texto_cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();

            }
        });

        carga.iniciarCarga();
        consultarModulos();

    }

    public void obtenerImg() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("id", sesion.getCodigo());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlImgColaborador, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        String resultado = response.getString("datos");

                        byte[] decodedString = Base64.decode(resultado, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        imgUser.setImageBitmap(decodedByte);

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS!, NO SE PUDO CERRAR.", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!" +e, Toast.LENGTH_LONG).show();
                    cerrarVentana();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();
                cerrarVentana();

            }

        });

        mQueue.add(peticion);

    }

    public void consultarModulos() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("usuario", sesion.getId());
        hashMapToken.put("app", sesion.getApp());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlModulos, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        JSONArray jArray = response.getJSONArray("datos");

                        listaModelos = new ArrayList<>();

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject datos = jArray.getJSONObject(i);

                            String nombreImg = datos.getString("Icono");
                            int img = getResources().getIdentifier(nombreImg, "drawable", getPackageName());
                            listaModelos.add(new Modelo_modulos(datos.getString("Subgrupo"), img, datos.getString("Ruta")));

                        }

                        setAdaptador();

                    } else {
                        carga.cargaCompleta();
                        vistaApps.setVisibility(View.GONE);
                        ventanaE.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "NO TIENE PERMISOS EN ALGÚN MODULO", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR: " +e, Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();

            }

        });

        mQueue.add(peticion);

    }

    public void setAdaptador() {
        adaptador = new AdaptadorApps(this, R.layout.gridview_modelo, listaModelos);
        tablero.setAdapter(adaptador);

    }

    AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String ruta = listaModelos.get(position).getRuta();

            if (ruta.equals("null")) {
                Toast.makeText(getApplicationContext(), "MODULO NO DISPONIBLE.", Toast.LENGTH_SHORT).show();

            } else {
                link = "com.example.siiapp." + ruta;

                try {
                    abrirModulo();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }
    };

    public void abrirModulo() throws ClassNotFoundException {
        Intent intento = new Intent(Menu.this, Class.forName(link));
        startActivity(intento);

    }

    public void obtenerSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);
        sesion.setId(memoria.getString(sesion.getID_USUARIO(), null));
        sesion.setApp(memoria.getString(sesion.getID_APP(), null));
        sesion.setNomApp(memoria.getString(sesion.getNOM_APP(), null));
        sesion.setCodigo(memoria.getString(sesion.getCF(), null));
        sesion.setNombreUsuario(memoria.getString(sesion.getNOM_USER(), null));
        sesion.setCargo(memoria.getString(sesion.getCARGO_USER(), null));

        if (sesion.getId() == null || sesion.getNomApp() == null || sesion.getApp() == null || sesion.getCodigo() == null) {

        }

    }

    public void cerrarSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);

        SharedPreferences.Editor establecer = memoria.edit();
        establecer.putString(sesion.getID_USUARIO(), null);
        establecer.putString(sesion.getID_APP(), null);
        establecer.putString(sesion.getNOM_APP(), null);
        establecer.putString(sesion.getCF(), null);
        establecer.putString(sesion.getNOM_USER(), null);
        establecer.putString(sesion.getCARGO_USER(), null);

        establecer.commit();

        Toast.makeText(getApplicationContext(), "SESION FINALIZADA", Toast.LENGTH_LONG).show();
        cerrarVentana();

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Home.class);
        startActivity(intento);
        this.finish();

    }

}
