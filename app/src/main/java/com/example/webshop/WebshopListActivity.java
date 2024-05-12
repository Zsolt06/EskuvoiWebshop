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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class WebshopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = WebshopListActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<ShopingItem> mItemList;
    private ShopingItemAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private int gridNumber = 1;
    private boolean viewRow = false;
    private String currentUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webshop_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Autentikált felhasználó");
            getCurrentUserType();
        } else {
            Log.d(LOG_TAG, "Nem autentikált felhasználó");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();
        mAdapter = new ShopingItemAdapter(this, mItemList, currentUserType);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();
    }

    private void getCurrentUserType() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(user.getUid());
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentUserType = documentSnapshot.getString("accountType");
                Log.d(LOG_TAG, "A felhasználó egy: " + currentUserType);
                mAdapter.setCurrentUserType(currentUserType);
            }
        }).addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba a felhasználói adatok lekérésekor: " + e.getMessage()));
    }

    // Read
    private void queryData() {
        mItemList.clear();

        mItems.orderBy("name").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShopingItem item = document.toObject(ShopingItem.class);
                item.setId(document.getId());
                mItemList.add(item);
            }

            if (mItemList.isEmpty()){
                initailizeData();
                queryData();
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    // Delete
    public void deleteItem(ShopingItem item) {
        DocumentReference itemRef = mItems.document(item._getId());
        itemRef.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Tétel törölve: " + item._getId());
            queryData();
        }).addOnFailureListener(e -> Toast.makeText(this, "A terméket " + item._getId() + "nem lehetett törölni!", Toast.LENGTH_SHORT).show());
    }

    // Initialize data
    private void initailizeData() {
        String[] itemsList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemsDescription = getResources().getStringArray(R.array.shopping_item_desc);
        String[] itemsPrice = getResources().getStringArray(R.array.shopping_item_price);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);

        for (int i = 0; i < itemsList.length; i++) {
            mItems.add(new ShopingItem(itemsList[i], itemsDescription[i], itemsPrice[i], itemsImageResource.getResourceId(i, 0)));
        }

        itemsImageResource.recycle();
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
        } else if (id == R.id.cart) {
            Log.d(LOG_TAG, "Kosár menüpont kiválasztva!");
            Toast.makeText(this, "Megrendelés sikeres!", Toast.LENGTH_SHORT).show();
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

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_row);
        mRecyclerView.startAnimation(animation);

        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }
}