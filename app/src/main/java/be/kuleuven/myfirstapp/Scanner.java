package be.kuleuven.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//import androidx.navigation.NavType;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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
import java.util.ArrayList;

public class Scanner extends AppCompatActivity {

    private TextView barcode;
    private SurfaceView cameraView;
    private CameraSource cameraSource;
    private TextView information;
    private Button process;
    private RequestQueue requestQueue;
    private Button submit;
    private Button scannedList;
    private TextView modeText;
    private ArrayList<Product> addNewProducts = new ArrayList<>();
    private ArrayList<Product> addInventoryProducts = new ArrayList<>();
    private ArrayList<Product> updateProducts = new ArrayList<>();
    private ArrayList<Product> removeProducts = new ArrayList<>();
    private ArrayList<Product> addGroceries = new ArrayList<>();
    private ArrayList scannedBarcodes = new ArrayList<>();
    private ArrayList<Long> allBarcodes = new ArrayList<>();
    private int mode;
    private int id;
    private int format = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        setContentView(R.layout.activity_scanner);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode",0);        //mode = 0 : add products, 1 : remove products, 2 : grocery list
        scannedBarcodes = intent.getIntegerArrayListExtra("barcodes");
        id = intent.getIntExtra("id",-1);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        barcode = (TextView) findViewById(R.id.txtContent);
        information = (TextView) findViewById(R.id.information);
        process = (Button) findViewById(R.id.process);
        submit = (Button) findViewById(R.id.submit);
        modeText = (TextView) findViewById(R.id.mode);
        scannedList = (Button) findViewById(R.id.scannedList);

        setMode();
        getAllBarcodes();

        process.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (barcode.getText().toString().equals("Barcode")){
                    Toast.makeText(Scanner.this, "No barcode was scanned", Toast.LENGTH_LONG).show();
                }else {
                    setData();
                    System.out.println(updateProducts);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(scannedBarcodes);
                for (Product product:addNewProducts) {
                    sendProducts(product.getBarcode(),product.getName().replace(" ", "+"),product.getPicture());
                    System.out.println(product.getBarcode() + product.getName() + product.getPicture());
                }
                for (Product product:addInventoryProducts) {
                    sendToInventory(product.getBarcode(),id,1);
                }
                for (Product product:updateProducts) {
                    updateInventory(product.getBarcode(),id,product.getQuantity());
                }

                addInventoryProducts.clear();
                addNewProducts.clear();
                updateProducts.clear();

            }
        });

        scannedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Scanner.this, ScannedProducts.class);
                intent.putParcelableArrayListExtra("new", addNewProducts);
                intent.putParcelableArrayListExtra("update", updateProducts);
                intent.putParcelableArrayListExtra("newInventory", addInventoryProducts);
                Scanner.this.startActivity(intent);
            }
        });

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.EAN_13).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).setAutoFocusEnabled(true).setRequestedFps(20.0f).build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(holder);

                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                    ie.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    barcode.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            barcode.setText(barcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });
    }

    private void setMode() {
        if (mode==0) modeText.setText("adding to inventory");
        if (mode==1) modeText.setText("removing from inventory");
        if (mode==2) modeText.setText("adding to grocerylist");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.addProducts:
                Toast.makeText(this, "Mode changed to add products", Toast.LENGTH_SHORT).show();
                mode = 0;
                setMode();
                break;

            case R.id.removeProducts:
                Toast.makeText(this, "Mode changed to remove products", Toast.LENGTH_SHORT).show();
                mode = 1;
                setMode();
                break;

            case R.id.addGroceries:
                Toast.makeText(this, "Mode changed to add groceries", Toast.LENGTH_SHORT).show();
                mode = 2;
                setMode();
                break;

            case R.id.EAN13:
                Toast.makeText(this, "Mode changed to EAN13", Toast.LENGTH_SHORT).show();
                format = 1;
                break;

            case R.id.EAN8:
                Toast.makeText(this, "Mode changed to EAN8", Toast.LENGTH_SHORT).show();
                format = 2;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {         //extra menu
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){       //back button working
        finish();
        return true;
    }

    public void getProductData(final long code){
        //todo meer parameters opvragen
        requestQueue = Volley.newRequestQueue(this);
        String url = "https://world.openfoodfacts.org/api/v0/product/"+code+".json?fields=brands,product_name";

        final JsonObjectRequest queueRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    for (int i = 0; i < 1; i++) {
                        System.out.println(response);
                        JSONObject object = response.getJSONObject("product");
                        try {
                            information.setText(String.format("%s %s", object.getString("product_name"), object.getString("brands")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                information.setText(object.getString("product_name"));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                        Product product = new Product(code,object.getString("product_name"));
                        addNewProducts.add(product);
                        addInventoryProducts.add(product);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Scanner.this, "Failed to retrieve product name", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Scanner.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(queueRequest);
    }

    public void sendProducts(long barcode, String name, URL picture) {

            final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/addProduct/" + barcode + "/" + name + "/" + picture;

            final StringRequest submitRequest = new StringRequest(Request.Method.GET, QUEUE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(Scanner.this, "All products added", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Scanner.this, "Unable to add products", Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(submitRequest);
    }
    public void sendToInventory(long barcode, int id, int quantity) {

        final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/addToInventory/" + id + "/" + barcode + "/" +quantity ;

        final StringRequest submitRequest = new StringRequest(Request.Method.GET, QUEUE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Scanner.this, "All products added to inventory", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Scanner.this, "Unable to add products", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }
    public void updateInventory(long barcode, int id, int quantity) {

        final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/updateInventory/"+ quantity + "/"+ quantity + "/"+ barcode + "/" + id;
        System.out.println(id+""+barcode);
        final StringRequest submitRequest = new StringRequest(Request.Method.GET, QUEUE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Scanner.this, "All products updated", Toast.LENGTH_SHORT).show();
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Scanner.this, "Unable to add products", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }

    public void getProduct(long barcode, int id) {

        requestQueue = Volley.newRequestQueue(this);
        String url = "https://studev.groept.be/api/a19sd303/getProduct/" + id + "/" + barcode;
        final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        information.setText(object.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Scanner.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();

                error.printStackTrace();
            }
        });
        requestQueue.add(queueRequest);
    }


    public void getAllBarcodes() {

        requestQueue = Volley.newRequestQueue(this);
        String url = "https://studev.groept.be/api/a19sd303/getAllBarcodes/";
        final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        allBarcodes.add(object.getLong("barcode"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Scanner.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();

                error.printStackTrace();
            }
        });
        requestQueue.add(queueRequest);
    }

    public void setData(){
        getProduct(Long.parseLong(barcode.getText().toString()),id);
        int quantity = 1;
        boolean value = false;
        Product product = new Product(Long.parseLong(barcode.getText().toString()),quantity);

        if (mode==0){
            if(scannedBarcodes.contains(Long.parseLong(barcode.getText().toString()))){
                for (Product update:updateProducts) {
                    if (update.getBarcode() == product.getBarcode()) {
                        update.setQuantityPlus();
                        value = true;
                    }
                }
                if (!value)updateProducts.add(product);
            }else {
                if (allBarcodes.contains(Long.parseLong(barcode.getText().toString()))){
                    addInventoryProducts.add(product);
                    scannedBarcodes.add(product.getBarcode());
                }else {
                    scannedBarcodes.add(product.getBarcode());
                    getProductData(Long.parseLong(barcode.getText().toString()));
                }
            }
        }
        if (mode==1){
            if(scannedBarcodes.contains(Long.parseLong(barcode.getText().toString()))){
                for (Product update:updateProducts) {
                    if (update.getBarcode() == product.getBarcode()) {
                        update.setQuantityMin();

                        value = true;
                    }
                }
                product.setQuantity(-1);
                if (!value)updateProducts.add(product);
            }else {
                Toast.makeText(Scanner.this, "This product was never scanned before.", Toast.LENGTH_LONG).show();
            }
        }
        if (mode==2){
            //todo nog te implementeren
        }
    }
}
