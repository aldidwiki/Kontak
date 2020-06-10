package com.aldidwikip.kontak.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aldidwikip.kontak.R;
import com.aldidwikip.kontak.presenter.InsertPresenter;
import com.aldidwikip.kontak.rest.ApiClient;
import com.aldidwikip.kontak.rest.ApiInterface;
import com.aldidwikip.kontak.utils.CustomBottomSheetDialog;
import com.aldidwikip.kontak.utils.LoadingAnimation;
import com.aldidwikip.kontak.view.InsertView;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class InsertActivity extends AppCompatActivity implements CustomBottomSheetDialog.ItemClickListener, InsertView {
    TextInputEditText edtNama, edtNomor, edtAlamat;
    CircleImageView avatarView;
    ApiInterface mApiInterface;
    String mediaPath;
    FrameLayout flLoadingInsert;
    SpinKitView spinKitView;
    LoadingAnimation loadingAnimation;
    InsertPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        presenter = new InsertPresenter(this, this, mApiInterface);

        edtNama = findViewById(R.id.edtNama);
        edtNomor = findViewById(R.id.edtNomor);
        edtAlamat = findViewById(R.id.edtAlamat);
        avatarView = findViewById(R.id.avatarView);
        flLoadingInsert = findViewById(R.id.flLoadingInsert);
        spinKitView = findViewById(R.id.spin_kit);

        loadingAnimation = new LoadingAnimation(flLoadingInsert, spinKitView);

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBottomSheetDialog bottomSheetDialog = CustomBottomSheetDialog.newInstance();
                bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet");
            }
        });
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
                insertKontak();
                if (loadingAnimation.isEnabled()) {
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

    private void insertKontak() {
        String strNama = Objects.requireNonNull(edtNama.getText()).toString();
        String strNomor = Objects.requireNonNull(edtNomor.getText()).toString();
        String strAlamat = Objects.requireNonNull(edtAlamat.getText()).toString();

        if (TextUtils.isEmpty(strNama)) {
            edtNama.setError("Column Can't be Empty");
        } else if (TextUtils.isEmpty(strNomor)) {
            edtNomor.setError("Column Can't be Empty");
        } else if (TextUtils.isEmpty(strAlamat)) {
            edtAlamat.setError("Column Can't be Empty");
        } else {
            presenter.insertKontak(strNama, strNomor, strAlamat, mediaPath);
        }
    }

    @Override
    public void onItemClick(int item) {
        if (item == R.id.removePhoto) {
            Glide.with(this).clear(avatarView);
            mediaPath = null;
        }
    }

    @Override
    public void showLoadingAnimation() {
        loadingAnimation.show(true);
    }

    @Override
    public void hideLoadingAnimation() {
        loadingAnimation.show(false);
    }

    @Override
    public void isInsertResponsed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("hasBackPressed", false);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void isUploadResponsed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("hasBackPressed", false);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("hasBackPressed", true);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
