package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private EditText test;
    private TextView barcodeInfo;
    private Button knop;
    private Button add;
    private Button remove;
    private Button inventory;
    private Button map;
    private Button grocerylist;
    private int userId;
    private ArrayList<Long> barcodes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId",-1);

        knop = (Button) findViewById(R.id.button);
        test = (EditText) findViewById(R.id.text);
        requestQueue = Volley.newRequestQueue(this);
        add = (Button) findViewById(R.id.add);
        remove = (Button) findViewById(R.id.remove);
        barcodeInfo = (TextView) findViewById(R.id.textView);
        inventory = (Button) findViewById(R.id.inventory);
        map = (Button) findViewById(R.id.map);
        grocerylist = (Button) findViewById(R.id.groceryList);

        getBarcodes(userId);

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.button:
                sendData(test.getText().toString().replace(" ", "+"));
                barcodeInfo.setText(test.getText().toString().replace(" ", "+"));
                break;
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, Scanner.class);
                intent.putExtra("mode",0);
                intent.putExtra("barcodes", barcodes);
                intent.putExtra("id",userId);
                MainActivity.this.startActivity(intent);
                break;
            case R.id.remove:
                Intent intent0 = new Intent(MainActivity.this, Scanner.class);
                intent0.putExtra("mode",1);
                MainActivity.this.startActivity(intent0);
                break;
            case R.id.inventory:
                Intent intent1 = new Intent(MainActivity.this, Inventory.class);
                intent1.putExtra("id",userId);
                MainActivity.this.startActivity(intent1);
                break;
            case R.id.map:
                Intent intent2 = new Intent(MainActivity.this, MapsActivity.class);
                MainActivity.this.startActivity(intent2);
                break;
            case R.id.groceryList:
                Intent intent3 = new Intent(MainActivity.this, GroceryListActivity.class);
                MainActivity.this.startActivity(intent3);
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

    public void getBarcodes(int id) {

        requestQueue = Volley.newRequestQueue(this);
        String url = "https://studev.groept.be/api/a19sd303/getProducts/"+id;
        final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        barcodes.add(object.getLong("fk_barcode"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();

                error.printStackTrace();
            }
        });
        requestQueue.add(queueRequest);
    }
}



