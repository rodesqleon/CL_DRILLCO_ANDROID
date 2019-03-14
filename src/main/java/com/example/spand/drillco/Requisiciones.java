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

public class Requisiciones extends Activity {

    TextView txtuser;
    ListView listview;
    ArrayAdapter<String> adapter;
    MyAsyncTask mTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requisiciones);

        txtuser = (TextView) findViewById(R.id.txtuser);

        Bundle bundle = this.getIntent().getExtras();
        txtuser.setText(bundle.getString("NAME"));



        listview = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.test_list_item);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                Intent i = new Intent(Requisiciones.this, Detalle.class);
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
            String userid =  txtuser.getText().toString();


            String query = "select P.ID, v.NAME, P.VENDOR_ID, pr.CURRENCY_ID, P.DESIRED_RECV_DATE, PR.AMOUNT from PURC_REQUISITION p, VENDOR v, PURC_REQ_CURR pr where pr.currency_id = P.CURRENCY_ID and p.ASSIGNED_TO = '"+ userid +"' and p.STATUS = 'I' and p.VENDOR_ID = v.ID and p.ID = pr.PURC_REQ_ID order by P.REQUISITION_DATE " ;

            try {
                Class.forName( "net.sourceforge.jtds.jdbc.Driver" );
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.13;databaseName=DRILPRUE","sa","13871388");
                Statement stmt = conn.createStatement();

                reset = stmt.executeQuery(query);


                while(reset.next()){

                    if ( isCancelled() ) break;

                     String str = reset.getString("ID")+"   "+reset.getDate("DESIRED_RECV_DATE")+"             RUT: "+reset.getString("VENDOR_ID")+"\n"+reset.getString("NAME")+"  \n  "+reset.getString("CURRENCY_ID")+ " " + reset.getInt("AMOUNT");


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