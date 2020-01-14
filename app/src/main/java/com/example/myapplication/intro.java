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
        addSlide(AppIntroFragment.newInstance("Jaudio","Jaudio를 사용하여 안정적인 틀을 마련했습니다.", R.drawable.jaudio, Color.parseColor("#8E0000")));
        addSlide(AppIntroFragment.newInstance("Python","Python을 이용해 Neural Network를 이용할 수 있는 환경을 구성했습니다.", R.drawable.python, Color.parseColor("#7B8259")));
        addSlide(AppIntroFragment.newInstance("Neural Network","Neural Network를 이용하여 사용자의 취향에 따라 노래를 평가할 때마다 학습합니다.", R.drawable.neural, Color.parseColor("#A5A5A5")));
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
