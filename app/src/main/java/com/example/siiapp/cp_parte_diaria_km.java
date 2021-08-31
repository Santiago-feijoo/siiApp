package com.example.siiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class cp_parte_diaria_km extends AppCompatActivity {

    /// OBJETOS ///

    conexion sql = new conexion();
    loading carga = new loading(this);
    modelo_equipos_pd equipos = new modelo_equipos_pd();

    modelo_descripciones_pd descripciones = new modelo_descripciones_pd();

    /// ATRIBUTOS ///

    private RequestQueue mQueue;

    private EditText caja_km_inicial;
    private EditText caja_km_final;

    private String turnos[] = {"SELECCIONAR", "DIURNO", "NOCTURNO"};
    private Spinner caja_turno;

    private String horas[];
    private String minutos[];
    private Spinner caja_horas;
    private Spinner caja_horas_os;
    private Spinner caja_minutos_os;

    private EditText caja_os;
    private EditText caja_descripcion_os;

    private Button añadirD;

    private Button botonRegistrar;

    private String iTag[], viTag[];
    private Spinner caja_interferencia;

    LinearLayout vista_horas;

    private List<modelo_descripciones_pd> listaDescripciones = new ArrayList<>();

    private RecyclerView listaD;
    private adaptador_pd_descripciones adaptador;

    String urlInterferencias = sql.api + "consultaInterferenciasPdAPP/";
    String urlCierrePd = sql.api + "updateCierrePdAPP/";
    String urlRegistroDetalles = sql.api + "registrarDetallesPdAPP/";

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parte_diaria_km);

        mQueue = Volley.newRequestQueue(this);

        caja_km_inicial = (EditText)findViewById(R.id.caja_km_inicial_pd_km);
        caja_km_inicial.setText(equipos.getKm_inicial());

        caja_km_final = (EditText)findViewById(R.id.caja_km_final_pd_km);

        vista_horas = (LinearLayout)findViewById(R.id.vista_horas_pd_km);

        caja_turno = (Spinner)findViewById(R.id.caja_turno_pd_km);
        caja_horas = (Spinner)findViewById(R.id.caja_jornada_pd_km);
        caja_horas_os = (Spinner)findViewById(R.id.caja_horas_pd_km);
        caja_minutos_os = (Spinner)findViewById(R.id.caja_minutos_pd_km);

        adaptarTurno();
        adaptarHoras();

        caja_os = (EditText)findViewById(R.id.caja_os_pd_km);
        caja_descripcion_os = (EditText)findViewById(R.id.caja_desc_pd_km);

        caja_interferencia = (Spinner)findViewById(R.id.caja_interferencia_pd_km);

        caja_interferencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (viTag[caja_interferencia.getSelectedItemPosition()].equals("0607") || viTag[caja_interferencia.getSelectedItemPosition()].equals("0624") || viTag[caja_interferencia.getSelectedItemPosition()].equals("5410") || viTag[caja_interferencia.getSelectedItemPosition()].equals("5411")) {
                    vista_horas.setVisibility(View.VISIBLE);

                } else {
                    vista_horas.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        añadirD = (Button)findViewById(R.id.boton_anadir_pd_km);
        añadirD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                añadirD();

            }
        });

        listaD = (RecyclerView)findViewById(R.id.listaDescripciones_pd_km);
        listaD.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new adaptador_pd_descripciones(cp_parte_diaria_km.this, listaDescripciones);

        adaptador.setOnItemClick(new adaptador_pd_descripciones.OnItemClick() {
            @Override
            public void itemClick(int position) {
                listaDescripciones.remove(position);
                adaptarDescripciones();

            }
        });

        carga.iniciarCarga();
        getInterferencias();

        botonRegistrar = (Button)findViewById(R.id.boton_registrar_pd_km);
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(caja_km_final.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "DEBE DIGITAR EL KILOMETRAJE FINAL", Toast.LENGTH_SHORT).show();

                } else if (Integer.parseInt(equipos.getKm_inicial()) > Integer.parseInt(caja_km_final.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "EL KILOMETRAJE FINAL NO PUEDE SER MENOR QUE EL INICIAL", Toast.LENGTH_SHORT).show();

                } else if (caja_turno.getSelectedItem().toString().equals("SELECCIONAR")) {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR EL TURNO", Toast.LENGTH_SHORT).show();

                } else if (caja_horas.getSelectedItem().toString().equals("SELECCIONAR")) {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR LA CANTIDAD DE HORAS", Toast.LENGTH_SHORT).show();

                } else if (listaDescripciones.size() == 0) {
                    Toast.makeText(getApplicationContext(), "DEBE REGISTRAR ALMENOS UNA OS Y UNA DESCRIPCIÓN", Toast.LENGTH_SHORT).show();

                } else {
                    equipos.setKm_final(caja_km_final.getText().toString());
                    equipos.setTurno(caja_turno.getSelectedItem().toString());
                    equipos.setJornada(caja_horas.getSelectedItem().toString());

                    int cantidadHoras = 0, cantidadMinutos = 0;

                    for(int i = 0; i < listaDescripciones.size(); i++) {
                        cantidadHoras += Integer.parseInt(listaDescripciones.get(i).getHorasLaboradas());
                        cantidadMinutos += Integer.parseInt(listaDescripciones.get(i).getMinutosLaborados());

                    }

                    equipos.setHorasLaboradas(String.valueOf(cantidadHoras + cantidadMinutos / 60));

                    botonRegistrar.setEnabled(false);
                    cerrarPd();

                }

            }
        });

    }

    public  void adaptarTurno() {
        ArrayAdapter<String> adaptarTurno = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, turnos);
        caja_turno.setAdapter(adaptarTurno);

    }

    public void adaptarHoras() {
        horas = new String[14];
        minutos = new String[horas.length - 1];
        int anadirHoras = 0, anadirMinutos = 0;

        horas[0] = "SELECCIONAR";
        minutos[0] = "SELECCIONAR";

        for(int i = 1; i < horas.length; i++) {
            horas[i] = String.valueOf(anadirHoras);
            anadirHoras += 1;

        }

        for(int i = 1; i < minutos.length; i++) {
            minutos[i] = String.valueOf(anadirMinutos);
            anadirMinutos += 5;

        }

        ArrayAdapter<String> adaptarHoras = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, horas);
        caja_horas.setAdapter(adaptarHoras);

        ArrayAdapter<String> adaptarHorasOs = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, horas);
        caja_horas_os.setAdapter(adaptarHorasOs);

        ArrayAdapter<String> adaptarMinutosOs = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minutos);
        caja_minutos_os.setAdapter(adaptarMinutosOs);

    }

    public void añadirD() {
        if(caja_interferencia.getSelectedItem().toString().equals("SELECCIONAR")) {
            if(caja_os.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE LA OS", Toast.LENGTH_SHORT).show();

            } else if(caja_descripcion_os.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE LA DESCRIPCIÓN", Toast.LENGTH_SHORT).show();

            }  else {
                listaDescripciones.add(new modelo_descripciones_pd(caja_os.getText().toString(), caja_descripcion_os.getText().toString(), "null" , "0", "0"));
                adaptarDescripciones();

            }

        } else {
            int posicion = caja_interferencia.getSelectedItemPosition();

            if(caja_os.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE LA OS", Toast.LENGTH_SHORT).show();

            } else if(caja_descripcion_os.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE LA DESCRIPCIÓN", Toast.LENGTH_SHORT).show();

            } else {
                if(viTag[posicion].equals("0607") || viTag[posicion].equals("0624") || viTag[posicion].equals("5410") || viTag[posicion].equals("5411")) {
                    if(caja_horas_os.getSelectedItem().toString().equals("SELECCIONAR")) {
                        Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR LAS HORAS DE LA OS", Toast.LENGTH_SHORT).show();

                    } else if(caja_minutos_os.getSelectedItem().toString().equals("SELECCIONAR")) {
                        Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR LOS MINUTOS DE LA OS", Toast.LENGTH_SHORT).show();

                    } else if (caja_horas_os.getSelectedItem().toString().equals("0") && caja_minutos_os.getSelectedItem().toString().equals("0")) {
                        Toast.makeText(getApplicationContext(), "HORA Y MINUTOS INCORRECTOS.", Toast.LENGTH_SHORT).show();

                    } else {
                        listaDescripciones.add(new modelo_descripciones_pd(caja_os.getText().toString(), caja_descripcion_os.getText().toString(), viTag[posicion], caja_horas_os.getSelectedItem().toString(), caja_minutos_os.getSelectedItem().toString()));
                        adaptarDescripciones();

                    }

                } else {
                    listaDescripciones.add(new modelo_descripciones_pd(caja_os.getText().toString(), caja_descripcion_os.getText().toString(), viTag[posicion], "0", "0"));
                    adaptarDescripciones();

                }


            }

        }

    }

    public void adaptarDescripciones() {
        adaptador.notifyDataSetChanged();
        listaD.setAdapter(adaptador);

        limpiar();

    }

    public void limpiar() {
        caja_os.setText("");
        caja_descripcion_os.setText("");
        caja_interferencia.setSelection(0);
        caja_horas_os.setSelection(0);
        caja_minutos_os.setSelection(0);

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
            registrarDetalles(listaDescripciones.get(i).getId(), listaDescripciones.get(i).getDescripcion(), listaDescripciones.get(i).getInterferencia(), listaDescripciones.get(i).getHorasLaboradas(), listaDescripciones.get(i).getMinutosLaborados());

        }

        registrarCierre();

    }

    public void registrarDetalles(String os, String descripcion, String interferencia, String horas, String minutos) {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("consecutivo", equipos.getCon());
        hashMapToken.put("os", os);
        hashMapToken.put("descripcion", descripcion);
        hashMapToken.put("interferencia", interferencia);
        hashMapToken.put("horas", horas);
        hashMapToken.put("minutos", minutos);

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
        HashMap<String, String> hashMapToken = new HashMap<>();
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
        ArrayAdapter<String> adaptarInt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, iTag);
        caja_interferencia.setAdapter(adaptarInt);

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Menu.class);
        startActivity(intento);
        this.finish();

    }

}
