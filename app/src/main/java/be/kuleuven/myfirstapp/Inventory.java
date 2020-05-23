package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends AppCompatActivity {

    private List<Product> productList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        getInventory(id);

        adapter = new InventoryAdapter(productList, this);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(this);

    }


    public void getInventory(int id) {

        requestQueue = Volley.newRequestQueue(this);
        String url = "https://studev.groept.be/api/a19sd303/getProducts/"+id;
        final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        try {
                            Product product = new Product(object.getLong("fk_barcode"), object.getString("name"), object.getDouble("price"), object.getInt("quantity"));
                            productList.add(product);
                        }catch (JSONException e){
                            Product product = new Product(object.getLong("fk_barcode"), object.getString("name"), object.getInt("quantity"), object.getString("picture").replace(" ", "/"), object.getString("brand"));
                            System.out.println(product.getPicture());
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Inventory.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();

                error.printStackTrace();
            }
        });
        requestQueue.add(queueRequest);
    }

}
