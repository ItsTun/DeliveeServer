package com.example.tunhanmyae.DeliveeServer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.tunhanmyae.DeliveeServer.Common.Common;
import com.example.tunhanmyae.DeliveeServer.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {
    TextView orders_id,orders_phone,orders_address,orders_total,orders_comment;
    String order_id_value="";
    RecyclerView lstFoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orders_id = (TextView) findViewById(R.id.order_id);
        orders_phone = (TextView) findViewById(R.id.order_phone);
        orders_address = (TextView) findViewById(R.id.order_address);
        orders_total = (TextView) findViewById(R.id.order_total);
        orders_comment = (TextView) findViewById(R.id.order_comment);
        lstFoods = (RecyclerView) findViewById(R.id.lstFoods);
        lstFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);

        if (getIntent()!=null)
            order_id_value = getIntent().getStringExtra("OrderId");




        // set valuse
        orders_id.setText(order_id_value);
        orders_phone.setText(Common.currentUser.getPhone());
        orders_address.setText(Common.currentRequest.getAddress());
        orders_total.setText(Common.currentRequest.getTotal());
        orders_comment.setText(Common.currentRequest.getComment());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        lstFoods.setAdapter(adapter);




    }



}
