package com.example.siiapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import static java.lang.Integer.parseInt;

public class Home_Parte_Diaria extends AppCompatActivity {

    /// OBJETOS ///

    Conectar sql = new Conectar();
    Sesion sesion = new Sesion();
    cargar_proceso carga = new cargar_proceso(this);

    modelo_equipos_pd equipos = new modelo_equipos_pd();

    /// ATRIBUTOS ///

    private RequestQueue mQueue;

    private TextView caja_codigo_pd_uno;
    private TextView caja_placa_pd_uno;
    private TextView caja_km_pd_uno;
    private TextView caja_descripcion_pd_uno;

    private TextView texto_num_pd_uno;

    private EditText caja_codigo_pd_alerta;

    private View vistaCodigo;
    private View vistaRespuestas;
    private View vistaObservacion;
    private View vistaTipo;

    private Spinner listaTipo;

    private EditText caja_observacion_pd;
    private TextView caja_observacion_pd_uno;

    private ConstraintLayout vistaObservacionI;
    private ConstraintLayout areaBoton_pd;

    private RadioGroup grupoRespuestas;
    private RadioButton respuestaSi;
    private RadioButton respuestaNo;
    private RadioButton respuestaNa;

    private Button boton_salir_pd_uno;
    private Button boton_continuar_pd_uno;

    List<modelo_preguntas> listaP = new ArrayList<>();

    private RecyclerView listadoQuiz;
    private adaptador_preguntas adaptador;
    private String tipos[] = {"SELECCIONAR", "KILOMETRAJE", "HOROMETRO"};

    String urlEquipos = sql.api + "consultarEquiposPdAPP/";
    String urlConsecutivo = sql.api + "consultaConsecutivosAPP/";
    String urlPdActiva = sql.api + "consultarPdActivaAPP/";
    String urlRegistrarConsecutivo = sql.api + "updateConsecutivoAPP/";
    String urlRegistrarPd = sql.api + "registrarMovimientosPdAPP/";
    String urlPreguntas = sql.api + "consultaPreguntasPdAPP/";
    String urlRegistrarRespuestas = sql.api + "registrarRespuestasPdAPP/";
    String urlRespuestas = sql.api + "consultaRespuestasPdAPP/";
    String urlActualizarRespuesta = sql.api + "updateRespuestaPdAPP/";

    int cantidadP = 0, posicion = 0;
    String codigoBuscar, observacion = "";

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_parte_diaria);

        obtenerSesion();
        mQueue = Volley.newRequestQueue(this);

        if(sesion.getRol().equals("OTRO")) {
            codigoBuscar = sesion.getId();

        } else {
            codigoBuscar = getIntent().getStringExtra("buscar");

        }

        caja_codigo_pd_uno = (TextView)findViewById(R.id.caja_codigo_pd_uno);
        caja_placa_pd_uno = (TextView)findViewById(R.id.caja_placa_pd_uno);
        caja_km_pd_uno = (TextView)findViewById(R.id.caja_km_pd_uno);
        caja_descripcion_pd_uno = (TextView)findViewById(R.id.caja_descripcion_pd_uno);

        texto_num_pd_uno = (TextView)findViewById(R.id.texto_num_pd_uno);

        listadoQuiz = (RecyclerView)findViewById(R.id.listadoPyR);
        listadoQuiz.setLayoutManager(new LinearLayoutManager(this));

        caja_observacion_pd_uno = (TextView)findViewById(R.id.caja_observacion_pd_uno);

        vistaObservacionI = (ConstraintLayout)findViewById(R.id.vistaObservacionI);
        areaBoton_pd = (ConstraintLayout)findViewById(R.id.area_boton_pd_uno);

        boton_salir_pd_uno = (Button)findViewById(R.id.boton_salir_pd_uno);
        boton_salir_pd_uno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

            }
        });

        boton_continuar_pd_uno = (Button)findViewById(R.id.boton_continuar_pd_uno);
        boton_continuar_pd_uno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirPd();

            }
        });

        carga.iniciarCarga();
        consultarPdActiva();

    }

    public void consultarPdActiva() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("usuario", codigoBuscar);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlPdActiva, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        JSONObject datos = response.getJSONObject("datos");

                        equipos.setCon(datos.getString("Consecutivo"));
                        equipos.setCodigo(datos.getString("Codigo_equipo"));
                        equipos.setPlaca(datos.getString("Placa"));
                        equipos.setTipo(datos.getString("Tipo"));
                        equipos.setDescripcion(datos.getString("Descripcion_equipo"));
                        equipos.setKm_inicial(datos.getString("Primer_km"));
                        observacion = datos.getString("Observacion_boletin");
                        equipos.setEstado(datos.getString("Estado"));

                        if(equipos.getPlaca().equals("null") || equipos.getPlaca().isEmpty()) {
                            equipos.setPlaca("NO APLICA");

                        }

                        carga.iniciarCarga();
                        getRespuestas();

                    } else {
                        if(sesion.getRol().equals("OTRO")) {
                            setCodigo();

                        } else {
                            Toast.makeText(getApplicationContext(), "LA PARTE DIARIA NO EXISTE", Toast.LENGTH_SHORT).show();
                            cerrar();

                        }

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!", Toast.LENGTH_LONG).show();
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

    public void setCodigo() {
        AlertDialog.Builder set = new AlertDialog.Builder(this);
        set.setTitle("DIGITA EL CODIGO DEL EQUIPO");
        vistaCodigo = getLayoutInflater().inflate(R.layout.alerta_codigo_equipo_pd, null);
        caja_codigo_pd_alerta = (EditText)vistaCodigo.findViewById(R.id.caja_codigo_equipo_alerta);
        set.setView(vistaCodigo);
        set.setCancelable(false);

        set.setPositiveButton("REGISTRAR EQUIPO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                equipos.setCodigo(caja_codigo_pd_alerta.getText().toString());

                if(equipos.getCodigo().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE CODIGO DEL EQUIPO", Toast.LENGTH_SHORT).show();
                    setCodigo();

                } else {
                    carga.iniciarCarga();
                    consultarEquipos();

                }

            }
        });

        set.setNegativeButton("SALIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                cerrarVentana();

            }
        });

        set.show();

    }

    public void consultarEquipos() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("idEquipo", equipos.getCodigo());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlEquipos, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        JSONObject datos = response.getJSONObject("datos");

                        equipos.setDescripcion(datos.getString("Descripcion_equipo"));
                        equipos.setPlaca(datos.getString("Placa"));
                        equipos.setTipo(datos.getString("Tipo"));
                        equipos.setKm_inicial(datos.getString("Ultimo_km"));
                        equipos.setEstado("1");

                        if(equipos.getPlaca().equals("null")) {
                            equipos.setPlaca("NO APLICA");

                        }

                        if(equipos.getKm_inicial().equals("null")) {
                            equipos.setKm_inicial("0");

                        }

                        carga.iniciarCarga();
                        consultarConsecutivo();

                    } else {
                        Toast.makeText(getApplicationContext(), "ESTE EQUIPO NO EXISTE!", Toast.LENGTH_SHORT).show();
                        setCodigo();

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!", Toast.LENGTH_LONG).show();
                    setCodigo();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();
                setCodigo();

            }

        });

        mQueue.add(peticion);

    }

    public void consultarConsecutivo() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("idApp", sesion.getApp());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlConsecutivo, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        JSONObject datos = response.getJSONObject("datos");

                        equipos.setCon(String.valueOf(Integer.parseInt(datos.getString("Conse")) + 1));
                        carga.iniciarCarga();
                        getPregunta();

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS, NO HAY CONSECUTIVOS!", Toast.LENGTH_SHORT).show();
                        cerrarVentana();

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!", Toast.LENGTH_LONG).show();
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

    public void getPregunta() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("idApp", sesion.getApp());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlPreguntas, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    carga.cargaCompleta();
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        JSONArray jArray = response.getJSONArray("datos");
                        cantidadP = jArray.length() - 1;

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject datos = jArray.getJSONObject(i);

                            listaP.add(new modelo_preguntas(datos.getString("Id_formato"), datos.getString("Consecutivo_pregunta"), datos.getString("Descripcion_pregunta"), ""));

                        }

                        carga.iniciarCarga();
                        setPregunta();

                    } else {
                        Toast.makeText(getApplicationContext(), "NO HAY PREGUNTAS REGISTRADAS!", Toast.LENGTH_LONG).show();

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

    public void setPregunta() {
        vistaRespuestas = getLayoutInflater().inflate(R.layout.modelo_respuestas_pd, null);
        grupoRespuestas = (RadioGroup) vistaRespuestas.findViewById(R.id.modelo_respuestas);
        respuestaSi = (RadioButton) vistaRespuestas.findViewById(R.id.radio_si);
        respuestaNo = (RadioButton) vistaRespuestas.findViewById(R.id.radio_no);
        respuestaNa = (RadioButton) vistaRespuestas.findViewById(R.id.radio_na);

        if(listaP.get(posicion).getRespuesta().equals("2")) {
            respuestaSi.setChecked(true);

        } else if(listaP.get(posicion).getRespuesta().equals("1")) {
            respuestaNo.setChecked(true);

        } else if(listaP.get(posicion).getRespuesta().equals("0")) {
            respuestaNa.setChecked(true);

        } else {
            respuestaSi.setChecked(true);

        }

        AlertDialog.Builder setP = new AlertDialog.Builder(Home_Parte_Diaria.this);
        setP.setTitle("INSPECCIÓN DEL EQUIPO");
        setP.setMessage(listaP.get(posicion).getId() + ". "+ listaP.get(posicion).getPregunta());
        setP.setView(vistaRespuestas);
        setP.setCancelable(false);

        if(posicion > 0) {
            setP.setNegativeButton("ATRAS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    posicion = posicion - 1;
                    carga.iniciarCarga();
                    setPregunta();

                }
            });

        }

        setP.setPositiveButton("SIGUIENTE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(respuestaSi.isChecked()) {
                    listaP.set(posicion, new modelo_preguntas(listaP.get(posicion).getFormato(), listaP.get(posicion).getId(), listaP.get(posicion).getPregunta(), "2"));

                } else if(respuestaNo.isChecked()) {
                    listaP.set(posicion, new modelo_preguntas(listaP.get(posicion).getFormato(), listaP.get(posicion).getId(), listaP.get(posicion).getPregunta(), "1"));

                } else if(respuestaNa.isChecked()) {
                    listaP.set(posicion, new modelo_preguntas(listaP.get(posicion).getFormato(), listaP.get(posicion).getId(), listaP.get(posicion).getPregunta(), "0"));

                }

                if(posicion == cantidadP) {
                    getObservacion();

                } else {
                    posicion = posicion + 1;
                    carga.iniciarCarga();
                    setPregunta();

                }

            }
        });

        carga.cargaCompleta();
        setP.show();

    }

    public void getObservacion() {
        AlertDialog.Builder set = new AlertDialog.Builder(this);
        set.setTitle("INSPECCIÓN DEL EQUIPO");
        vistaObservacion = getLayoutInflater().inflate(R.layout.alerta_observacion, null);
        caja_observacion_pd = (EditText)vistaObservacion.findViewById(R.id.caja_observacion_alerta);
        set.setView(vistaObservacion);
        set.setCancelable(false);

        set.setPositiveButton("REGISTRAR INFORMACIÓN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                observacion = caja_observacion_pd.getText().toString();

                carga.iniciarCarga();
                registrarPd();

            }
        });

        set.show();

    }

    public void registrarPd() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("consecutivo", equipos.getCon());
        hashMapToken.put("tipo", equipos.getTipo());
        hashMapToken.put("idEquipo", equipos.getCodigo());
        hashMapToken.put("km", equipos.getKm_inicial());
        hashMapToken.put("observacion", observacion);
        hashMapToken.put("usuario", sesion.getId());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlRegistrarPd, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        carga.iniciarCarga();
                        registrarConsecutivo();

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS! NO SE PUDO REGISTRAR.", Toast.LENGTH_LONG).show();

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

    public void registrarConsecutivo() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("idApp", sesion.getApp());
        hashMapToken.put("consecutivo", equipos.getCon());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlRegistrarConsecutivo, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        carga.iniciarCarga();
                        registroMasivo();

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS, NO SE PUDO ACTUALIZAR EL CONSECUTIVO!", Toast.LENGTH_SHORT).show();

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

    public void registroMasivo() {
        for (int i = 0; i <= cantidadP; i++) {
            registrarRespuesta(listaP.get(i).getFormato(), listaP.get(i).getId(), listaP.get(i).getRespuesta().trim());

        }

        carga.cargaCompleta();

        Toast.makeText(getApplicationContext(), "REGISTRO EXITOSO!", Toast.LENGTH_LONG).show();
        mostrarInfo();


    }

    public void registrarRespuesta(String formato, String id, String respuesta) {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("consecutivo", equipos.getCon());
        hashMapToken.put("formato", formato);
        hashMapToken.put("id", id);
        hashMapToken.put("respuesta", respuesta.trim());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlRegistrarRespuestas, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS, NO SE PUDO REGISTRAR!", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException error) {
                    Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_LONG).show();

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

    public void getRespuestas() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("idApp", sesion.getApp());
        hashMapToken.put("consecutivo", equipos.getCon());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlRespuestas, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    carga.cargaCompleta();
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        JSONArray jArray = response.getJSONArray("datos");

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject datos = jArray.getJSONObject(i);

                            listaP.add(new modelo_preguntas(datos.getString("Id_formato"), datos.getString("Consecutivo_pregunta"), datos.getString("Descripcion_pregunta"), datos.getString("calificacion")));

                        }

                        mostrarInfo();

                    } else {
                        Toast.makeText(getApplicationContext(), "NO HAY RESPUESTAS REGISTRADAS!", Toast.LENGTH_LONG).show();

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

    public void setEditarRespuesta(int posicion) {
        vistaRespuestas = getLayoutInflater().inflate(R.layout.modelo_respuestas_pd, null);
        grupoRespuestas = (RadioGroup) vistaRespuestas.findViewById(R.id.modelo_respuestas);
        respuestaSi = (RadioButton) vistaRespuestas.findViewById(R.id.radio_si);
        respuestaNo = (RadioButton) vistaRespuestas.findViewById(R.id.radio_no);
        respuestaNa = (RadioButton) vistaRespuestas.findViewById(R.id.radio_na);

        if(listaP.get(posicion).getRespuesta().equals("2")) {
            respuestaSi.setChecked(true);

        } else if(listaP.get(posicion).getRespuesta().equals("1")) {
            respuestaNo.setChecked(true);

        } else if(listaP.get(posicion).getRespuesta().equals("0")) {
            respuestaNa.setChecked(true);

        } else {
            respuestaSi.setChecked(true);

        }

        AlertDialog.Builder setP = new AlertDialog.Builder(Home_Parte_Diaria.this);
        setP.setTitle("INSPECCIÓN DEL EQUIPO");
        setP.setMessage(listaP.get(posicion).getId() + ". "+ listaP.get(posicion).getPregunta());
        setP.setView(vistaRespuestas);
        setP.setCancelable(false);

        setP.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }

        });

        setP.setPositiveButton("ACTUALIZAR RESPUESTA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevaRespuesta = "";

                if(respuestaSi.isChecked()) {
                    nuevaRespuesta = "2";

                } else if(respuestaNo.isChecked()) {
                    nuevaRespuesta = "1";

                } else if(respuestaNa.isChecked()) {
                    nuevaRespuesta = "0";

                }

                if(listaP.get(posicion).getRespuesta().trim().equals(nuevaRespuesta)) {

                } else {
                    listaP.set(posicion, new modelo_preguntas(listaP.get(posicion).getFormato(), listaP.get(posicion).getId(), listaP.get(posicion).getPregunta(), nuevaRespuesta));

                    carga.iniciarCarga();
                    actualizarRespuesta(listaP.get(posicion).getFormato(), listaP.get(posicion).getId(), listaP.get(posicion).getRespuesta().trim());

                }

            }

        });

        carga.cargaCompleta();
        setP.show();

    }

    public void actualizarRespuesta(String formato, String id, String respuesta) {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("consecutivo", equipos.getCon());
        hashMapToken.put("formato", formato);
        hashMapToken.put("id", id);
        hashMapToken.put("respuesta", respuesta.trim());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlActualizarRespuesta, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        Toast.makeText(getApplicationContext(), "RESPUESTA ACTUALIZADA!", Toast.LENGTH_LONG).show();
                        adaptar();

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS, NO SE PUDO REGISTRAR!", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException error) {
                    Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_LONG).show();

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

    public void adaptar() {
        adaptador = new adaptador_preguntas(Home_Parte_Diaria.this, listaP);
        listadoQuiz.setAdapter(adaptador);

        adaptador.setOnItemClick(new adaptador_preguntas.OnItemClick() {
            @Override
            public void itemClick(int position) {
                setEditarRespuesta(position);

            }
        });

    }

    public void mostrarInfo() {
        caja_codigo_pd_uno.setText(equipos.getCodigo());
        caja_placa_pd_uno.setText(equipos.getPlaca());
        caja_km_pd_uno.setText(equipos.getKm_inicial());
        caja_descripcion_pd_uno.setText(equipos.getDescripcion());
        texto_num_pd_uno.setText("PARTE DIARIA #" +equipos.getCon());

        if(!observacion.trim().isEmpty()) {
            vistaObservacionI.setVisibility(View.VISIBLE);
            caja_observacion_pd_uno.setText(observacion);

        }

        if(!sesion.getRol().equals("OTRO")) {
            areaBoton_pd.setVisibility(View.GONE);

        }

        adaptar();

    }

    public void obtenerSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);
        sesion.setId(memoria.getString(sesion.getID_USUARIO(), null));
        sesion.setApp(memoria.getString(sesion.getID_APP(), null));
        sesion.setNomApp(memoria.getString(sesion.getNOM_APP(), null));
        sesion.setCodigo(memoria.getString(sesion.getCF(), null));
        sesion.setNombreUsuario(memoria.getString(sesion.getNOM_USER(), null));
        sesion.setRol(memoria.getString(sesion.getROL(), null));

        if (sesion.getId() == null || sesion.getNomApp() == null || sesion.getApp() == null || sesion.getCodigo() == null) {

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

        establecer.commit();

    }

    public void obtenerDatos() {
        SharedPreferences memoria = getSharedPreferences(equipos.getID_CONSECUTIVO(), MODE_PRIVATE);
        equipos.setCon(memoria.getString(equipos.getCONSECUTIVO(), null));

    }

    public void guardarDatos() {
        SharedPreferences memoria = getSharedPreferences(equipos.getID_CONSECUTIVO(), MODE_PRIVATE);

        SharedPreferences.Editor establecer = memoria.edit();
        establecer.putString(equipos.getCONSECUTIVO(), equipos.getCon());

        establecer.commit();

    }

    public void cerrar() {
        Intent intento = new Intent(this, Home_Datos.class);
        startActivity(intento);
        this.finish();

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Menu.class);
        startActivity(intento);
        this.finish();

    }

    public void abrirPd() {
        if(equipos.getTipo().trim().equals("Kilometraje")) {
            Intent intento = new Intent(this, Parte_diaria_km.class);
            startActivity(intento);

        } else {


        }

    }

}
