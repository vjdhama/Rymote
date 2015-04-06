package com.example.vjdhama.pyrote;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    final String BASE_URL = "http://192.168.0.101:3000/api/";
    HashMap params;
    String open_url;
    AlertDialog alertDialog;
    Uri data;
    int volume_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        params = new HashMap();
        params.put("musique_toggle", "?state=toggle");
        params.put("musique_next","?state=next");
        params.put("rhythmbox_play", "?state=play");
        params.put("rhythmbox_pause", "?state=pause");
        params.put("rhythmbox_next", "?state=next");
        params.put("firefox_open", "?url=");
        params.put("volume", "?amount=");

        setContentView(R.layout.activity_main);

        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        Intent intent = getIntent();

        Button musique_toggle = (Button) findViewById(R.id.musique_toggle);
        Button musique_next = (Button) findViewById(R.id.musique_next);
        Button rhythmbox_play = (Button) findViewById(R.id.rhythmbox_play);
        Button rhythmbox_pause = (Button) findViewById(R.id.rhythmbox_pause);
        Button rhythmbox_next = (Button) findViewById(R.id.rhythmbox_next);
        Button firefox_open = (Button) findViewById(R.id.firefox_button);
        Button volume_button = (Button) findViewById(R.id.volume_set_button);

        final SeekBar volume_seekbar = (SeekBar) findViewById(R.id.volume_seekBar);

        final EditText firefox_input = (EditText) findViewById(R.id.firefox_editText);

        musique_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJsonData("musique", (String) params.get("musique_toggle"), BASE_URL);
            }
        });

        musique_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJsonData("musique", (String) params.get("musique_next"), BASE_URL);
            }
        });

        rhythmbox_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJsonData("rhythmbox", (String) params.get("rhythmbox_play"), BASE_URL);
            }
        });

        rhythmbox_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJsonData("rhythmbox", (String) params.get("rhythmbox_pause"), BASE_URL);
            }
        });

        rhythmbox_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJsonData("rhythmbox", (String) params.get("rhythmbox_next"), BASE_URL);
            }
        });

        volume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volume_progress = volume_seekbar.getProgress();
                open_url = (String) params.get("volume") + Integer.toString(volume_progress);
                Log.i("Volume Url : ", open_url);
                getJsonData("volume", open_url, BASE_URL);
            }
        });

        volume_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        firefox_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firefox_input.getText().toString().equals("")) {
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Empty URl.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }else{
                    open_url = (String) params.get("firefox_open") + firefox_input.getText();
                    getJsonData("firefox", open_url, BASE_URL);
                }
            }
        });

        if (intent.getData() != null) {
            data = intent.getData();
            Log.i("Incoming URL : ", data.getHost());

            firefox_input.setText(data.toString());

            firefox_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    open_url = (String) params.get("firefox_open") + data.toString();
                    getJsonData("firefox", open_url, BASE_URL);
                }
            });
        }

    }

    public void getJsonData(String relative_path, String params, String url) {

        String URL = url + relative_path + params;

        Log.e("Query URL : ", URL);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                if (error instanceof NoConnectionError) {
                    Log.d("Error : ", "NoConnectionError");
                } else if (error instanceof AuthFailureError) {
                    Log.d("Error : ", "AuthFailureError");
                } else if (error instanceof ServerError) {
                    Log.d("Error : ", "ServerError");
                } else if (error instanceof NetworkError) {
                    Log.d("Error : ", "NetworkError");
                } else if (error instanceof ParseError) {
                    Log.d("Error : ", "ParseError");
                }else if (error instanceof TimeoutError) {
                    Log.d("Error : ", "TimeoutError");
                }
            }
        });

        MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue().add(req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
