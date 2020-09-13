package com.example.drawzuiwei;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
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
        JSONArray records = jsonObject.getJSONArray("tableAllowance");
        for (int i = 0; i < records.size(); i++) {
                Point point = JSONObject.parseObject(records.get(i).toString(), Point.class);
                point.fx = "1000";
//                if (TextUtils.equals(point.tableNo,"12") ||TextUtils.equals(point.tableNo,"098") || TextUtils.equals(point.tableNo,"097")){
//                      list.add(point);
//                }
            list.add(point);
        }
        //reserve_6_0_state1_1000
        final SeatSelectView view = findViewById(R.id.view);
//        view.setOnViewClickListener();

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
            }
        });

        findViewById(R.id.btn_reduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}