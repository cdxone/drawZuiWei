package com.example.drawzuiwei;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.drawzuiwei.bean.Point;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SeatSelectView view;
    ArrayList<Point> list;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            view.setData(list);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 解析数据
        list = new ArrayList<>();
        String date = MyDate.dataTTY3;
        JSONObject jsonObject = JSONObject.parseObject(date);
        JSONArray records = jsonObject.getJSONArray("tableAllowance");
        for (int i = 0; i < records.size(); i++) {
                Point point = JSONObject.parseObject(records.get(i).toString(), Point.class);
                point.tableStatusCopy = point.tableStatus;
//                if (TextUtils.equals(point.tableNo,"8") ||TextUtils.equals(point.tableNo,"81")){
//                      list.add(point);
//                }
//                if (TextUtils.equals(point.tableNo,"6") ||TextUtils.equals(point.tableNo,"81")){
//                      list.add(point);
//                }
            list.add(point);
        }
        //reserve_6_0_state1_1000
        view = findViewById(R.id.view);
        view.setOnViewClickListener(new SeatSelectView.OnViewClickListener() {
            @Override
            public void onClick(String tableNo,String id) {
//                Toast.makeText(MainActivity.this,tableNo,1).show();

            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                handler.sendEmptyMessageDelayed(1,500);
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
                list.add("老人");
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