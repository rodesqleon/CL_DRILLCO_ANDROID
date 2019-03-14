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

public class HistReq extends Activity {
    TextView txtorden;
    ListView lstdetalle;
    ArrayAdapter<String> adapter;
    MyAsyncTask mTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.histreq);


        txtorden = (TextView) findViewById(R.id.txtorden);
        Bundle bundle = this.getIntent().getExtras();
        txtorden.setText(bundle.getString("ID"));




        lstdetalle = (ListView)findViewById(R.id.lstdetalle);
        adapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        lstdetalle.setAdapter(adapter);

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
            String userid =  txtorden.getText().toString();

            String query = "select pl.LINE_NO as [LINEA], pl.PART_ID as [CODIGO PRODUCTO], (case isnull(p.description,'') when '' then isnull(replace(CONVERT ( VARCHAR ( 200 ), CONVERT (VARBINARY( 200 ),pb.bits)),char(0),''),'')  else p.DESCRIPTION end) as [PRODUCTO], pl.ORDER_QTY as [CANTIDAD], pl.UNIT_PRICE as [PRECIO UNITARIO], pl.ORDER_QTY * pl.UNIT_PRICE as [TOTAL FINAL] from PURC_REQ_LINE pl,PURC_REQ_LN_BINARY pb, PART p where pl.PURC_REQ_ID = '"+ userid +"' and pl.PURC_REQ_ID = pb.PURC_REQ_ID and pl.LINE_NO = pb.PURC_REQ_LINE_NO and pl.PART_ID = p.id order by pl.line_no" ;

            try {
                Class.forName( "net.sourceforge.jtds.jdbc.Driver" );
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.13;databaseName=DRILPRUE","sa","13871388");
                Statement stmt = conn.createStatement();

                reset = stmt.executeQuery(query);


                while(reset.next()){

                    if ( isCancelled() ) break;

                    final String str = "Cantidad:"+reset.getInt("CANTIDAD")+" Precio unitario:$"+reset.getInt("PRECIO UNITARIO")+" TOTAL:$"+reset.getInt("TOTAL FINAL")+"   "+reset.getString("PRODUCTO");
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