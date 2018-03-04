package com.example.vamc.smpplayer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LottieAnimationView animationView =  findViewById(R.id.animation_view);

        animationView.setAnimation("animation-w360-h220.json");
//        animationView.loop(true);
        animationView.playAnimation();

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f)
                .setDuration(100);

        /*animator.addUpdateListener(animation -> {
            animationView.setProgress((Float) animation.getAnimatedValue());
        });*/
        animator.start();

        animationView.addAnimatorListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {


                startActivity(new Intent(getApplicationContext(),MyMediaPlayer.class));
                finish();

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}

    });




//        animator.pause();
    }
}
