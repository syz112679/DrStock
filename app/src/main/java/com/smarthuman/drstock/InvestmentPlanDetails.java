package com.smarthuman.drstock;

import android.os.Bundle;

/**
 * Created by yuxuangu on 2018/4/22.
 */

public class InvestmentPlanDetails extends TitleActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investmentplandetails);

        setTitle(R.string.investmentPlanDetails);
        showBackward(getDrawable(R.drawable.ic_return), true);
    }
}
