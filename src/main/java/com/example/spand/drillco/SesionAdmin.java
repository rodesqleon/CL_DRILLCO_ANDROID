package com.example.spand.drillco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SesionAdmin extends Activity {

        ConnectionClass connectionClass;
        EditText edtuserid,edtpass;
        Button btnlogin;
        ProgressBar pbbar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sesionadmin);

        connectionClass = new ConnectionClass();
        edtuserid = (EditText) findViewById(R.id.et_username);
        edtpass = (EditText) findViewById(R.id.et_password);
        btnlogin = (Button) findViewById(R.id.btn_Login);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(edtuserid.getText().toString().equals("Sysadm") &&
                        edtpass.getText().toString().equals("Sy123")) {
                    Toast.makeText(getApplicationContext(),
                            "Correcto",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SesionAdmin.this, Administrador.class);
                    Bundle b = new Bundle();
                    b.putString("NAME", edtuserid.getText().toString());
                    i.putExtras(b);
                    finish();
                    startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(), "Datos incorrectos",Toast.LENGTH_SHORT).show();


                        btnlogin.setEnabled(true);
                    }
                }

        });

    }



    }
