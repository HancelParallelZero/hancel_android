package org.parallelzero.hancel;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import android.app.Activity;

import org.parallelzero.hancel.Fragments.SlideItemFragment;

/**
 * Created by izel on 8/11/15.
 */
public class IntroActivity extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SlideItemFragment.newInstance(R.layout.welcome, R.drawable.slide_rings));
        addSlide(SlideItemFragment.newInstance(R.layout.welcome, R.drawable.slide_tracking));
        addSlide(SlideItemFragment.newInstance(R.layout.welcome, R.drawable.slide_alert));
        addSlide(SlideItemFragment.newInstance(R.layout.welcome, R.drawable.slide_chat));
    }

    private void loadMainActivity(){
        finish();
    }

    @Override
    public void onDonePressed() {

        loadMainActivity();
    }
}
