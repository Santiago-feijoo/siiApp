package com.example.siiapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

public class Parte_diaria_dos extends AppCompatActivity {

    /// OBJETOS ///

    Conectar sql = new Conectar();
    Sesion sesion = new Sesion();
    cargar_proceso carga = new cargar_proceso(this);
    modelo_equipos_pd equipos = new modelo_equipos_pd();

    modelo_descripciones_pd descripciones = new modelo_descripciones_pd();

    /// ATRIBUTOS ///

    private RequestQueue mQueue;

    private EditText caja_km_inicial;
    private EditText caja_km_final;

    private String turnos[] = {"SELECCIONAR", "DIURNO", "NOCTURNO"};
    private Spinner caja_turno;

    private String horas[];
    private Spinner caja_horas;

    private EditText caja_os;
    private EditText caja_descripcion_os;

    private ImageView añadirD;

    private Button botonRegistrar;

    private String iTag[], viTag[];
    private Spinner caja_interferencia;

    private List<modelo_descripciones_pd> listaDescripciones = new ArrayList<>();

    private RecyclerView listaD;
    private adaptador_descripciones adaptador;

    String urlInterferencias = sql.api + "consultaInterferenciasPdAPP/";
    String urlCierrePd = sql.api + "updateCierrePdAPP/";
    String urlRegistroDetalles = sql.api + "registrarDetallesPdAPP/";

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parte_diaria_dos);

        mQueue = Volley.newRequestQueue(this);

        caja_km_inicial = (EditText)findViewById(R.id.caja_km_inicial_pd_dos);
        caja_km_inicial.setText(equipos.getKm_inicial());

        caja_km_final = (EditText)findViewById(R.id.caja_km_final_pd_dos);

        caja_turno = (Spinner)findViewById(R.id.caja_turno_pd_dos);
        caja_horas = (Spinner)findViewById(R.id.caja_jornada_pd_dos);

        adaptarTurno();
        adaptarHoras();

        caja_os = (EditText)findViewById(R.id.caja_os_pd_dos);
        caja_descripcion_os = (EditText)findViewById(R.id.caja_desc_pd_dos);

        caja_interferencia = (Spinner)findViewById(R.id.caja_interferencia_pd_dos);

        añadirD = (ImageView)findViewById(R.id.img_añadir_pd_dos);
        añadirD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                añadirD();

            }
        });

        listaD = (RecyclerView)findViewById(R.id.listaDescripciones);
        listaD.setLayoutManager(new LinearLayoutManager(this));

        carga.iniciarCarga();
        getInterferencias();

        botonRegistrar = (Button)findViewById(R.id.boton_registrar_pd_dos);
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(caja_km_final.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "DEBE DIGITAR EL KILOMETRAJE FINAL", Toast.LENGTH_SHORT).show();

                } else if (caja_turno.getSelectedItem().toString().equals("SELECCIONAR")) {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR EL TURNO", Toast.LENGTH_SHORT).show();

                } else if (caja_horas.getSelectedItem().toString().equals("SELECCIONAR")) {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR LA CANTIDAD DE HORAS", Toast.LENGTH_SHORT).show();

                } else if (listaDescripciones.size() == 0) {
                    Toast.makeText(getApplicationContext(), "DEBE REGISTRAR ALMENOS UNA OS Y UNA DESCRIPCIÓN", Toast.LENGTH_SHORT).show();

                } else {
                    equipos.setKm_final(caja_km_final.getText().toString());

                   if (Integer.parseInt(equipos.getKm_inicial()) > Integer.parseInt(equipos.getKm_final())) {
                       Toast.makeText(getApplicationContext(), "EL KILOMETRAJE FINAL NO PUEDE SER MENOR QUE EL INICIAL", Toast.LENGTH_SHORT).show();

                   } else {
                       botonRegistrar.setEnabled(false);
                       equipos.setTurno(caja_turno.getSelectedItem().toString());
                       equipos.setJornada(caja_horas.getSelectedItem().toString());

                       cerrarPd();

                   }

                }

            }
        });

    }

    public  void adaptarTurno() {
        ArrayAdapter<String> adaptarTurno = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, turnos);
        caja_turno.setAdapter(adaptarTurno);

    }

    public void adaptarHoras() {
        horas = new String[13];
        horas[0] = "SELECCIONAR";

        for(int i = 1; i < horas.length; i++) {
            horas[i] = String.valueOf(i);

        }

        ArrayAdapter<String> adaptarHoras = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, horas);
        caja_horas.setAdapter(adaptarHoras);

    }

    public void añadirD() {
        if(caja_interferencia.getSelectedItem().toString().equals("SELECCIONAR")) {
            if(caja_os.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE LA OS", Toast.LENGTH_SHORT).show();

            } else if(caja_descripcion_os.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE LA DESCRIPCIÓN", Toast.LENGTH_SHORT).show();

            } else {
                listaDescripciones.add(new modelo_descripciones_pd(caja_os.getText().toString(), caja_descripcion_os.getText().toString(), "null"));
                adaptarDescripciones();

            }

        } else {
            int posicion = caja_interferencia.getSelectedItemPosition();

            if(caja_os.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE LA OS", Toast.LENGTH_SHORT).show();

            } else if(caja_descripcion_os.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE LA DESCRIPCIÓN", Toast.LENGTH_SHORT).show();

            } else {
                listaDescripciones.add(new modelo_descripciones_pd(caja_os.getText().toString(), caja_descripcion_os.getText().toString(), viTag[posicion]));
                adaptarDescripciones();

            }

        }

    }

    public void adaptarDescripciones() {
        adaptador = new adaptador_descripciones(Parte_diaria_dos.this, listaDescripciones);
        listaD.setAdapter(adaptador);

        limpiar();

    }

    public void limpiar() {
        caja_os.setText("");
        caja_descripcion_os.setText("");
        caja_interferencia.setSelection(0);

    }

    public void getInterferencias() {
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.GET, urlInterferencias, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    carga.cargaCompleta();
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        JSONArray jArray = response.getJSONArray("datos");
                        iTag = new String[jArray.length()];
                        viTag = new String[jArray.length()];

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject datos = jArray.getJSONObject(i);

                            viTag[i] = datos.getString("Id_interferencia");
                            iTag[i] = datos.getString("Descripcion");

                        }

                        adaptarInterferencias();

                    } else {
                        Toast.makeText(getApplicationContext(), "NO HAY INTERFERENCIAS REGISTRADAS!", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException error) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_LONG).show();

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

    public void cerrarPd() {
        carga.iniciarCarga();

        for(int i = 0; i < listaDescripciones.size(); i++) {
            registrarDetalles(listaDescripciones.get(i).getId(), listaDescripciones.get(i).getDescripcion(), listaDescripciones.get(i).getInterferencia());

        }

        registrarCierre();

    }

    public void registrarDetalles(String os, String descripcion, String interferencia) {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("consecutivo", equipos.getCon());
        hashMapToken.put("os", os);
        hashMapToken.put("descripcion", descripcion);
        hashMapToken.put("interferencia", interferencia);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlRegistroDetalles, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {

                    } else {
                        Toast.makeText(getApplicationContext(), "¡UPS, NO SE PUDO REGISTRAR!", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!", Toast.LENGTH_LONG).show();

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

    public void registrarCierre() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("consecutivo", equipos.getCon());
        hashMapToken.put("turno", equipos.getTurno());
        hashMapToken.put("jornada", equipos.getJornada());
        hashMapToken.put("km", equipos.getKm_final());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlCierrePd, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        Toast.makeText(getApplicationContext(), "PARTE DIARIA CERRADA CON EXITO", Toast.LENGTH_SHORT).show();
                        cerrarVentana();

                    } else {
                        Toast.makeText(getApplicationContext(), "¡UPS, NO SE PUDO CERRAR!", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!", Toast.LENGTH_LONG).show();

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

    public void adaptarInterferencias() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ArrayAdapter<String> adaptarInt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, iTag);
        caja_interferencia.setAdapter(adaptarInt);

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Menu.class);
        startActivity(intento);
        this.finish();

    }

}
