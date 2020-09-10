package com.example.drawzuiwei;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.drawzuiwei.bean.Point;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 解析数据
        final ArrayList<Point> list = new ArrayList<>();
        String date = MyDate.date;
        JSONObject jsonObject = JSONObject.parseObject(date);
        JSONArray records = jsonObject.getJSONArray("RECORDS");
        for (int i = 0; i < records.size(); i++) {
                Point point = JSONObject.parseObject(records.get(i).toString(), Point.class);
                list.add(point);
        }
        //reserve_6_0_state1_1000
        final MyView view = findViewById(R.id.view);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                view.setData(list);
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.add();
            }
        });

        findViewById(R.id.btn_reduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.reduce();
            }
        });
    }
}