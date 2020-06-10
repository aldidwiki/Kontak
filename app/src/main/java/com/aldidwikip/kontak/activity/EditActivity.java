package com.aldidwikip.kontak.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aldidwikip.kontak.R;
import com.aldidwikip.kontak.presenter.EditPresenter;
import com.aldidwikip.kontak.rest.ApiClient;
import com.aldidwikip.kontak.rest.ApiInterface;
import com.aldidwikip.kontak.utils.CustomBottomSheetDialog;
import com.aldidwikip.kontak.utils.LoadingAnimation;
import com.aldidwikip.kontak.view.EditView;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class EditActivity extends AppCompatActivity implements CustomBottomSheetDialog.ItemClickListener, EditView {
    TextInputEditText edtNama, edtNomor, edtAlamat;
    CircleImageView avatarView;
    ApiInterface mApiInterface;
    String mediaPath, Id;
    Intent mIntent;
    FrameLayout flLoadingEdit;
    SpinKitView spinKitView;
    LoadingAnimation loadingAnimation;
    private Boolean imgRemoved = FALSE;
    private EditPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        presenter = new EditPresenter(this, this, mApiInterface);

        edtNama = findViewById(R.id.edtNama);
        edtNomor = findViewById(R.id.edtNomor);
        edtAlamat = findViewById(R.id.edtAlamat);
        avatarView = findViewById(R.id.avatarView);
        flLoadingEdit = findViewById(R.id.flLoadingEdit);
        spinKitView = findViewById(R.id.spin_kit);

        loadingAnimation = new LoadingAnimation(flLoadingEdit, spinKitView);

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
                editKontak();
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

    private void editKontak() {
        String strNama = Objects.requireNonNull(edtNama.getText()).toString();
        String strNomor = Objects.requireNonNull(edtNomor.getText()).toString();
        String strAlamat = Objects.requireNonNull(edtAlamat.getText()).toString();
        String fieldAvatar = mIntent.getStringExtra("Avatar");

        if (TextUtils.isEmpty(strNama)) {
            edtNama.setError("Column Can't be Empty");
        } else if (TextUtils.isEmpty(strNomor)) {
            edtNomor.setError("Column Can't be Empty");
        } else if (TextUtils.isEmpty(strAlamat)) {
            edtAlamat.setError("Column Can't be Empty");
        } else {
            presenter.editKontak(Id, strNama, strNomor, strAlamat, fieldAvatar, imgRemoved, mediaPath);
        }
    }

    @Override
    public void onItemClick(int item) {
        if (item == R.id.removePhoto) {
            Glide.with(this).clear(avatarView);
            mediaPath = null;
            imgRemoved = TRUE;
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
    public void isEditResponsed() {
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