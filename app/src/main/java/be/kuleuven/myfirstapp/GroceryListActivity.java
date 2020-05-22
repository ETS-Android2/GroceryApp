package be.kuleuven.myfirstapp;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
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

public class GroceryListActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/getAllGroceryList/";
    private static final String REMOVE_URL = "https://studev.groept.be/api/a19sd303/removeList/";
    private static final String RENAME_URL = "https://studev.groept.be/api/a19sd303/renameList/";
    int id1;
    ArrayList<String> list = new ArrayList<>();
    ListView list_view;
    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_grocery_list);
        //get userID
        Intent intent = getIntent();
        id1 = intent.getIntExtra("id", -1);
        //setMenuItem();
        receiveData();


        //find view by id
        list_view = findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter(GroceryListActivity.this, android.R.layout.simple_list_item_1, list);
        list_view.setAdapter(arrayAdapter);

        //
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {

                PopupMenu popupMenu = new PopupMenu(GroceryListActivity.this, view);
                System.out.println("name " + list.get(position).toString());
                popupMenu.getMenuInflater().inflate(R.menu.pop_up_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.updateListName:
                                //function for update
                                AlertDialog.Builder builder = new AlertDialog.Builder(GroceryListActivity.this);
                                View v = LayoutInflater.from(GroceryListActivity.this).inflate(R.layout.item_dialog, null, false);
                                builder.setTitle("Change List Name");
                                final EditText editText = v.findViewById(R.id.etItem);
                                editText.setText(list.get(position));

                                //set custome view to dialog
                                builder.setView(v);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!editText.getText().toString().isEmpty()) {
                                            String completeURL=(editText.getText().toString().trim()).replaceAll("\\s+", "")+"/"+list.get(position).trim()+"/"+id1;
                                            postRename(completeURL);
                                            list.set(position, editText.getText().toString().trim());
                                            arrayAdapter.notifyDataSetChanged();


                                        } else {
                                            editText.setError("change here !");
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.show();

                                break;

                            case R.id.itemDel:
                                //function for del
                                Toast.makeText(GroceryListActivity.this, "List deleted", Toast.LENGTH_SHORT).show();

                                postDelete(list.get(position).toString() + "/" + id1);
                                list.remove(position);
                                arrayAdapter.notifyDataSetChanged();

                                break;
                            case R.id.addItem:
                                //function add
                                Intent intent1 = new Intent(GroceryListActivity.this, IngredientOfGroceryList.class);
                                intent1.putExtra("list_name", list.get(position).toString());
                                intent1.putExtra("id", id1);
                                GroceryListActivity.this.startActivity(intent1);
                                break;


                        }

                        return true;
                    }
                });

                //don't forgot this
                popupMenu.show();

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){       //back button working
        System.out.println("hallo");
        finish();
        return true;
    }

    // create "+" sign to add new grocery list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.add_item:
                _addItem();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;

        }

        return true;
    }

    /*
     * method for adding item
     * */
    private void _addItem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(GroceryListActivity.this);
        builder.setTitle("Add New List");

        View v = LayoutInflater.from(GroceryListActivity.this).inflate(R.layout.item_dialog, null, false);

        builder.setView(v);
        final EditText etItem = v.findViewById(R.id.etItem);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!etItem.getText().toString().isEmpty()) {
                    list.add(etItem.getText().toString().trim());
                    arrayAdapter.notifyDataSetChanged();

                } else {
                    etItem.setError("add list here !");
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();


    }

    // get All list from database


    private void receiveData(){

        //requestQueue = Volley.newRequestQueue(this);
            final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET,QUEUE_URL+id1,null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i=0; i<response.length(); ++i) {
                    JSONObject product= null;
                    try {
                        product= response.getJSONObject(i);
                        list.add(i, product.get("list_name") + "\n");

                        arrayAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GroceryListActivity.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(queueRequest);
    }

    private void postDelete(final String value) {
        final StringRequest submitRequest = new StringRequest(Request.Method.GET, REMOVE_URL + value, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(GroceryListActivity.this, "List was deleted", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GroceryListActivity.this, "Unable to delete list", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }
    private void postRename(final String value) {
        final StringRequest submitRequest = new StringRequest(Request.Method.GET, RENAME_URL+ value, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(GroceryListActivity.this, "List name changed", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GroceryListActivity.this, "Unable to change", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }
}
