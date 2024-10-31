package com.example.kasapp;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity {
    EditText edtJumlah, edtKeterangan, edtTanggal;
    RadioGroup radioStatus;
    RadioButton radioMasuk, radioKeluar;
    String status;
    Koneksi koneksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        koneksi = new Koneksi(this);

        radioMasuk = findViewById(R.id.radioMasuk);
        radioKeluar = findViewById(R.id.radioKeluar);
        edtJumlah = findViewById(R.id.edtJumlah);
        edtKeterangan = findViewById(R.id.edtKeterangan);
        edtTanggal = findViewById(R.id.edtTanggal);
        radioStatus = findViewById(R.id.radioStatus);

        radioStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMasuk) status = "MASUK";
            else if (checkedId == R.id.radioKeluar) status = "KELUAR";
        });

        init();

        edtTanggal.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int tahun = calendar.get(Calendar.YEAR);
            int bulan = calendar.get(Calendar.MONTH);
            int tanggal = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog date_picker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                edtTanggal.setText(year + "/" + (month + 1) + "/" + dayOfMonth);
            }, tahun, bulan, tanggal);
            date_picker.show();
        });

        findViewById(R.id.btnUbah).setOnClickListener(v -> updateData());
    }

    public void updateData() {
        if (status.isEmpty()) {
            showToast("Pilih Status!!");
        } else if (edtJumlah.getText().toString().isEmpty()) {
            showToast("Isi Nilai");
        } else if (edtKeterangan.getText().toString().isEmpty()) {
            showToast("Isi Keterangan");
        } else if (edtTanggal.getText().toString().isEmpty()) {
            showToast("Isi Tanggal");
        } else {
            SQLiteDatabase db = koneksi.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("status", status);
            values.put("jumlah", edtJumlah.getText().toString());
            values.put("keterangan", edtKeterangan.getText().toString());
            values.put("tanggal", edtTanggal.getText().toString());

            int rowsUpdated = db.update("transaksi", values, "transaksi_id = ?", new String[]{MainActivity.transaksi_id});

            if (rowsUpdated > 0) {
                showToast("Data berhasil diubah");
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                showToast("Gagal mengubah data");
            }
        }
    }

    public void init() {
        SQLiteDatabase db = koneksi.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transaksi WHERE transaksi_id = '" + MainActivity.transaksi_id + "'", null);

        if (cursor != null && cursor.moveToFirst()) {
            status = cursor.getString(1);
            if (status.equals("MASUK")) {
                radioMasuk.setChecked(true);
            } else {
                radioKeluar.setChecked(true);
            }

            edtJumlah.setText(cursor.getString(2));
            edtKeterangan.setText(cursor.getString(3));
            edtTanggal.setText(cursor.getString(4));

            cursor.close();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
