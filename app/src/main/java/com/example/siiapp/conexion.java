package com.example.siiapp;

import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Base64;

public class conexion extends AppCompatActivity {

    String contenedor = "BD_CCC", contenedorEx = "CPersonal";
    String ip = "190.109.168.19";
    String api = "http://" +ip+ ":8090/api/";
    String webServices = "http://" + ip + ":81/";

    public Connection conexionSql() {
        Connection conexion = null;

        try {
            StrictMode.ThreadPolicy politica = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(politica);

            /// CARGAMOS EL DRIVER ///
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

            /// CONECTAMOS A LA BASE DE DATOS ///
            conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://"+ip+";databaseName=master;user=sa;password=h1dr2sql1t45ng2;");

            /// ALERTA DE CONEXIÓN CON EXITO ///
            Toast.makeText(this,"¡CONEXIÓN CON EXITO!",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();

        }

        return conexion;
    }

}
