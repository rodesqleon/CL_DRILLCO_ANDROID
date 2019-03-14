package com.example.spand.drillco;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Historia extends Activity {
    ListView lstorden;
    TextView txtproducto;
    ArrayAdapter<String> adapter;
    MyAsyncTask mTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historia);
        txtproducto = (TextView)findViewById(R.id.txtproducto);
        Bundle bundle = this.getIntent().getExtras();
        txtproducto.setText(bundle.getString("ID"));



        lstorden = (ListView)findViewById(R.id.lstorden);
        adapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        lstorden.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.sendEmptyMessage(0);
    }

    class MyAsyncTask extends AsyncTask<String, Void, ArrayList<String>>
    {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }


        @Override
        protected ArrayList<String> doInBackground( String... params){


            ArrayList<String> list = new ArrayList<String>();

            ResultSet reset = null;
            Connection conn = null;
            String historia = txtproducto.getText().toString();

            String query = "select top 5 P.ID as [N° REQUISICION], v.NAME as [NOMBRE PROVEEDOR], P.VENDOR_ID as [RUT], P.CURRENCY_ID as [MONEDA], P.DESIRED_RECV_DATE as [FEC DESEADA], pl.UNIT_PRICE as [PRECIO UNIT] from PURC_REQUISITION p, VENDOR v, PURC_REQ_LINE pl, PART pa where p.STATUS = 'C' and p.VENDOR_ID = v.ID and p.ID = pl.PURC_REQ_ID and pa.ID = pl.PART_ID and pa.DESCRIPTION = '"+ historia +"' order by p.desired_recv_date desc,p.id,pl.LINE_NO\n" ;

            try {
                Class.forName( "net.sourceforge.jtds.jdbc.Driver" );
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.13;databaseName=DRILPRUE","sa","13871388");
                Statement stmt = conn.createStatement();

                reset = stmt.executeQuery(query);


                while(reset.next()){

                    if ( isCancelled() ) break;

                    final String str =reset.getString("NOMBRE PROVEEDOR")+"\n"+"Moneda: "+reset.getString("MONEDA")+"                Precio unitario:  $ "+reset.getInt("PRECIO UNIT");
                    list.add(str);

                }


                conn.close();
            }

            catch (Exception e)
            {
                Log.w("111Error connection", "" + e.getMessage());
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