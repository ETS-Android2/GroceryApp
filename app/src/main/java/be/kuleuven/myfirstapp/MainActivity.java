package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private EditText test;
    private TextView barcodeInfo;
    private Button knop;
    private Button next;
    private Button inventory;
    private Button map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();


        knop = (Button) findViewById(R.id.button);
        test = (EditText) findViewById(R.id.text);
        requestQueue = Volley.newRequestQueue(this);
        next = (Button) findViewById(R.id.next);
        barcodeInfo = (TextView) findViewById(R.id.textView);
        inventory = (Button) findViewById(R.id.inventory);
        map = (Button) findViewById(R.id.map);

    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.button:
                sendData(test.getText().toString().replace(" ", "+"));
                barcodeInfo.setText(test.getText().toString().replace(" ", "+"));
                break;
            case R.id.next:
                Intent intent = new Intent(MainActivity.this, Scanner.class);
                MainActivity.this.startActivity(intent);
                break;
            case R.id.inventory:
                Intent intent1 = new Intent(MainActivity.this, Inventory.class);
                MainActivity.this.startActivity(intent1);
                break;
            case R.id.map:
                Intent intent2 = new Intent(MainActivity.this, MapsActivity.class);
                MainActivity.this.startActivity(intent2);
                break;
            default:
                break;

        }
    }

    public void sendData(String value) {
        final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/test2/"+value;

        final StringRequest submitRequest = new StringRequest(Request.Method.GET, QUEUE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "Order placed", Toast.LENGTH_SHORT).show();

            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Unable to place the order", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }
}



