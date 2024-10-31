package com.example.kasapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddActivity extends AppCompatActivity {
    EditText edtJumlah, edtKeterangan;
    RadioGroup radioStatus;
    Button btnSimpan;
    Koneksi koneksi;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtJumlah = findViewById(R.id.edtJumlah);
        edtKeterangan = findViewById(R.id.edtKeterangan);
        btnSimpan = findViewById(R.id.btnSimpan);
        radioStatus = findViewById(R.id.radioStatus);
        status = "";
        koneksi = new Koneksi(this);
        radioStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMasuk) status = "MASUK";
            else if (checkedId == R.id.radioKeluar) status = "KELUAR";
        });

        btnSimpan.setOnClickListener(v -> {
                    if (status.isEmpty()) {
                        Toast.makeText(this, "Status Belum dipilih", Toast.LENGTH_SHORT).show();
                    } else if (edtJumlah.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Jumlah Belum diisi", Toast.LENGTH_SHORT).show();
                    } else if (edtKeterangan.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Keterangan Belum diisi", Toast.LENGTH_SHORT).show();
                    } else {
                        simpanData();
                    }

                });
    }



    private void simpanData() {
        SQLiteDatabase db = koneksi.getWritableDatabase();
        String querySQL = "insert into transaksi (status, jumlah, keterangan)" +
                " values ('" + status + "', '" + edtJumlah.getText().toString() + "'" +
                " , '" + edtKeterangan.getText().toString() + "')";
        db.execSQL(querySQL);
        Toast.makeText(this, "Data Berhasil disimpan", Toast.LENGTH_SHORT).show();
        finish();
    }
}
