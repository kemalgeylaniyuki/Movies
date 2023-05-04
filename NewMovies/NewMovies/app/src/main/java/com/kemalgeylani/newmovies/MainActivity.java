package com.kemalgeylani.newmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.kemalgeylani.newmovies.databinding.ActivityMainBinding;

import java.security.PublicKey;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    SQLiteDatabase sqLiteDatabase;
    ArrayList<Movie> movieArrayList;
    MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        movieArrayList = new ArrayList<>();

        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(movieArrayList);
        binding.recyclerView.setAdapter(movieAdapter);
    }

    private void getData(){

        try {

            sqLiteDatabase = this.openOrCreateDatabase("Movie",MODE_PRIVATE,null);

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM movie",null);

            int imageIx = cursor.getColumnIndex("image");
            int explanationTextIx = cursor.getColumnIndex("explanationText");
            int urlTextIx = cursor.getColumnIndex("urlText");
            int nameTextIx = cursor.getColumnIndex("nameText");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()){

                byte[] image = cursor.getBlob(imageIx);
                int id = cursor.getInt(idIx);
                String name = cursor.getString(nameTextIx);
                String exp = cursor.getString(explanationTextIx);
                String url = cursor.getString(urlTextIx);

                Movie movie = new Movie(image,id,name,exp,url);
                movieArrayList.add(movie);
            }

            cursor.close();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.movie_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_movie_id){
            Intent intent = new Intent(this,MovieActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}