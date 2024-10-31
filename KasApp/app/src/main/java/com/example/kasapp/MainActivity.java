package com.example.kasapp;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btnTambah;
    TextView textMasuk, textKeluar, textSaldo;
    ListView listTransaksi;
    String queryKas, queryTotal;
    Koneksi koneksi;
    Cursor cursor;
    ArrayList<HashMap<String, String>> arusKas = new ArrayList<HashMap<String, String>>();
    public static String transaksi_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textMasuk = findViewById(R.id.textMasuk);
        textKeluar = findViewById(R.id.textKeluar);
        textSaldo = findViewById(R.id.textSaldo);
        listTransaksi = findViewById(R.id.listTransaksi);
        koneksi = new Koneksi(this);
        queryKas = "";
        queryTotal = "";
        btnTambah = findViewById(R.id.btnTambah);
        btnTambah.setOnClickListener(v -> {
            startActivity(new Intent(this, AddActivity.class));
        });
        tampilKas();

    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilKas();
    }

    private void tampilKas() {
        arusKas.clear();
        listTransaksi.setAdapter(null);
        SQLiteDatabase db = koneksi.getReadableDatabase();
        queryKas = "select * from transaksi";
        cursor = db.rawQuery(queryKas, null);
        int i;
        for (i=0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            HashMap<String, String> map = new HashMap<String, String >();
            map.put("transaksi_id", cursor.getString(0));
            map.put("status", cursor.getString(1));
            map.put("jumlah", cursor.getString(2));
            map.put("keterangan", cursor.getString(3));
            map.put("tanggal", cursor.getString(4));
            arusKas.add(map);
        }

        SimpleAdapter simple = new SimpleAdapter(this, arusKas, R.layout.list_item,
                new String[] {"transaksi_id","status" ,"jumlah","keterangan","tanggal"},
                new int[] {R.id.textID, R.id.textStatus, R.id.textJumlah, R.id.textKeterangan, R.id.textTanggal});
        listTransaksi.setAdapter(simple);

        listTransaksi.setOnItemClickListener((parent, view, position, id) -> {
            transaksi_id = ((TextView)view.findViewById(R.id.textID)).getText().toString();


//            Toast.makeText(this, "Transaksi ID: " + transaksi_id, Toast.LENGTH_SHORT).show();
        tampilkanMenu();

       });
        tampilTotal();
    }

    private void tampilkanMenu() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.list_menu);
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        dialog.show();
        TextView textMenuUbah = dialog.findViewById(R.id.textMenuUbah);
        TextView textMenuHapus = dialog.findViewById(R.id.textMenuHapus);
        textMenuHapus.setOnClickListener(v -> {

            dialog.dismiss();
            hapusData();
        });
        textMenuUbah.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(this, EditActivity.class));
        });
    }

    private void hapusData() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Hapus Data?");
        alert.setMessage("Yakin mau hapus data ini?");
        alert.setPositiveButton("Ya", (dialog, which) -> {
            SQLiteDatabase db = koneksi.getWritableDatabase();
            db.execSQL("delete from transaksi where transaksi_id = '" + transaksi_id + "'");
            Toast.makeText(this, "Data transaksi yang dipilih berhsil dihapus!",
                    Toast.LENGTH_SHORT).show();
            tampilKas();
        });
        alert.setNegativeButton("tidak",(dialog, which) -> {});
        alert.show();
    }


    private void tampilTotal() {
        SQLiteDatabase db = koneksi.getReadableDatabase();
        queryTotal = "select sum(jumlah), " +
                "(select sum(jumlah) from transaksi where status='MASUK') as jlhMasuk, " +
                "(select sum(jumlah) from transaksi where status='KELUAR') as jlhKeluar" +
                " from transaksi";
        cursor = db.rawQuery(queryTotal, null);
        cursor.moveToFirst();
        textMasuk.setText(String.valueOf(cursor.getDouble(1)));
        textKeluar.setText(String.valueOf(cursor.getDouble(2)));
        double saldo = cursor.getDouble(1) - cursor.getDouble(2);
        textSaldo.setText(String.valueOf(saldo));
    }
}