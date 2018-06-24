package com.llawl.tristonpang.intheloop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannerActivity extends AppCompatActivity {

    private Button scan_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scan_button = (Button) findViewById(R.id.scan_button);
        final Activity activity = this;
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scanning cancelled", Toast.LENGTH_LONG).show();
            } else {
                String resultValue = result.getContents();
                Log.d("InTheLoop", "ScannerActivity result: " + resultValue);
                //Toast.makeText(this, resultValue, Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT);

                //pass back to MyInfoActivity
                Intent intent = new Intent();
                intent.putExtra("scanValue", resultValue);
                setResult(RESULT_OK, intent);
                finish();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}