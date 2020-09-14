package com.example.drawzuiwei;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

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
                point.tableStatusCopy = point.tableStatus;
                point.fx = "1000";
//                if (TextUtils.equals(point.tableNo,"12") ||TextUtils.equals(point.tableNo,"098") || TextUtils.equals(point.tableNo,"097")){
//                      list.add(point);
//                }
            list.add(point);
        }
        //reserve_6_0_state1_1000
        final SeatSelectView view = findViewById(R.id.view);
        view.setOnViewClickListener(new SeatSelectView.OnViewClickListener() {
            @Override
            public void onClick(String tableNo) {
                Toast.makeText(MainActivity.this,tableNo,1).show();
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                view.setData(list);
            }
        });

        findViewById(R.id.btn_gl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList list = new ArrayList();
                list.add("靠窗");
                list.add("近小料台");
                view.filterData(list);
            }
        });
        findViewById(R.id.btn_gl2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList list = new ArrayList();
                list.add("靠窗");
                view.filterData(list);
            }
        });

        findViewById(R.id.btn_cz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.filterData(null);
            }
        });
    }
}