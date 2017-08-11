package com.example.nanda.agrinai2;



import android.app.Activity;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;




public class MainActivity extends Activity {
    EditText idname, idpassword;
    Button login;
    String name, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idname = (EditText) findViewById(R.id.idname);
        idpassword = (EditText) findViewById(R.id.idpassword);
        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = idname.getText().toString();
                id = idpassword.getText().toString();
                new sendToAgri(""+name,""+id).execute();
            }
        });

    }

    private class sendToAgri extends AsyncTask<String, Void, String> {
        int code = 0;
        String name, id, userName;

        public sendToAgri(String a, String b) {
            name = a;
            id = b;
        }

        @Override
        protected String doInBackground(String... params) {

            URL url;
            String jsonResponse;

            try {
                url = new URL("http://192.168.43.140:9000/agri/v1/newuser");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-type", "application/json");
                connection.setDoOutput(true);


                JSONObject jsondata = new JSONObject();
                jsondata.put("name", name);
                jsondata.put("id", id);


                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(String.valueOf(jsondata));
                writer.close();

                InputStream inputStream = null;
                int status = connection.getResponseCode();
                if (status == 200) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                BufferedReader reader;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputline;
                while ((inputline = reader.readLine()) != null)
                    buffer.append(inputline).append("\n");
                if (buffer.length() == 0) {
                    return null;
                }
                jsonResponse = buffer.toString();
                JSONObject json = new JSONObject(jsonResponse);
                code = json.getInt("code");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Done..", Toast.LENGTH_SHORT).show();
                    }
                });

                if (code == 200) {
                    JSONObject data = json.getJSONObject("data");
                    userName = data.getString("name");


                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Timeout!.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "MalformedURLException!.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {

                e.printStackTrace();
                Log.d("IOException",""+e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "IOException!.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "JSONException!.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (code == 200) {
                if (MainActivity.this != null) {
                    SharedPreferences sp = getSharedPreferences("Data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name", userName);
                    editor.apply();
                    Intent i = new Intent(MainActivity.this, Home.class);
                    startActivity(i);

                }
            } else if (code == 404) {
                Toast.makeText(MainActivity.this, "This User does not exists", Toast.LENGTH_LONG).show();
            }
        }
    }
}
