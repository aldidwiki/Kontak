package com.aldidwikip.kontak.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aldidwikip.kontak.R;
import com.aldidwikip.kontak.adapter.KontakAdapter;
import com.aldidwikip.kontak.listener.KontakListener;
import com.aldidwikip.kontak.model.GetKontak;
import com.aldidwikip.kontak.model.Kontak;
import com.aldidwikip.kontak.presenter.MainPresenter;
import com.aldidwikip.kontak.rest.ApiClient;
import com.aldidwikip.kontak.rest.ApiInterface;
import com.aldidwikip.kontak.utils.ConnectionError;
import com.aldidwikip.kontak.view.MainView;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.imaginativeworld.oopsnointernet.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.NoInternetDialog;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity implements MainView, KontakListener {
    FloatingActionButton btIns;
    ApiInterface mApiInterface;
    SwipeRefreshLayout swipeRefreshLayout;
    NoInternetDialog noInternetDialog;
    GetKontak mKontak;
    Boolean swipedHelper = TRUE;
    SearchView searchView;
    MenuItem searchItem;
    Fragment fragment;
    TextView tvNothingToShow;
    FragmentTransaction fragmentTransaction;
    boolean hasBackPressed;
    private RecyclerView mRecyclerView;
    private KontakAdapter mAdapter;
    private Skeleton skeleton;
    private MainPresenter presenter;
    private final static int INTENT_TO_INSERT = 10, INTENT_TO_EDIT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        presenter = new MainPresenter(this, this, mApiInterface);

        tvNothingToShow = findViewById(R.id.tvNothingtoShow);

        initRecyclerView();
        initSkeleton();
        initSwipeRefreshLayout();

        btIns = findViewById(R.id.btIns);
        btIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, InsertActivity.class), INTENT_TO_INSERT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_TO_INSERT || requestCode == INTENT_TO_EDIT) {
                assert data != null;
                hasBackPressed = data.getBooleanExtra("hasBackPressed", false);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int myId = android.os.Process.myPid();
        android.os.Process.killProcess(myId);
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refreshKontak();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimaryDark)
        );
    }

    private void initSkeleton() {
        skeleton = SkeletonLayoutUtils.applySkeleton(mRecyclerView, R.layout.kontak_list, 10);
        skeleton.setShimmerDurationInMillis(1000);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NoInternetDialog.Builder builder = new NoInternetDialog.Builder(this);
        builder.setConnectionCallback(new ConnectionCallback() {
            @Override
            public void hasActiveConnection(boolean b) {
//                initSkeleton();
            }
        });
        noInternetDialog = builder.build();
        if (!hasBackPressed) {
            presenter.refreshKontak();
        }
    }

    @Override
    protected void onPause() {
        if (noInternetDialog != null) {
            noInternetDialog.destroy();
        }
        super.onPause();
    }

    Kontak kontakPosition = null;
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return swipedHelper;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            kontakPosition = mKontak.getListDataKontak().get(position);
            final String delId = kontakPosition.getId();
            mKontak.getListDataKontak().remove(position);
            mAdapter.notifyItemRemoved(position);
            Snackbar snackbar = Snackbar.make(mRecyclerView, "Item Deleted", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mKontak.getListDataKontak().add(position, kontakPosition);
                    mAdapter.notifyItemInserted(position);
                }
            });
            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        presenter.deleteKontak(delId);
                    }
                }
            });
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.flat_red_1))
                    .addActionIcon(R.drawable.ic_delete_24dp)
                    .create()
                    .decorate();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchItem = menu.findItem(R.id.icon_search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Cari Nama");
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                swipedHelper = FALSE;
                mRecyclerView.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setEnabled(false);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        presenter.searchKontak(query);
                        swipeRefreshLayout.setEnabled(false);
                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        presenter.searchKontak(newText);
                        swipeRefreshLayout.setEnabled(false);
                        return true;
                    }
                });
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mRecyclerView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(true);
                swipedHelper = TRUE;
                presenter.refreshKontak();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void showConnectionError() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ConnectionError(), "CONNECTION ERROR")
                .commit();
        btIns.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void hideConnectionError() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment = getSupportFragmentManager().findFragmentByTag("CONNECTION ERROR");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
        btIns.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void showSkeleton() {
        skeleton.showSkeleton();
    }

    @Override
    public void hideSkeleton() {
        skeleton.showOriginal();
    }

    @Override
    public void onKontakLoaded(Context context, GetKontak getKontak) {
        mAdapter = new KontakAdapter(context, getKontak, this);
        mRecyclerView.setAdapter(mAdapter);
        this.mKontak = getKontak;

        if (!searchView.isIconified()) {
            searchItem.collapseActionView();
        }
    }

    @Override
    public void hideSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onKontakSearchResult(Context context, GetKontak getSearchResult) {
        mAdapter = new KontakAdapter(context, getSearchResult, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showRecyclerView() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showtvNothingToShow() {
        tvNothingToShow.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidetvNothingToShow() {
        tvNothingToShow.setVisibility(View.GONE);
    }

    @Override
    public void onKontakClicked(@NonNull Kontak kontak) {
        Intent intentToDetail = new Intent(MainActivity.this, EditActivity.class);
        intentToDetail.putExtra("Id", kontak.getId());
        intentToDetail.putExtra("Nama", kontak.getNama());
        intentToDetail.putExtra("Nomor", kontak.getNomor());
        intentToDetail.putExtra("Alamat", kontak.getAlamat());
        intentToDetail.putExtra("Avatar", kontak.getAvatar());
        startActivityForResult(intentToDetail, INTENT_TO_EDIT);
    }
}
