package com.bhavaniprasad.diagnal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;




import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;


public class MainActivity extends AppCompatActivity {

    private JSONArray obj;
    private Button offlinebutton;
    private TextView textView,phototext;
    private int totalcount=0,pagenum;
    private ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<HashMap<String, String>> formList;
    ImageAdapter adapter;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    GridLayoutManager gridLayoutManager;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerview);
        formList = new ArrayList<>();

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            getSupportActionBar().setCustomView(R.layout.action_bar);
            getSupportActionBar().setElevation(0);

            title=findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
            title.setText("");
            title.measure(0,0);
        }
        getdata("CONTENTLISTINGPAGE-PAGE1.json");
        gridLayoutManager=new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter=new ImageAdapter(this,formList);
        recyclerView.setAdapter(adapter);

        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager=new GridLayoutManager(this,3);
            recyclerView.setLayoutManager(gridLayoutManager);
        }
        else {
            gridLayoutManager=new GridLayoutManager(this,7);
            recyclerView.setLayoutManager(gridLayoutManager);
        }

        loaddataonscroll();
    }

    private void loaddataonscroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if(formList.size()<=totalcount && pagenum==1){
                                getdata("CONTENTLISTINGPAGE-PAGE2.json");
                                adapter.notifyDataSetChanged();
                                loading=true;
                            }
                            else if(formList.size()<=totalcount && pagenum==2){
                                getdata("CONTENTLISTINGPAGE-PAGE3.json");
                                adapter.notifyDataSetChanged();
                                loading=true;
                            }
                        }
                    }
                }
            }
        });
    }

    private void getdata(String filename) {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(filename));
            title.setText(obj.getJSONObject("page").getString("title"));
            JSONArray m_jArry = obj.getJSONObject("page").getJSONObject("content-items").getJSONArray("content");
            if(totalcount==0){
                totalcount=Integer.parseInt(obj.getJSONObject("page").getString("total-content-items"));
            }
            pagenum=Integer.parseInt(obj.getJSONObject("page").getString("page-num"));
            HashMap<String, String> list;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject insidedata = m_jArry.getJSONObject(i);
                list = new HashMap<>();
                list.put("name", insidedata.getString("name"));
                list.put("poster-image", insidedata.getString("poster-image"));
                formList.add(list);
            }
        } catch (Exception e) {
            Log.e("error","error"+e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is=this.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}




