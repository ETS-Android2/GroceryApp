package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
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

public class GrocerySearchActivity extends AppCompatActivity {
    private Button btnPlus;
    private Button btnMinus;
    private TextView lblQty;
    private RequestQueue requestQueue;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/getAllBarcodes";
    private static final String SUBMIT_URL = "https://studev.groept.be/api/a19sd303/addItemToList/";

    SearchView mySearchView;
    ListView myListView;

    ArrayList<String> list;
    ArrayList<String> barcodeList;
    ArrayAdapter<String> adapter;
    String listname;
    int id1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        id1 = intent.getIntExtra("id", -1);
        listname = intent.getStringExtra("list_name");

        setContentView(R.layout.activity_search);

        btnMinus = (Button) findViewById(R.id.btnMinus);
        btnPlus = (Button) findViewById(R.id.btnPlus);
        lblQty = (TextView) findViewById(R.id.lblQty);
        mySearchView = findViewById(R.id.searchView);
        myListView = findViewById(R.id.lstView);

        list = new ArrayList<>();
        barcodeList = new ArrayList<>();
        //setMenuItem();
        receiveData();


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        myListView.setAdapter(adapter);

        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList = (myListView.getItemAtPosition(position).toString());
                //System.out.println(selectedFromList);
                int quantity = Integer.parseInt(lblQty.getText().toString());

                if (quantity > 0) {
                    String submittext = id1 + "/" + listname.trim() + "/" + barcodeList.get(position) + "/" + quantity;
                    post(submittext);//selectedFromList.split(" ")[0]

                } else {
                    Toast.makeText(GrocerySearchActivity.this, "Quantity must be greater than 0", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onBtnPlus_Clicked(View caller) {
        int quantity = Integer.parseInt(lblQty.getText().toString()) + 1;

        lblQty.setText(Integer.toString(quantity));

    }

    public void onBtnMinus_Clicked(View caller) {
        int quantity = Integer.parseInt(lblQty.getText().toString()) - 1;
        if (quantity >= 0) {//if there is no quantity
            lblQty.setText(Integer.toString(quantity));
        }

    }
    /*
    public void goBackMyList_Clicked(View caller) {

        Intent goback = new Intent(GrocerySearchActivity.this, IngredientOfGroceryList.class);
        goback.putExtra("list_name", listname);
        goback.putExtra("id", id1);
        GrocerySearchActivity.this.startActivity(goback);
        finish();

    }
    */


    @Override
    public boolean onSupportNavigateUp() {       //back button working
        finish();
        return true;
    }

    private void receiveData() {

        //requestQueue = Volley.newRequestQueue(this);
        final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, QUEUE_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); ++i) {
                    JSONObject product = null;
                    try {
                        product = response.getJSONObject(i);
                        list.add(i, product.get("name") + "\n");
                        barcodeList.add(i, product.getString("barcode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GrocerySearchActivity.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(queueRequest);
    }

/*
    public void setMenuItem() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, QUEUE_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);
                            // lstSource = new String[array.length()];

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);
                                list.add(i, product.get("name") + "\n");
                                barcodeList.add(i, product.getString("barcode"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }
*/
    private void post(final String value) {

        final StringRequest submitRequest = new StringRequest(Request.Method.GET, SUBMIT_URL + value, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(GrocerySearchActivity.this, "Order placed", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GrocerySearchActivity.this, "Unable to place the order", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }
}
