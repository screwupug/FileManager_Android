package com.griga.filemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.no_permission);
        button = findViewById(R.id.request_permission);
        button.setOnClickListener(view -> makeAlertDialog());
        if (checkPermission()) {
            makeAlertDialog();
        } else {
            openMainFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    private void makeAlertDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("ОК", (dialogInterface, i) -> requestPermission())
                .setNegativeButton("НЕТ", (dialogInterface, i) -> showRequestPermissionButton())
                .setView(R.layout.alert_dialog_permission)
                .show();

    }

    private void showRequestPermissionButton() {
        if (checkPermission()) {
            textView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 30 and above
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            // below 30
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1);
        }
    }

    private final ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            showRequestPermissionButton();
                            openMainFragment();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Permission required", Toast.LENGTH_SHORT).show();
                        textView.setVisibility(View.VISIBLE);
                        button.setVisibility(View.VISIBLE);
                    }
                }
            }
    );

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return !Environment.isExternalStorageManager();
        } else {
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return read != PackageManager.PERMISSION_GRANTED;
        }
    }

    private void openMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main, new FilesManagerList(), null)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showRequestPermissionButton();
                openMainFragment();
            } else {
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
            }
        }
    }
}