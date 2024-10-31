package com.example.kasapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Koneksi extends SQLiteOpenHelper {


    public Koneksi(@Nullable Context context) {
        super(context, "kasApp", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table transaksi (transaksi_id integer primary key autoincrement, status text, jumlah long, keterangan text, tanggal date default current_date)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
