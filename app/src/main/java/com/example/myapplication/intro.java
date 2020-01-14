package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class intro extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance("Docker","Docker를 사용하여 안정적인 틀을 마련했습니다.", R.drawable.docker, Color.parseColor("#51e2b7")));
        addSlide(AppIntroFragment.newInstance("Jupyter","Jupyter로 바로 Tensorflow를 이용할 수 있는 환경을 구성했습니다.", R.drawable.jupyter, Color.parseColor("#51e2b7")));
        addSlide(AppIntroFragment.newInstance("Tensorflow","Tensorflow를 이용하여 사용자가 노래를 평가할 때마다 학습합니다.", R.drawable.tensorflow, Color.parseColor("#51e2b7")));
        showStatusBar(false);
        setBarColor(Color.parseColor("#333639"));
        setSeparatorColor(Color.parseColor("#2196F3"));

    }

    @Override
    public void onDonePressed(Fragment currentFragment){
        super.onDonePressed(currentFragment);
        Toast.makeText(this,"Eye of Soul에 오신 것을 환영합니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        Toast.makeText(this,"Eye of Soul에 오신 것을 환영합니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

}
