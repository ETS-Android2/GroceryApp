package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
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

public class HomePage extends AppCompatActivity {
    GridLayout mainGrid;
    private CardView shoppingList;
    private CardView nearbyGroceries;
    private CardView scanner;
    private CardView add;;
    private CardView remove;
    private CardView inventory;
    private RequestQueue requestQueue;
    private int userId;
    private ArrayList<Long> barcodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId",-1);

        mainGrid=(GridLayout)findViewById(R.id.mainGrid);
        shoppingList=(CardView) findViewById(R.id.shoppingList);
        nearbyGroceries=(CardView) findViewById(R.id.nearbyGroceries);

        add=(CardView) findViewById(R.id.add);
        remove=(CardView) findViewById(R.id.remove);
        inventory=(CardView) findViewById(R.id.remove);

        getBarcodes(userId);

        //Set Event
        setSingleEvent(mainGrid);

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void setSingleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i <mainGrid.getChildCount() ; i++) {
            //all child are cardView so they are casted cardView
            CardView cardView=(CardView)mainGrid.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {

                        case R.id.nearbyGroceries:
                            Intent intent2 = new Intent(HomePage.this, MapsActivity.class);
                            HomePage.this.startActivity(intent2);
                            break;
                        case R.id.shoppingList:
                            Intent intent3 = new Intent(HomePage.this, GroceryListActivity.class);
                            HomePage.this.startActivity(intent3);
                            break;

                        case R.id.add:
                        Intent intent = new Intent(HomePage.this, Scanner.class);
                        intent.putExtra("mode",0);
                        intent.putExtra("barcodes", barcodes);
                        intent.putExtra("id",userId);
                        HomePage.this.startActivity(intent);
                        break;
                        case R.id.remove:
                            Intent intent0 = new Intent(HomePage.this, Scanner.class);
                            intent0.putExtra("mode",1);
                            intent0.putExtra("barcodes", barcodes);
                            intent0.putExtra("id",userId);
                            HomePage.this.startActivity(intent0);
                            break;
                        case R.id.inventory:
                            Intent intent4 = new Intent(HomePage.this, Inventory.class);
                            intent4.putExtra("id",userId);
                            HomePage.this.startActivity(intent4);
                            break;

                        default:
                            break;
                    }
                }
            });
            
        }
    }
    public void sendData(String value) {
        final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/test2/"+value;

        final StringRequest submitRequest = new StringRequest(Request.Method.GET, QUEUE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(HomePage.this, "Order placed", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomePage.this, "Unable to place the order", Toast.LENGTH_LONG).show();
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

                Toast.makeText(HomePage.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();

                error.printStackTrace();
            }
        });
        requestQueue.add(queueRequest);
    }
}
