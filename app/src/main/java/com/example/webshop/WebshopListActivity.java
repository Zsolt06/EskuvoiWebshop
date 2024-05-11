package com.example.webshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class WebshopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = WebshopListActivity.class.getName();

    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<ShopingItem> mItemList;
    private ShopingItemAdapter mAdapter;
    private int gridNumber = 1;
    private boolean viewRow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webshop_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Autentikált felhasználó");
        } else {
            Log.d(LOG_TAG, "Nem autentikált felhasználó");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();
        mAdapter = new ShopingItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        initailizeData();
    }

    private void initailizeData() {
        String[] itemList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemDescription = getResources().getStringArray(R.array.shopping_item_desc);
        String[] itemPrice = getResources().getStringArray(R.array.shopping_item_price);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);

        mItemList.clear();

        for (int i = 0; i < itemList.length; i++) {
            mItemList.add(new ShopingItem(itemList[i], itemDescription[i], itemPrice[i], itemsImageResource.getResourceId(i, 0)));
        }

        itemsImageResource.recycle();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.webshop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            Log.d(LOG_TAG, "Felhasználó kijelentkezett!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (id == R.id.settings) {
            Log.d(LOG_TAG, "Beállítások menüpont kiválasztva!");
            return true;
        } else if (id == R.id.cart) {
            Log.d(LOG_TAG, "Kosár menüpont kiválasztva!");
            return true;
        } else if (id == R.id.view) {
            Log.d(LOG_TAG, "Nézet menüpont kiválasztva!");
            if (viewRow) {
                changeSpanCount(item, R.drawable.grid_view, 1);
            } else {
                changeSpanCount(item, R.drawable.view_row, 2);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }
}