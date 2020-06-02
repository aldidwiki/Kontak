package com.aldidwikip.kontak;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aldidwikip.kontak.Model.PostPutDelKontak;
import com.aldidwikip.kontak.Rest.ApiClient;
import com.aldidwikip.kontak.Rest.ApiInterface;
import com.aldidwikip.kontak.Utils.CustomBottomSheetDialog;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class EditActivity extends AppCompatActivity implements CustomBottomSheetDialog.ItemClickListener {
    TextInputEditText edtNama, edtNomor, edtAlamat;
    CircleImageView avatarView;
    ApiInterface mApiInterface;
    String mediaPath, Id;
    Intent mIntent;
    File file;
    FrameLayout flLoadingEdit;
    SpinKitView spinKitView;
    private Boolean imgRemoved = FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
                .placeholder(R.drawable.ic_person_24dp)
                .into(avatarView);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBottomSheetDialog bottomSheetDialog = CustomBottomSheetDialog.newInstance();
                bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            mediaPath = ImagePicker.Companion.getFilePath(data);
            Glide.with(this)
                    .load(data.getData())
                    .placeholder(R.drawable.ic_person_24dp)
                    .into(avatarView);
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
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void EditKontak() {
        String strAvatar;
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
            showLoadingAnimation();

            if (mediaPath != null) {
                uploadImage();
                strAvatar = file.getName();
            } else if (imgRemoved) {
                strAvatar = "Image Removed";
            } else {
                String fieldAvatar = mIntent.getStringExtra("Avatar");
                strAvatar = fieldAvatar.substring(fieldAvatar.lastIndexOf("/") + 1);
            }
            Call<PostPutDelKontak> updateKontakCall = mApiInterface.putKontak(Id, strNama, strNomor, strAlamat, strAvatar);
            updateKontakCall.enqueue(new Callback<PostPutDelKontak>() {
                @Override
                public void onResponse(Call<PostPutDelKontak> call, Response<PostPutDelKontak> response) {
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    if (mediaPath == null || imgRemoved) {
                        hideLoadingAnimation();
                        MainActivity.ma.refresh();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<PostPutDelKontak> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error Update", Toast.LENGTH_SHORT).show();
                    if (mediaPath == null || imgRemoved) {
                        hideLoadingAnimation();
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
                hideLoadingAnimation();
                MainActivity.ma.refresh();
                finish();
            }

            @Override
            public void onFailure(Call<PostPutDelKontak> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                hideLoadingAnimation();
            }
        });
    }

    @Override
    public void onItemClick(int item) {
        if (item == R.id.removePhoto) {
            Glide.with(this).clear(avatarView);
            mediaPath = null;
            imgRemoved = TRUE;
        }
    }

    private void initLoadingAnimation() {
        flLoadingEdit = findViewById(R.id.flLoadingEdit);
        spinKitView = findViewById(R.id.spin_kit);
        ProgressBar progressBar = spinKitView;
        Sprite doubleBounce = new Circle();
        progressBar.setIndeterminateDrawable(doubleBounce);
    }

    private void showLoadingAnimation() {
        initLoadingAnimation();
        flLoadingEdit.setVisibility(View.VISIBLE);
        spinKitView.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        initLoadingAnimation();
        flLoadingEdit.setVisibility(View.GONE);
        spinKitView.setVisibility(View.GONE);
    }
}