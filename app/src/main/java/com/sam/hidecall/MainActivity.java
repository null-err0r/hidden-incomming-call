package com.sam.hidecall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    private static final int MY_REQUEST_CODE = 9999;
    ArrayList<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int accessPhone = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);

            int accessAlert = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW);

            permissions = new ArrayList();

            if (accessPhone == PackageManager.PERMISSION_DENIED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }

            if (accessAlert == PackageManager.PERMISSION_DENIED) {
                permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
            }

            if (permissions.size() > 0) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions.toArray(new String[permissions.size()]), 1);
            } else {
              //  Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
            }

        }

        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);

        if (policyManager.isAdminActive(componentName)) {
            finish();
        } else {
            activeManage();
        }
        setContentView(R.layout.main);
    }

    private void activeManage() {

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");

        startActivityForResult(intent, MY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            finish();
        } else {
            activeManage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
