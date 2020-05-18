package com.aldidwikip.kontak;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aldidwikip.kontak.Model.PostPutDelKontak;
import com.aldidwikip.kontak.Rest.ApiClient;
import com.aldidwikip.kontak.Rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditActivity extends AppCompatActivity {
    TextInputEditText edtNama, edtNomor, edtAlamat;
    CircleImageView avatarView;
    Button btDelete;
    ApiInterface mApiInterface;
    String mediaPath, Id;
    Intent mIntent;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_edit);

        edtNama = findViewById(R.id.edtNama);
        edtNomor = findViewById(R.id.edtNomor);
        edtAlamat = findViewById(R.id.edtAlamat);
        avatarView = findViewById(R.id.avatarView);

        mIntent = getIntent();
        Id = mIntent.getStringExtra("Id");
        edtNama.setText(mIntent.getStringExtra("Nama"));
        edtNomor.setText(mIntent.getStringExtra("Nomor"));
        edtAlamat.setText(mIntent.getStringExtra("Alamat"));
        Glide
                .with(this)
                .load(mIntent.getStringExtra("Avatar"))
                .error(R.drawable.ic_person_black_24dp)
                .into(avatarView);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        btDelete = findViewById(R.id.btDelete2);
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<PostPutDelKontak> deleteKontak = mApiInterface.deleteKontak(Id);
                deleteKontak.enqueue(new Callback<PostPutDelKontak>() {
                    @Override
                    public void onResponse(Call<PostPutDelKontak> call, Response<PostPutDelKontak> response) {
                        MainActivity.ma.refresh();
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<PostPutDelKontak> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error Delete", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(EditActivity.this)
                        .galleryOnly()
                        .compress(1024)
                        .start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            mediaPath = ImagePicker.Companion.getFilePath(data);
            Glide.with(this).load(data.getData()).into(avatarView);
            Toast.makeText(this, mediaPath, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int IdMenu = item.getItemId();
        switch (IdMenu) {
            case R.id.icon_save:
                EditKontak();
                break;
            case android.R.id.home:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void EditKontak() {
        String strAvatar;
        String strNama = edtNama.getText().toString();
        String strNomor = edtNomor.getText().toString();
        String strAlamat = edtAlamat.getText().toString();

        if (mediaPath != null) {
            uploadImage();
            strAvatar = file.getName();
        } else {
            String fieldAvatar = mIntent.getStringExtra("Avatar");
            strAvatar = fieldAvatar.substring(fieldAvatar.lastIndexOf("/") + 1);
        }

        if (TextUtils.isEmpty(strNama)) {
            edtNama.setError("Column Can't be Empty");
        } else if (TextUtils.isEmpty(strNomor)) {
            edtNomor.setError("Column Can't be Empty");
        } else if (TextUtils.isEmpty(strAlamat)) {
            edtAlamat.setError("Column Can't be Empty");
        } else {
            Call<PostPutDelKontak> updateKontakCall = mApiInterface.putKontak(Id, strNama, strNomor, strAlamat, strAvatar);
            updateKontakCall.enqueue(new Callback<PostPutDelKontak>() {
                @Override
                public void onResponse(Call<PostPutDelKontak> call, Response<PostPutDelKontak> response) {
                    MainActivity.ma.refresh();
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<PostPutDelKontak> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error Update", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void uploadImage() {
        file = new File(mediaPath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part imageToUpload = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);

        Call<PostPutDelKontak> call = mApiInterface.uploadFile(imageToUpload);
        call.enqueue(new Callback<PostPutDelKontak>() {
            @Override
            public void onResponse(Call<PostPutDelKontak> call, Response<PostPutDelKontak> response) {
                Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<PostPutDelKontak> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}