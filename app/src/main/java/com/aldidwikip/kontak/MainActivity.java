package com.aldidwikip.kontak;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aldidwikip.kontak.Adapter.KontakAdapter;
import com.aldidwikip.kontak.Model.GetKontak;
import com.aldidwikip.kontak.Model.Kontak;
import com.aldidwikip.kontak.Model.PostPutDelKontak;
import com.aldidwikip.kontak.Rest.ApiClient;
import com.aldidwikip.kontak.Rest.ApiInterface;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.imaginativeworld.oopsnointernet.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.NoInternetDialog;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btIns;
    ApiInterface mApiInterface;
    SwipeRefreshLayout swipeRefreshLayout;
    NoInternetDialog noInternetDialog;
    List<Kontak> KontakList, searchList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static MainActivity ma;
    private Skeleton skeleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        initRecyclerView();
        initSkeleton();
        initSwipeRefreshLayout();
        refresh();

        btIns = findViewById(R.id.btIns);
        btIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InsertActivity.class));
            }
        });

        ma = this;
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary)
        );
    }

    private void initSkeleton() {
        skeleton = SkeletonLayoutUtils.applySkeleton(mRecyclerView, R.layout.kontak_list, 10);
        skeleton.setShimmerDurationInMillis(1000);
        skeleton.showSkeleton();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NoInternetDialog.Builder builder = new NoInternetDialog.Builder(this);
        builder.setConnectionCallback(new ConnectionCallback() {
            @Override
            public void hasActiveConnection(boolean b) {

            }
        });
        noInternetDialog = builder.build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noInternetDialog != null) {
            noInternetDialog.destroy();
        }
    }

    public void refresh() {
        Call<GetKontak> kontakCall = mApiInterface.getKontak();
        kontakCall.enqueue(new Callback<GetKontak>() {
            @Override
            public void onResponse(@NonNull Call<GetKontak> call, @NonNull Response<GetKontak> response) {
                KontakList = response.body().getListDataKontak();
                Log.d("Retrofit Get", "Jumlah data Kontak: " + KontakList.size());
                skeleton.showOriginal();
                mAdapter = new KontakAdapter(ma, KontakList);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onFailure(Call<GetKontak> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    Kontak kontakPosition = null;
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            kontakPosition = KontakList.get(position);
            final String delId = kontakPosition.getId();
            KontakList.remove(position);
            mAdapter.notifyItemRemoved(position);
            Snackbar snackbar = Snackbar.make(mRecyclerView, "Item Deleted", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KontakList.add(position, kontakPosition);
                    mAdapter.notifyItemInserted(position);
                }
            });
            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        deleteKontak(delId);
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

    public void deleteKontak(String delId) {
        Call<PostPutDelKontak> deleteKontak = mApiInterface.deleteKontak(delId);
        deleteKontak.enqueue(new Callback<PostPutDelKontak>() {
            @Override
            public void onResponse(@NonNull Call<PostPutDelKontak> call, @NonNull Response<PostPutDelKontak> response) {
                Toast.makeText(getApplicationContext(), "Deleted from Database", Toast.LENGTH_SHORT).show();
                Log.d("Delete Kontak", "onResponse: Deleted from Database");
            }

            @Override
            public void onFailure(@NonNull Call<PostPutDelKontak> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Error Delete", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Cari Nama");
        searchView.setIconified(false);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchKontak(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchKontak(newText);
                        return true;
                    }
                });
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                refresh();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void searchKontak(String keyword) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        Call<GetKontak> callSearch = mApiInterface.searchKontak(keyword);
        callSearch.enqueue(new Callback<GetKontak>() {
            @Override
            public void onResponse(@NonNull Call<GetKontak> call, @NonNull Response<GetKontak> response) {
                assert response.body() != null;
                mRecyclerView.setVisibility(View.VISIBLE);
                searchList = response.body().getListDataKontak();
                mAdapter = new KontakAdapter(ma, searchList);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<GetKontak> call, Throwable t) {
            }
        });
    }
}
