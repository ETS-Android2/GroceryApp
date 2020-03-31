package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
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
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Scanner extends AppCompatActivity {

    private TextView barcode;
    private SurfaceView cameraView;
    private CameraSource cameraSource;
    private TextView information;
    private Button process;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        barcode = (TextView) findViewById(R.id.txtContent);
        information = (TextView) findViewById(R.id.information);
        process = (Button) findViewById(R.id.process);

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.EAN_13).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).setAutoFocusEnabled(true).build();

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

    public void onClick(View view){
       getProductData(barcode.getText().toString());

    }

    public void getProductData(final String code){

        requestQueue = Volley.newRequestQueue(this);
        String url = "https://world.openfoodfacts.org/api/v0/product/"+code+".json?fields=brands,product_name";

        final JsonObjectRequest queueRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
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