package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.sql.SQLOutput;
import java.util.ArrayList;

public class Scanner extends AppCompatActivity {

    private TextView barcode;
    private SurfaceView cameraView;
    private CameraSource cameraSource;
    private TextView information;
    private Button process;
    private RequestQueue requestQueue;
    private Button submit;
    private TextView modeText;
    private ArrayList<Product> scannedProducts = new ArrayList<>();
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        setContentView(R.layout.activity_scanner);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode",0);        //mode = 0 : add products, 1 : remove products, 2 : grocery list

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        barcode = (TextView) findViewById(R.id.txtContent);
        information = (TextView) findViewById(R.id.information);
        process = (Button) findViewById(R.id.process);
        submit = (Button) findViewById(R.id.submit);
        modeText = (TextView) findViewById(R.id.mode);

        if (mode==0) modeText.setText("adding to inventory");
        if (mode==1) modeText.setText("removing from inventory");
        if (mode==2) modeText.setText("adding to grocerylist");

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barcode.getText().toString().equals("Barcode")){
                    Toast.makeText(Scanner.this, "No barcode was scanned", Toast.LENGTH_LONG).show();
                }else {
                    getProductData(Long.parseLong(barcode.getText().toString()));
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (Product product:scannedProducts) {
                    sendProducts(product.getBarcode(),product.getName().replace(" ", "+"),product.getPicture());
                    System.out.println(product.getBarcode() + product.getName() + product.getPicture());
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void getProductData(final long code){

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
                            information.setText(String.format("%s%s", object.getString("product_name"), object.getString("brands")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                information.setText(object.getString("product_name"));
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                        Product product = new Product(code,object.getString("product_name"));
                        scannedProducts.add(product);
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
}

/*
    public void getProductData(final String code){
        requestQueue = Volley.newRequestQueue(this);

        String url = "https://world.openfoodfacts.org/api/v0/product/"+code+".json?fields=product_name";
        // String url = "https://studev.groept.be/api/a19sd303/test";
        final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET,url,null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);

                        information.setText(object.getString("product_name"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    information.setText("1");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Scanner.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
                //information.setText(error.toString());
                error.printStackTrace();
                System.out.println(error);
            }
        });
        requestQueue.add(queueRequest);


 */