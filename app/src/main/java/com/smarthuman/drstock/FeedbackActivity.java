package com.smarthuman.drstock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Created by yuxuangu on 2018/4/14.
 */

public class FeedbackActivity extends TitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        setTitle(R.string.feedbackActivity);
        showBackwardView(R.string.text_back, true);

        findViewById(R.id.TableRow_disclaimer).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.TableRow_disclaimer:
//                startActivity(new Intent(this, DisclaimerActivity.class));
                Toast.makeText(this, "Disclaimer", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
