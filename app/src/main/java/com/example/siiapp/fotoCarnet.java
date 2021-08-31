package com.example.siiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class fotoCarnet extends AppCompatActivity {

    /// OBJETOS ///

    conexion sql = new conexion();
    modelo_sesion sesion = new modelo_sesion();
    modelo_colaborador persona = new modelo_colaborador();

    /// ATRIBUTOS ///

    Bitmap imageBitmap, barra;

    String url = sql.webServices + "subirFotoCarnet.php", nombreFoto, cedulaC;
    String URL_CONSULTA_PROVEEDOR = sql.api + "proveedorAPP/";

    TextView textoEmpresa, textoNombre, textoCargo, cedula, codigObligatorio, botonEnviar, salir;
    EditText codigo;
    ImageView fotoCarnet, barraCodigo, imgSalir;

    private RequestQueue mQueue;
    byte[] imgFinal;

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gp_foto_carnet);

        mQueue = Volley.newRequestQueue(this);
        obtenerSesion();

        barraCodigo = (ImageView)findViewById(R.id.barraCodigo);

        textoEmpresa = (TextView)findViewById(R.id.textoEmpresa);
        textoNombre = (TextView)findViewById(R.id.texto_nombre_af_firmapp);
        textoCargo = (TextView)findViewById(R.id.textoCargo);
        cedula = (TextView)findViewById(R.id.cedula);

        codigo = (EditText)findViewById(R.id.codigo);

        fotoCarnet = (ImageView)findViewById(R.id.fotoCarnet);
        fotoCarnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activarCamara();

            }
        });

        botonEnviar = (TextView)findViewById(R.id.botonEnviar);
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreFoto = codigo.getText().toString();
                consultaP();

            }
        });

        codigObligatorio = (TextView)findViewById(R.id.codigObligatorio);
        codigo = (EditText)findViewById(R.id.codigo);

        salir = (TextView)findViewById(R.id.texto_salir_fotoCarnet);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

            }
        });

        imgSalir = (ImageView)findViewById(R.id.img_salir_fotoCarnet);
        imgSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

            }
        });

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void activarCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            fotoCarnet.setImageBitmap(imageBitmap);

        }

    }

    public void generarCodigoBarras() {
        try {
            barra = encodeAsBitmap(codigo.getText().toString(), BarcodeFormat.CODE_128, 800, 120);
            barraCodigo.setImageBitmap(barra);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void consultaP() {
        HashMap<String, String> hashMapToken = new HashMap<>();
        hashMapToken.put("id", nombreFoto);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, URL_CONSULTA_PROVEEDOR, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        JSONObject colaborador = response.getJSONObject("datos");

                        persona.setCodigoC(colaborador.getString("Id_colaborador"));
                        persona.setCedulaC(colaborador.getString("Doc_colaborador"));
                        persona.setNombreC(colaborador.getString("Nom_colaborador"));
                        persona.setCargoC(colaborador.getString("Cargo_colaborador"));
                        persona.setEstado(colaborador.getString("Estado"));
                        persona.setEmpresa(colaborador.getString("Empresa"));

                        subirIm();
                        mostrarDatos();
                        generarCodigoBarras();
                        codigo.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(), "COLABORADOR INEXISTENTE!", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException error) {
                    Toast.makeText(getApplicationContext(), "ERROR: " + error, Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE RESPUESTA: " + error, Toast.LENGTH_LONG).show();

            }

        });

        mQueue.add(peticion);

    }

    public void subirIm() {
        StringRequest peticion = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_LONG).show();

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();

                String datosFirma = imagenFotoC();
                parametros.put("image", datosFirma);
                parametros.put("nombreImg", persona.getCedulaC());

                return parametros;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(peticion);

    }

    public void mostrarDatos() {
        textoEmpresa.setText(persona.getEmpresa());
        textoNombre.setText(persona.getNombreC());
        textoCargo.setText(persona.getCargoC());
        cedula.setText(persona.getCedulaC());

    }

    public String imagenFotoC() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] envio = stream.toByteArray();

        final String encode = Base64.encodeToString(envio, Base64.DEFAULT);

        return encode;
    }

    public void obtenerSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);
        sesion.setId(memoria.getString(sesion.getID_USUARIO(), null));
        sesion.setApp(memoria.getString(sesion.getID_APP(), null));
        sesion.setNomApp(memoria.getString(sesion.getNOM_APP(), null));
        sesion.setCodigo(memoria.getString(sesion.getCF(), null));
        sesion.setNombreUsuario(memoria.getString(sesion.getNOM_USER(), null));

        if (sesion.getId() == null || sesion.getNomApp() == null || sesion.getApp() == null || sesion.getCodigo() == null) {

        }

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Menu.class);
        startActivity(intento);
        this.finish();

    }

    /**************************************************************
     * getting from com.google.zxing.client.Android.encode.QRCodeEncoder
     *
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/Android/src/com/google/zxing/client/Android/encode/EncodeActivity.Java
     * http://code.google.com/p/zxing/source/browse/trunk/Android/src/com/google/zxing/client/Android/encode/QRCodeEncoder.Java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    }

