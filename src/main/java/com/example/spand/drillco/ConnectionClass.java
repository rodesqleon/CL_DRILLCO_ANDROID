package com.example.spand.drillco;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionClass {
    String ip = "192.168.1.13";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "DRILPRUE";
    String un = "sa";
    String password = "13871388";

    public String getip() {
        return ip;

    }

    public String getclasss() {
        return classs;

    }

    public String getdb() {
        return db;
    }

    public String getun() {
        return un;
    }

    public String getpassword() {
        return password;
    }

    public void setip(String Ip) {
        ip = Ip;
    }

    public void setdb(String Db) {
        db = Db;
    }

    public void setclasss(String Classs) {
        classs = Classs;
    }

    public void setun(String Un) {
        un = Un;
    }

    public void setpassword(String Password) {
        password = Password;
    }




    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
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
}
