package com.example.spand.drillco;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Proveedor extends Activity {
    TextView txtnombre;
    ListView lstdetalle;
    ArrayAdapter<String> adapter;
    MyAsyncTask mTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proveedor);

        txtnombre = (TextView) findViewById(R.id.txtnombre);
        Bundle bundle = this.getIntent().getExtras();
        txtnombre.setText(bundle.getString("NOMBRE"));




        lstdetalle = (ListView)findViewById(R.id.lstdetalle);
        adapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        lstdetalle.setAdapter(adapter);

        lstdetalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                Intent i = new Intent(Proveedor.this, HistReq.class);
                i.putExtra("ID", str);
                startActivity(i);
            }
        });
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
            String proveedor = txtnombre.getText().toString();

            String query = "select top 5 p.ID, p.total_amt_ordered, P.REQUISITION_DATE, v.NAME from PURC_REQUISITION p, VENDOR v where p.vendor_id = v.id and p.status = 'C' and v.NAME = '"+ proveedor +"' order by REQUISITION_DATE" ;

            try {
                Class.forName( "net.sourceforge.jtds.jdbc.Driver" );
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.13;databaseName=DRILPRUE","sa","13871388");
                Statement stmt = conn.createStatement();

                reset = stmt.executeQuery(query);


                while(reset.next()){

                    if ( isCancelled() ) break;

                    final String str = reset.getInt("ID")+ "                        "+reset.getDate("REQUISITION_DATE")+"                    "+"$" +reset.getInt("total_amt_ordered");
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