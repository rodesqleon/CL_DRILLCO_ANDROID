package com.example.spand.drillco;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Login extends Activity
{
    ConnectionClass connectionClass;
    EditText edtuserid,edtpass;
    Button btnlogin;
    ProgressBar pbbar;
    FloatingActionButton btnadmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btnadmin = (FloatingActionButton) findViewById(R.id.btnadmin);

        btnadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, SesionAdmin.class);
                finish();
                startActivity(i);
            }
        });

        connectionClass = new ConnectionClass();
        edtuserid = (EditText) findViewById(R.id.et_username);
        btnlogin = (Button) findViewById(R.id.btn_Login);
        edtpass = (EditText) findViewById(R.id.et_password);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin  doLogin = new DoLogin();
                doLogin.execute("");

            }
        });
    }


    public class DoLogin extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;


        String userid = edtuserid.getText().toString();
        String password = edtpass.getText().toString();


        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(Login.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess) {
                Toast.makeText(Login.this,r,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if(userid.trim().equals("")|| password.trim().equals(""))
                z = "Por favor ingrese sus datos";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Error en la conexión con la base de datos";
                    } else {
                        String query = "select u.USER_ID, d.PASSWORD from DRILL_MAE_USUARIO_MOVIL d, GROUP_USER u where u.USER_ID='" + userid + "' and d.PASSWORD='" + password + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if (rs.next()) {

                            z = "Correcto";
                            isSuccess = true;

                            Intent i = new Intent(Login.this, Requisiciones.class);
                            Bundle b = new Bundle();
                            b.putString("NAME", edtuserid.getText().toString());

                            i.putExtras(b);
                            finish();
                            startActivity(i);
                        } else {
                            z = "Datos incorrectos";
                            isSuccess = false;
                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions";
                }
            }
            return z;
        }
    }
}
