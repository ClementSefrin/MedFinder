package iut.dam.sae_dam.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import iut.dam.sae_dam.R;


public class ScannerActivity extends AppCompatActivity {
    public interface OnDataMatrixDetectedListener {
        void onDataMatrixDetected(String data);
    }

    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private final int REQUEST_CODE_CAMERA_PERMISSION = 100;
    private OnDataMatrixDetectedListener onDataMatrixDetectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_camera);
        surfaceView = findViewById(R.id.scanner_cameraView);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            startCamera();
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
    }

    private void startCamera() {
        if (barcodeDetector == null) {
            barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.DATA_MATRIX)
                    .build();
        }

        if (cameraSource == null) {
            cameraSource = new CameraSource.Builder(this, barcodeDetector)
                    .setAutoFocusEnabled(true)
                    .setRequestedPreviewSize(640, 480)
                    .build();
        }

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(holder);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (cameraSource != null) {
                    cameraSource.stop();
                }
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes != null && barcodes.size() > 0) {
                    Barcode barcode = barcodes.valueAt(0);
                    if (barcode.format == Barcode.DATA_MATRIX) {
                        final String data = barcode.rawValue;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayData(data);
                            }
                        });
                    }
                }
            }
        });
    }

    private void displayData(String data) {
        cameraSource.stop();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dataMatrix", data);
        Bundle bundle = new Bundle();
        bundle.putString("dataMatrix", data);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Permission de la caméra non accordée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    public void setOnDataMatrixDetectedListener(OnDataMatrixDetectedListener listener) {
        this.onDataMatrixDetectedListener = listener;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }


}

