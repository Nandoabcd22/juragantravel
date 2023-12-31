package com.nando.juragantravel;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.widget.Toast;
import android.text.InputType;
import android.content.SharedPreferences;
import android.content.Context;
import android.text.TextUtils;

public class RegisterJamaahActivity extends AppCompatActivity {

    private EditText namalengkapEditText, emailEditText, nikEditText, nohpEditText, alamatEditText, usernameEditText, radioGroup;
    private Spinner genderSpinner;
    private DatabaseHelper databaseHelper;
    private boolean isNikVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_jamaah);

        // Inisialisasi komponen EditText
        namalengkapEditText = findViewById(R.id.namalengkap);
        emailEditText = findViewById(R.id.email);
        nikEditText = findViewById(R.id.nik);
        nohpEditText = findViewById(R.id.nohp);
        alamatEditText = findViewById(R.id.alamat);
        usernameEditText = findViewById(R.id.username);

        // Inisialisasi Spinner
        genderSpinner = findViewById(R.id.gender);

        // Inisialisasi database helper
        databaseHelper = new DatabaseHelper(this);

        // Pilihan jenis kelamin
        String[] genders = {"Laki-laki", "Perempuan"};

        // Buat adapter untuk Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter ke Spinner
        genderSpinner.setAdapter(adapter);

        // Tambahkan listener untuk tombol "Daftar"
        Button signupButton = findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mengambil data dari formulir
                String fullName = namalengkapEditText.getText().toString();
                saveFullName(fullName); // Simpan Nama Lengkap ke SharedPreferences

                String email = emailEditText.getText().toString();
                String nik = nikEditText.getText().toString();
                String phone = nohpEditText.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String address = alamatEditText.getText().toString();
                String username = usernameEditText.getText().toString();

                boolean isValid = true;
                if (fullName.isEmpty()) {
                    namalengkapEditText.setError("Nama lengkap harus diisi");
                    isValid = false;
                }
                if (email.isEmpty()) {
                    emailEditText.setError("Email harus diisi");
                    isValid = false;
                }
                if (nik.isEmpty()) {
                    nikEditText.setError("NIK harus diisi");
                    isValid = false;
                } else if (nik.length() != 16) {
                    nikEditText.setError("NIK harus terdiri dari 16 angka");
                    isValid = false;
                }
                if (phone.isEmpty()) {
                    nohpEditText.setError("Nomor HP harus diisi");
                    isValid = false;
                }
                if (address.isEmpty()) {
                    alamatEditText.setError("Alamat harus diisi");
                    isValid = false;
                }
                if (username.isEmpty()) {
                    usernameEditText.setError("Username harus diisi");
                    isValid = false;
                }

                if (isValid) {
                    // Simpan data ke SQLite
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_FULL_NAME, fullName);
                    values.put(DatabaseHelper.COLUMN_EMAIL, email);
                    values.put(DatabaseHelper.COLUMN_NIK, nik);
                    values.put(DatabaseHelper.COLUMN_PHONE, phone);
                    values.put(DatabaseHelper.COLUMN_GENDER, gender);
                    values.put(DatabaseHelper.COLUMN_ADDRESS, address);
                    values.put(DatabaseHelper.COLUMN_USERNAME, username);

                    long newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values);

                    if (newRowId != -1) {
                        // Data berhasil disimpan
                        Toast.makeText(RegisterJamaahActivity.this, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();
                        // Navigasi ke aktivitas LogJamaahActivity
                        Intent intent = new Intent(RegisterJamaahActivity.this, LogJamaahActivity.class);
                        startActivity(intent);
                    } else {
                        // Gagal mendaftar
                        Toast.makeText(RegisterJamaahActivity.this, "Gagal mendaftar", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Menambahkan onTouchListener untuk ikon Show/Hide NIK
        nikEditText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (nikEditText.getRight() - nikEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        toggleNikVisibility(nikEditText);
                        return true;
                    }
                }
                return false;
            }
        });

        // Memeriksa apakah ada Nama Lengkap yang disimpan di SharedPreferences
        String savedFullName = getSavedFullName();
        if (!TextUtils.isEmpty(savedFullName)) {
            namalengkapEditText.setText(savedFullName);
        }
    }

    // Metode untuk mengganti visibilitas NIK (terlihat atau tersembunyi)
    private void toggleNikVisibility(EditText editText) {
        if (isNikVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
        }

        // Memperbarui status visibilitas
        isNikVisible = !isNikVisible;
    }

    // Metode untuk menyimpan Nama Lengkap ke SharedPreferences
    private void saveFullName(String fullName) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fullName", fullName);
        editor.apply();
    }

    // Metode untuk mengambil Nama Lengkap dari SharedPreferences
    private String getSavedFullName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("fullName", "");
    }
}
