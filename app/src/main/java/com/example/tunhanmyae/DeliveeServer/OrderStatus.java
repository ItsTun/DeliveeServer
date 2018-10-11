package com.example.tunhanmyae.DeliveeServer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tunhanmyae.DeliveeServer.Common.Common;
import com.example.tunhanmyae.DeliveeServer.Interface.ItemClickListener;
import com.example.tunhanmyae.DeliveeServer.Remote.APIService;
import com.example.tunhanmyae.DeliveeServer.ViewHolder.OrderViewHolder;
import com.example.tunhanmyae.DeliveeServer.model.Category;
import com.example.tunhanmyae.DeliveeServer.model.MyResponse;
import com.example.tunhanmyae.DeliveeServer.model.Notification;
import com.example.tunhanmyae.DeliveeServer.model.Request;
import com.example.tunhanmyae.DeliveeServer.model.Sender;
import com.example.tunhanmyae.DeliveeServer.model.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MaterialSpinner spinner;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;
    APIService mService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);


        //firebaseinit

        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        //Init Service
        mService = Common.getFCMService();



        //recyceler init

        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        loadOrders();
        

    }


    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if( !isLongClick )
                        {
                            Intent intent = new Intent(getApplicationContext(),TrackingOrder.class);
                            Common.currentRequest=model;
                            startActivity(intent);
                        }
//                        Intent orderDetail = new Intent(getApplicationContext(),OrderDetail.class);
//                        Common.currentRequest=model;
//                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
//
//                        startActivity(orderDetail);



                    }
                });






            }

        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else  if (item.getTitle().equals(Common.DELETE))
        {
            deleteOrder(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key, Request item) {
        requests.child(key).removeValue();
        Toast.makeText(OrderStatus.this,"Item Delected",Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(final String key, final Request item)

    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please Choose Status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout,null);
        spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On My Way","Shipped");

        alertDialog.setView(view);
        final String localkey = key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                requests.child(localkey).setValue(item);
                sendOrderStatusToUser(localkey,item);


            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    private void sendOrderStatusToUser(final String key,final Request item) {
        DatabaseReference tokens = db.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapshot.getValue(Token.class);
                            Notification notification = new Notification("Eat it","Your Order"+key+" was updated");
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success==1)
                                            {
                                                Toast.makeText(OrderStatus.this,"Order was updated !",Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                Toast.makeText(OrderStatus.this,"Order was updated but failed !",Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("Error !",t.getMessage());

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


}
