package com.smarthuman.drstock;

import android.os.Bundle;

/**
 * Created by yuxuangu on 2018/4/14.
 */

public class CreditsActivity extends TitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        setTitle(R.string.creditsActivity);
        showBackward(getDrawable(R.drawable.ic_return), true);

    }

}
