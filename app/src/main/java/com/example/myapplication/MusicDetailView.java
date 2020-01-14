package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MusicDetailView extends AppCompatActivity implements View.OnClickListener {
    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;

    private List<MainActivity.MusicDTO> music_list;
    private MediaPlayer mediaPlayer;
    private TextView title;
    private ImageView album, previous, play, pause, next, shuffle, smile, like, dislike;
    private SeekBar seek_bar;
    boolean isPlaying = true;
    private ContentResolver res;
    private ProgressUpdate progressUpdate;
    private int position;
    private int prev_position;
    private boolean isShuffle = false;
    private boolean isSmile = false;
    private boolean isLike = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.music_detail_view);
        Intent intent = getIntent();
        mediaPlayer = new MediaPlayer();
        setRetrofitInit();
        title = (TextView)findViewById(R.id.title);
        album = (ImageView)findViewById(R.id.album);
        seek_bar = (SeekBar)findViewById(R.id.seek_bar);

        position = intent.getIntExtra("position",0);
        prev_position = -1;
        music_list = (List<MainActivity.MusicDTO>) intent.getSerializableExtra("playlist");
        res = getContentResolver();

        previous = (ImageView)findViewById(R.id.pre);
        play = (ImageView)findViewById(R.id.play);
        pause = (ImageView)findViewById(R.id.pause);
        next = (ImageView)findViewById(R.id.next);
        shuffle = findViewById(R.id.shuffle);
        shuffle.setColorFilter(Color.GRAY);
        smile = findViewById(R.id.smile);
        smile.setColorFilter(Color.GRAY);
        like = findViewById(R.id.like);
        dislike = findViewById(R.id.dislike);

        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        smile.setOnClickListener(this);
        like.setOnClickListener(this);
        dislike.setOnClickListener(this);

        playMusic(music_list.get(position));
        progressUpdate = new ProgressUpdate();
        progressUpdate.start();

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                if(seekBar.getProgress()>0 && play.getVisibility()== View.GONE){
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            //smile 이면 async 맞춰서 리퀘스트 기다리고 재생시키는 부분 만들어야함.
            if(isSmile){
                play_smile_music();
            }
            else if(isShuffle){
                play_shuffle_music();
            }else {
                play_next_music(1);
            }
        });
    }

    private void play_shuffle_music(){
        position = new Random().nextInt(music_list.size());
        playMusic(music_list.get(position));
    }

    private void play_smile_music(){
        //isLike 1이면 좋아요 누르고 다음노래, 0이면 싫어요 누르고 다음노래
        Call<ResponseBody> result = mRetrofitAPI.getnextmusic(
                new nextmusicinfo(Integer.toString(prev_position),
                        Integer.toString(position),
                        Boolean.toString(isLike))
        );
        result.enqueue(mRetrofitCallback);

    }

    private Callback<ResponseBody> mRetrofitCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                String jsonString = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonString);
                Log.i("result_info",jsonObject.toString());
                Log.i("result_info",jsonObject.getString("body"));
                prev_position = position;
                position = Integer.parseInt(jsonObject.getString("body"));
                Log.i("result_info",Integer.toString(position));
                playMusic(music_list.get(position));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }

    };




    //다음 노래 재생시키는 부분
    private void play_next_music(int weight) {
        position += weight;
        if(position >= 0 && position < music_list.size())
            playMusic(music_list.get(position));
        else if(position == music_list.size() || position == -1){
            position = 0;
            play_next_music(0);
        }
        else
            throw new RuntimeException("Error At play next music");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();
                break;
            case R.id.pause:
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                break;
            case R.id.pre:
                Log.d("[PRE_BUTTON]", "Pressed");
                play_next_music(-1);
                break;
            case R.id.next:
                Log.d("[NEXT_BUTTON]", "Pressed");
                if(isSmile){
                    play_smile_music();
                }
                else if(isShuffle){
                    play_shuffle_music();
                }else {
                    play_next_music(1);
                }
                break;
            case R.id.shuffle:
                if (isShuffle) {
                    shuffle.setColorFilter(Color.GRAY);
                } else {
                    if(isSmile){
                        smile.setColorFilter(Color.GRAY);
                        like.setVisibility(View.GONE);
                        dislike.setVisibility(View.GONE);
                        isSmile = !isSmile;
                        prev_position = -1;
                    }
                    shuffle.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                }
                isShuffle = !isShuffle;
                break;
            case R.id.smile:
                if (isSmile) {
                    smile.setColorFilter(Color.GRAY);
                    like.setVisibility(View.GONE);
                    dislike.setVisibility(View.GONE);
                    prev_position = -1;
                } else {
                    if(isShuffle){
                        shuffle.setColorFilter(Color.GRAY);
                        isShuffle = !isShuffle;
                    }
                    smile.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    like.setVisibility(View.VISIBLE);
                    dislike.setVisibility(View.VISIBLE);
                    if(isLike){
                        dislike.setColorFilter(null);
                        like.setColorFilter(Color.parseColor("#21FF4D"), PorterDuff.Mode.SRC_IN);
                    }
                    else{
                        like.setColorFilter(null);
                        dislike.setColorFilter(Color.parseColor("#E32A00"), PorterDuff.Mode.SRC_IN);
                    }
                }
                isSmile = !isSmile;
                break;
            case R.id.like:
                dislike.setColorFilter(null);
                like.setColorFilter(Color.parseColor("#21FF4D"), PorterDuff.Mode.SRC_IN);
                isLike = true;
                break;
            case R.id.dislike:
                like.setColorFilter(null);
                dislike.setColorFilter(Color.parseColor("#E32A00"), PorterDuff.Mode.SRC_IN);
                isLike = false;
        }
    }

    private class ProgressUpdate extends Thread {
        @Override
        public void run(){
            while(isPlaying){
                try{
                    Thread.sleep(500);
                    if(mediaPlayer != null)
                        seek_bar.setProgress(mediaPlayer.getCurrentPosition());
                } catch (Exception e){
                    Log.e("ProgressUpdate", e.getMessage());
                }
            }
        }
    }


    //POSITION에 있는 음악
    private void playMusic(MainActivity.MusicDTO musicDTO) {
        try {
            seek_bar.setProgress(0);
            if(musicDTO.getArtist().length() > 20)
                title.setText(musicDTO.getTitle());
            else
                title.setText(musicDTO.getArtist()+" - "+musicDTO.getTitle());

            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource("http://192.249.19.252:2980/music/"+musicDTO.getId());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seek_bar.setMax(mediaPlayer.getDuration());
            if(mediaPlayer.isPlaying()){
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }else{
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }

            Picasso.get().load("http://192.249.19.252:2980/image/"+musicDTO.getId()+".png").into(album);
            Log.i("result_info","image path : " + "http://192.249.19.252:2980/image/"+musicDTO.getId()+".png");

        }
        catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
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
