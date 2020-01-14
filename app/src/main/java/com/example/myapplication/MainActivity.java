package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ArrayList<MusicDTO> music_list;
    private ListView musicListView;
    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;
    private int introflag = 1;
    private int flag = 0;
    private int total_flag = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRetrofitInit();

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("firstStart",true);
                //if(isFirstStart){
                if(introflag == 1){
                    introflag = 0;
                    startActivity(new Intent(MainActivity.this, intro.class));
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart",false);
                    e.apply();
                }
            }
        });
        thread.start();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
        }
        else{
            flag+=1;
        }

        if(flag == total_flag) init();
    }

    private void init(){
        request_music();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (flag == total_flag - 1) init();
            else flag += 1;
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void request_music(){
        //url 요청주소 넣는 editText를 받아 url만들기
        Call<ResponseBody> result = mRetrofitAPI.getmusiclist();
        result.enqueue(mRetrofitCallback);
    }

    private Callback<ResponseBody> mRetrofitCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            music_list = new ArrayList<>();
            try {
                String jsonString = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonarray = jsonObject.getJSONArray("body");
                for (int i = 0, size = jsonarray.length(); i < size; i++){
                    JSONObject tmp_object= jsonarray.getJSONObject(i);
                    MusicDTO music_dto = new MusicDTO();
                    music_dto.setId(tmp_object.get("id").toString()); //서버에 저장된 파일 이름
                    music_dto.setTitle(tmp_object.get("title").toString()); //ㄹㅇ 음악이름
                    String tmp_bitmap_string = tmp_object.get("albumImage").toString().split(",")[1];

                    byte[] decodedString = Base64.decode(tmp_bitmap_string, Base64.DEFAULT);
                    Bitmap tmp_bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if(tmp_bitmap == null) Log.i("result_info","no tmp_bitmap");
                    music_dto.setAlbumImage(tmp_bitmap);
                    music_dto.setArtist(tmp_object.get("artist").toString());
                    music_list.add(music_dto);

                    if(i==size-1){
                        View view = findViewById(R.id.music_list);
                        musicListView = findViewById(R.id.music_list);
                        musicListView.setAdapter(new MusicListViewAdapter(view, music_list));

                        musicListView.setOnItemClickListener((parent, view1, position, id) -> {
                            Intent intent = new Intent(view1.getContext(), MusicDetailView.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("playlist", music_list);
                            bundle.putSerializable("position", position);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            t.printStackTrace();
        }
    };

    public static class MusicDTO implements Parcelable {
        private String id;
        private Bitmap albumImage;
        private String title;
        private String artist;

        public MusicDTO(){}

        public MusicDTO(Parcel in){
            id = in.readString();
            title = in.readString();
            artist = in.readString();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Bitmap getAlbumImage() {
            return albumImage;
        }

        public void setAlbumImage(Bitmap albumImage) {
            this.albumImage =  albumImage;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return "MusicDto{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", artist='" + artist + '\'' +
                    '}';
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public static final Creator<MusicDTO> CREATOR = new Creator<MusicDTO>(){
            @Override
            public MusicDTO createFromParcel(Parcel source) {
                return new MusicDTO(source);
            }

            @Override
            public MusicDTO[] newArray(int size) {
                return new MusicDTO[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(title);
            dest.writeString(artist);
        }
    }

    private void setRetrofitInit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://192.249.19.252:2980")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);
    }
}
