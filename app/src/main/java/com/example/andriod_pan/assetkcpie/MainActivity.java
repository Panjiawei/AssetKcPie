package com.example.andriod_pan.assetkcpie;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.andriod_pan.assetkcpie.model.AssetKcData;
import com.example.andriod_pan.assetkcpie.view.AssetKcPie;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AssetKcPie assetKcPie;
    TextView tv_da;
    List<AssetKcData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assetKcPie = findViewById(R.id.assetkc_chart);
        tv_da = findViewById(R.id.tv_da);
        float total = 10 + 10 + 80;
        list.add(new AssetKcData(Color.parseColor("#FEAB1F"), "账户余额", 1, "1.00"));
        list.add(new AssetKcData(Color.parseColor("#22B5AB"), "待收本金", 1, "1.00"));
        list.add(new AssetKcData(Color.parseColor("#EF7340"), "锁定金额", 98, "98.00"));
        tv_da.setVisibility(View.GONE);
        assetKcPie.setData(list, total);
    }
}
