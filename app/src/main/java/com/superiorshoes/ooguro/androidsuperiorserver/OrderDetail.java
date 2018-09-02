package com.superiorshoes.ooguro.androidsuperiorserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.superiorshoes.ooguro.androidsuperiorserver.Common.Common;
import com.superiorshoes.ooguro.androidsuperiorserver.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {

    TextView order_id,order_phone,order_address,order_total,order_comment;
    String order_id_value="";
    RecyclerView lstCloths;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_id = (TextView)findViewById(R.id.order_id);
        order_phone = (TextView)findViewById(R.id.order_phone);
        order_address = (TextView)findViewById(R.id.order_address);
        order_total = (TextView)findViewById(R.id.order_total);
        order_comment = (TextView)findViewById(R.id.order_comment);

        lstCloths = (RecyclerView)findViewById(R.id.lstCloths);
        lstCloths.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstCloths.setLayoutManager(layoutManager);

        if(getIntent() != null)
            order_id_value = getIntent().getStringExtra("OrderId");

        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_phone.setText(Common.currentRequest.getTotal());
        order_phone.setText(Common.currentRequest.getAddress());
        order_phone.setText(Common.currentRequest.getComment());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getCloths());
        adapter.notifyDataSetChanged();
        lstCloths.setAdapter(adapter);
    }
}
