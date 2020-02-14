package com.example.android_lab_assignment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.security.acl.LastOwnerException;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    List<FavLocation> favLocationList;
    Boolean expanded;
    Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_custom_cell,parent,false);

        return new ViewHolder(view);

    }

    public List<FavLocation> getFavLocationList() {
        return favLocationList;
    }

    public void setFavLocationList(List<FavLocation> favLocationList) {
        this.favLocationList = favLocationList;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {
       final FavLocation favLocation = favLocationList.get(position);

        holder.address.setText("Address: "+ favLocation.getAddress());
        holder.date.setText("Date: "+favLocation.getDate());
        if(favLocation.getVisited())
        {
            holder.visitedTv.setTextColor(Color.parseColor("#00ff00"));
            holder.visitedTv.setText("You have Visited This place");
        }

        else
        {
            holder.visitedTv.setText("You haven't Visited This place");

        }


        holder.mycardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(expanded) {

                    holder.lat.setVisibility(View.GONE);
                    holder.lng.setVisibility(View.GONE);
                    notifyItemChanged(position);
                    expanded = false;
                }
                else
                {
                    holder.lat.setVisibility(View.VISIBLE);
                    holder.lng.setVisibility(View.VISIBLE);
                    notifyItemChanged(position);
                    expanded = true;

                }
                //notifyDataSetChanged();
                notifyItemChanged(position);
            }
        });
        holder.directionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CustomDirection.class);
                intent.putExtra("data",favLocation);
                getContext().startActivity(intent);
            }
        });
        holder.visitedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavLocation favLocation1 = favLocationList.get(position);
                favLocation1.setVisited(true);
                LocationDB locationDB = LocationDB.getInstance(context);
                locationDB.daoObjct().update(favLocation1);
                holder.visitedTv.setTextColor(Color.parseColor("#00ff00"));
                holder.visitedTv.setText("You have Visited This place");
                notifyItemChanged(position);
            }
        });




    }

    @Override
    public int getItemCount() {
        return favLocationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lat, address, date, lng,temp,visitedTv;
        CardView mycardview,mycardview1;
        ImageButton directionBtn,visitedBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mycardview = itemView.findViewById(R.id.newcard);
            //mycardview1 = itemView.findViewById(R.id.newcard1);


            address = itemView.findViewById(R.id.textView2);
            date = itemView.findViewById(R.id.textView3);
            directionBtn = itemView.findViewById(R.id.directionBtn1);
            visitedBtn = itemView.findViewById(R.id.visitedBtn);
            visitedTv = itemView.findViewById(R.id.visitedTv);

            //expanded= false;

            //temp = itemView.findViewById(R.id.latii);





        }
    }

    public void deleteItem(int position) {

        FavLocation places = favLocationList.get(position);
        LocationDB userDatabase = LocationDB.getInstance(context);
        userDatabase.daoObjct().delete(places);
        Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show();
        favLocationList.remove(position);
        notifyDataSetChanged();
    }
}
