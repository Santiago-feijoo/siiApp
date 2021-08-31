package com.example.siiapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

public class Home extends AppCompatActivity {

    /// OBJETOS ///

    Red conexion = new Red(this);
    com.example.siiapp.conexion sql = new conexion();
    modelo_sesion sesion = new modelo_sesion();


    /// ATRIBUTOS ///

    TextView codigObligatorio, claveObligatorio, appObligatorio, textoOnline, texto1_offline, texto2_offline;
    ImageView img_offline;
    EditText nombreUsuario, claveUsuario;
    Button botonIngresar;
    Spinner listaAplicativos;
    private RequestQueue mQueue;

    String urlApps = sql.api + "appsmovilesAPP/";
    String urlUsuarios = sql.api + "usuariosappAPP/";
    String codigo, clave, nombreApp;

    ArrayList<String> listAplicativos;

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (conexion.red().equals(false)) {
            setContentView(R.layout.offline);

            img_offline = (ImageView)findViewById(R.id.img_offline);
            texto1_offline = (TextView)findViewById(R.id.texto1_offline);
            texto2_offline = (TextView)findViewById(R.id.texto2_offline);

        } else {
            setContentView(R.layout.home);
            mQueue = Volley.newRequestQueue(this);

            nombreUsuario = (EditText) findViewById(R.id.nombreUsuario);
            claveUsuario = (EditText) findViewById(R.id.claveUsuario);

            codigObligatorio = (TextView) findViewById(R.id.codigObligatorio);
            claveObligatorio = (TextView) findViewById(R.id.claveObligatorio);
            appObligatorio = (TextView) findViewById(R.id.appObligatorio);

            /// PERMISOS ///

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1000);

            }

            /// TEXTO ANIMADO ///

            textoOnline = (TextView) findViewById(R.id.textoOnline);
            textoOnline.setText("TI - 2021");

            /// DECLARAMOS SPINNER Y AGREGAMOS SUS ITEMS ///

            listaAplicativos = (Spinner) findViewById(R.id.listaAplicativos);

            obtenerLista();
            ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listAplicativos);
            listaAplicativos.setAdapter(adaptador);

            /// EXTRAER MEMORIA DE SESION ///
            try {
                obtenerSesion();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();

            }

            /// DECLARAMOS EL BOTON Y SU EVENTO ///
            botonIngresar = (Button) findViewById(R.id.botonIngresar);

            botonIngresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    codigo = nombreUsuario.getText().toString();
                    clave = claveUsuario.getText().toString();
                    nombreApp = listaAplicativos.getSelectedItem().toString();

                    if (codigo.isEmpty()) {
                        codigObligatorio.setVisibility(View.VISIBLE);

                    } else if (clave.isEmpty()) {
                        claveObligatorio.setVisibility(View.VISIBLE);

                    } else if (nombreApp.equals("SELECCIONA UN APP")) {
                        appObligatorio.setVisibility(View.VISIBLE);

                    } else if (!codigo.isEmpty() && !clave.isEmpty() && !nombreApp.equals("SELECCIONA UN APP")) {
                        iniciarSesion();

                    }

                }

            });

        }

    }

    public void iniciarSesion() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("codigo", codigo);
        hashMapToken.put("clave", clave);
        hashMapToken.put("app", nombreApp);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlUsuarios, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {

                        JSONObject colaborador = response.getJSONObject("datos");

                        sesion.setApp(colaborador.getString("Id_aplicativo"));
                        sesion.setNomApp(colaborador.getString("Ruta_Aplicativo"));
                        sesion.setId(colaborador.getString("Id_usuario"));
                        sesion.setCodigo(colaborador.getString("FuncR"));
                        sesion.setNombreUsuario(colaborador.getString("Nom_usuario").trim());
                        sesion.setCargo(colaborador.getString("Cargo_colaborador"));
                        sesion.setRol(colaborador.getString("Rol"));
                        sesion.setClave(colaborador.getString("Cclave"));

                        if (sesion.getClave().equals("0")) {
                            abrirCambiarClave();

                        } else {
                            abrirApp();

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "CODIGO Y/O CLAVE INCORRECTA!", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException | ClassNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                Toast.makeText(getApplicationContext(), "ERROR DE RESPUESTA: " +error, Toast.LENGTH_LONG).show();

            }

        });

        mQueue.add(peticion);

    }

    public void obtenerSesion() throws ClassNotFoundException {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);
        sesion.setId(memoria.getString(sesion.getID_USUARIO(), null));
        sesion.setApp(memoria.getString(sesion.getID_APP(), null));
        sesion.setNomApp(memoria.getString(sesion.getNOM_APP(), null));
        sesion.setCodigo(memoria.getString(sesion.getCF(), null));
        sesion.setNombreUsuario(memoria.getString(sesion.getNOM_USER(), null));
        sesion.setCargo(memoria.getString(sesion.getCARGO_USER(), null));
        sesion.setRol(memoria.getString(sesion.getROL(), null));

        if (sesion.getId() == null || sesion.getNomApp() == null || sesion.getApp() == null || sesion.getCodigo() == null) {

        } else {
            abrirApp();

        }

    }

    public void guardarSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);

        SharedPreferences.Editor establecer = memoria.edit();
        establecer.putString(sesion.getID_USUARIO(), sesion.getId());
        establecer.putString(sesion.getID_APP(), sesion.getApp());
        establecer.putString(sesion.getNOM_APP(), sesion.getNomApp());
        establecer.putString(sesion.getCF(), sesion.getCodigo());
        establecer.putString(sesion.getNOM_USER(), sesion.getNombreUsuario());
        establecer.putString(sesion.getCARGO_USER(), sesion.getCargo());
        establecer.putString(sesion.getROL(), sesion.getRol());

        establecer.commit();

    }

    public void obtenerLista() {
        listAplicativos = new ArrayList<String>();
        listAplicativos.add("SELECCIONA UN APP");

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.GET, urlApps, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jArray = response.getJSONArray("datos");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject datos = jArray.getJSONObject(i);

                        listAplicativos.add(datos.getString("Aplicativo"));

                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "NO HAY APLICATIVOS!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();

            }

        });

        mQueue.add(peticion);

    }

    public void abrirCambiarClave() {
        Intent intento = new Intent(this, cambiar_clave.class);
        intento.putExtra("usuario", sesion.getId());
        intento.putExtra("idApp", sesion.getApp());
        startActivity(intento);

    }

    public void abrirApp() throws ClassNotFoundException {
        Toast.makeText(getApplicationContext(), "¡BIENVENIDO!", Toast.LENGTH_SHORT).show();
        guardarSesion();

        String link = "com.example.siiapp."+sesion.getNomApp();

        Intent intento = new Intent(Home.this, Class.forName(link));
        startActivity(intento);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    }

