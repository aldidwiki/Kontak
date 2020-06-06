package com.aldidwikip.kontak;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aldidwikip.kontak.Model.PostPutDelKontak;
import com.aldidwikip.kontak.Rest.ApiClient;
import com.aldidwikip.kontak.Rest.ApiInterface;
import com.aldidwikip.kontak.Utils.CustomBottomSheetDialog;
import com.aldidwikip.kontak.Utils.ShowLoadingAnimation;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertActivity extends AppCompatActivity implements CustomBottomSheetDialog.ItemClickListener {
    TextInputEditText edtNama, edtNomor, edtAlamat;
    CircleImageView avatarView;
    ApiInterface mApiInterface;
    String mediaPath;
    File file;
    FrameLayout flLoadingInsert;
    SpinKitView spinKitView;
    ShowLoadingAnimation showLoadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        edtNama = findViewById(R.id.edtNama);
        edtNomor = findViewById(R.id.edtNomor);
        edtAlamat = findViewById(R.id.edtAlamat);
        avatarView = findViewById(R.id.avatarView);
        flLoadingInsert = findViewById(R.id.flLoadingInsert);
        spinKitView = findViewById(R.id.spin_kit);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBottomSheetDialog bottomSheetDialog = CustomBottomSheetDialog.newInstance();
                bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet");
            }
        });

        showLoadingAnimation = new ShowLoadingAnimation(flLoadingInsert, spinKitView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            mediaPath = ImagePicker.Companion.getFilePath(data);
            Glide.with(this)
                    .load(data.getData())
                    .placeholder(R.drawable.ic_person_24dp)
                    .into(avatarView);
            Toast.makeText(getApplicationContext(), mediaPath, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idMenu = item.getItemId();
        switch (idMenu) {
            case R.id.icon_insert:
                InsertKontak();
                if (showLoadingAnimation.isEnabled()) {
                    item.setVisible(false);
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InsertKontak() {
        final String strAvatar;
        String strNama = edtNama.getText().toString();
        String strNomor = edtNomor.getText().toString();
        String strAlamat = edtAlamat.getText().toString();

        if (TextUtils.isEmpty(strNama)) {
            edtNama.setError("Column Can't be Empty");
        } else if (TextUtils.isEmpty(strNomor)) {
            edtNomor.setError("Column Can't be Empty");
        } else if (TextUtils.isEmpty(strAlamat)) {
            edtAlamat.setError("Column Can't be Empty");
        } else {
            showLoadingAnimation.show(true);

            if (mediaPath != null) {
                uploadImage();
                strAvatar = file.getName();
            } else {
                strAvatar = "";
            }
            Call<PostPutDelKontak> postKontakCall = mApiInterface.postKontak(strNama, strNomor, strAlamat, strAvatar);
            postKontakCall.enqueue(new Callback<PostPutDelKontak>() {
                @Override
                public void onResponse(Call<PostPutDelKontak> call, Response<PostPutDelKontak> response) {
                    Toast.makeText(getApplicationContext(), "Inserted", Toast.LENGTH_SHORT).show();
                    if (mediaPath == null) {
                        showLoadingAnimation.show(false);
                        MainActivity.ma.refresh();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<PostPutDelKontak> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error Insert", Toast.LENGTH_SHORT).show();
                    if (mediaPath == null) {
                        showLoadingAnimation.show(false);
                    }
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
                showLoadingAnimation.show(false);
                MainActivity.ma.refresh();
                finish();
            }

            @Override
            public void onFailure(Call<PostPutDelKontak> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                showLoadingAnimation.show(false);
            }
        });
    }

    @Override
    public void onItemClick(int item) {
        if (item == R.id.removePhoto) {
            Glide.with(this).clear(avatarView);
            mediaPath = null;
        }
    }
}
