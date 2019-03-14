package com.example.spand.drillco;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Detalle extends Activity {
    ConnectionClass connectionclass;
    final Context context = this;

    TextView txtorden;
    TextView txtasignado;
    TextView txtnombre;
    TextView txttipo;
    TextView txtusuario;
    ListView lstdetalle;
    FloatingActionButton btncancelar;
    FloatingActionButton btnaceptar;
    ArrayAdapter<String> adapter;
    MyAsyncTask mTask;
    ResultSet rs;
    String call, db, un, passwords;
    Connection connect;
    Requisiciones userid;


    @SuppressLint("NewApi")
    private Connection CONN(String _user, String _pass, String _DB,
                            String _server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://" + _server + ";"
                    + "databaseName=" + _DB + ";user=" + _user + ";password="
                    + _pass + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle);

        txtasignado = (TextView) findViewById(R.id.txtasignado);
        txttipo = (TextView)findViewById(R.id.txttipo);
        txtusuario = (TextView)findViewById(R.id.txtusuario);

        btncancelar = (FloatingActionButton) findViewById(R.id.btncancelar);
        btnaceptar = (FloatingActionButton) findViewById(R.id.btnaceptar);

        txtorden = (TextView) findViewById(R.id.txtorden);
        Bundle bundle = this.getIntent().getExtras();
        txtorden.setText(bundle.getString("ID"));
        txtnombre = (TextView)findViewById(R.id.txtnombre);

        txtnombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Detalle.this, Proveedor.class);
                Bundle b = new Bundle();
                b.putString("NOMBRE", txtnombre.getText().toString());

                i.putExtras(b);
                startActivity(i);
            }
        });

        txtnombre.setText("COMERC., IMPORT. Y EXPORT. CENTER TOOLS LTDA.");


//-----------------------------------------Lista detalle--------------------------------------------

        lstdetalle = (ListView) findViewById(R.id.lstdetalle);
        adapter = new ArrayAdapter<>(this, android.R.layout.test_list_item);
        lstdetalle.setAdapter(adapter);

        lstdetalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                Intent i = new Intent(Detalle.this, Historia.class);
                i.putExtra("ID", str);
                startActivity(i);
            }
        });

        connectionclass = new ConnectionClass();
        call = connectionclass.getip();
        un = connectionclass.getun();
        passwords = connectionclass.getpassword();
        db = connectionclass.getdb();
        connect = CONN(un, passwords, db, call);
        userid = new Requisiciones();

        //******************************boton aprobar requisicion******************************//

        btnaceptar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle("Requisición");
                alertDialogBuilder
                        .setMessage("Desea aprobar esta requisición?")
                        .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String requisiciones = txtorden.getText().toString();
                        String asignado = txtasignado.getText().toString();
                        String tipo = txttipo.getText().toString();
                        String usuario = txtusuario.getText().toString();
                        String querycmd = "UPDATE PURC_REQUISITION set STATUS = 'V', ASSIGNED_TO = '"+asignado+"', APP1_DATE = getdate(), APP2_DATE = getdate(), USER_10 = 'APP_MOVIL' where ID = '"+requisiciones+"' " +
                                "update purc_req_line set line_status = 'V' where purc_req_id = '"+requisiciones+"' " +
                                "select top 1 task_no,seq_no from TASK where EC_ID = '"+requisiciones+"' order by seq_no desc " +
                                "UPDATE TASK SET USER_ID = 'FPADILLA', STATUS = 'P' WHERE EC_ID = '"+ requisiciones +"' AND SEQ_NO = 1 AND SUB_TYPE = 'AT' " +
                                "UPDATE TASK SET COMPLETED_DATE = getdate(), STATUS_EFF_DATE = getdate(), STATUS = 'C' WHERE EC_ID = '"+ requisiciones +"' AND SEQ_NO > 1" +
                                    "insert TASK (TYPE, TASK_NO, SEQ_NO, USER_ID, SUB_TYPE, EC_ID, STATUS, COMPLETED_DATE) select TYPE, TASK_NO, SEQ_NO + 1, '"+ usuario +"', '"+ tipo +"', EC_ID, 'C', GETDATE() from task where ec_id = '"+ requisiciones +"' and SEQ_NO = 1 and SUB_TYPE = 'AT'";
                        try {
                            Statement statement = connect.createStatement();
                            rs = statement.executeQuery(querycmd);

                            } catch (SQLException e){}
                        Intent i = new Intent(Detalle.this, Aprobado.class);
                        finish();
                        startActivity(i);
                    }
                })

                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        //******************************boton rechazar requisicion******************************//

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle("Requisición");
                alertDialogBuilder
                        .setMessage("Desea rechazar esta requisición?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String requisiciones = txtorden.getText().toString();
                                String asignado = txtasignado.getText().toString();
                                String tipo = txttipo.getText().toString();
                                String usuario = txtusuario.getText().toString();
                                String querycmd = "UPDATE PURC_REQUISITION set STATUS = 'X', ASSIGNED_TO = '"+asignado+"', APP1_DATE = getdate(), APP2_DATE = getdate(), USER_10 = 'APP_MOVIL' where ID = '"+requisiciones+"'" +
                                        " update purc_req_line set line_status = 'X' where purc_req_id = '"+requisiciones+"'" +
                                        " select top 1 task_no,seq_no from TASK where EC_ID = '"+requisiciones+"' order by seq_no desc" +
                                        " UPDATE TASK SET USER_ID = 'FPADILLA', STATUS = 'P' WHERE EC_ID = '"+ requisiciones +"' AND SEQ_NO = 1 AND SUB_TYPE = 'AT'" +
                                        " UPDATE TASK SET COMPLETED_DATE = getdate(), STATUS_EFF_DATE = getdate(), STATUS = 'C' WHERE EC_ID = '"+ requisiciones +"' AND SEQ_NO > 1" +
                                        " insert TASK (TYPE, TASK_NO, SEQ_NO, USER_ID, SUB_TYPE, EC_ID, STATUS, COMPLETED_DATE) select TYPE, TASK_NO, SEQ_NO + 1, '"+ usuario +"', '"+ tipo +"', EC_ID, 'C', GETDATE() from task where ec_id = '"+ requisiciones +"' and SEQ_NO = 1 and SUB_TYPE = 'AT'";

                                try {
                                    Statement statement = connect.createStatement();
                                    rs = statement.executeQuery(querycmd);

                                } catch (SQLException e){}
                                Intent i = new Intent(Detalle.this, Rechazado.class);
                                finish();
                                startActivity(i);
                            }
                        })

                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.sendEmptyMessage(0);
    }
             /******************************Lista con detalle******************************/

             private class MyAsyncTask extends AsyncTask<String, Void, ArrayList<String>>
    {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }


        @Override
        protected ArrayList<String> doInBackground( String... params){


            ArrayList<String> list = new ArrayList<>();

            ResultSet reset;
            Connection conn;
            String requisiciones = txtorden.getText().toString();

            String query = "select pl.LINE_NO as [LINEA], pl.PART_ID as [CODIGO PRODUCTO], (case isnull(p.description,'') when '' then isnull(replace(CONVERT ( VARCHAR ( 200 ), CONVERT (VARBINARY( 200 ),pb.bits)),char(0),''),'')  else p.DESCRIPTION end) as [PRODUCTO], pl.ORDER_QTY as [CANTIDAD], pl.UNIT_PRICE as [PRECIO UNITARIO], pl.ORDER_QTY * pl.UNIT_PRICE as [TOTAL FINAL] from PURC_REQ_LINE pl,PURC_REQ_LN_BINARY pb, PART p where pl.PURC_REQ_ID = '"+ requisiciones +"' and pl.PURC_REQ_ID = pb.PURC_REQ_ID and pl.LINE_NO = pb.PURC_REQ_LINE_NO and pl.PART_ID = p.id order by pl.line_no";

            try {
                Class.forName( "net.sourceforge.jtds.jdbc.Driver" );
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.13;databaseName=DRILPRUE","sa","13871388");
                Statement stmt = conn.createStatement();
                reset = stmt.executeQuery(query);


                while(reset.next()){

                    if ( isCancelled() ) break;

                    final String str =reset.getString("PRODUCTO")+ "                                                                                                "+ "Cantidad:  "+reset.getInt("CANTIDAD")+ "\nPrecio Unitario: "+reset.getInt("PRECIO UNITARIO")+"  Valor: $"+reset.getInt("TOTAL FINAL");
                    list.add(str);

                }

                conn.close();
            }

            catch (Exception e)
            {
                Log.w("Error connection", "" + e.getMessage());
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list){

            adapter.clear();
            adapter.addAll(list);

            adapter.notifyDataSetChanged();
            handler.sendEmptyMessageDelayed(0, 1000);

        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
        }
    }

    public Handler handler = new Handler(){
        public void handleMessage( Message msg){
            super.handleMessage(msg);

            mTask = new MyAsyncTask();

            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        }
    };
}