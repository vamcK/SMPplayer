package com.example.vamc.smpplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vamc on 17/2/18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.SongHolder> {

    ArrayList<SongInfo> songs;
    Context context;

    OnItemClickListener onItemClickListener;

    RecyclerAdapter(Context context, ArrayList<SongInfo> songs){
        this.context = context;
        this.songs = songs;
    }

    public interface OnItemClickListener{
        void onItemClick(Button b_id, View v, SongInfo obj, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener =  onItemClickListener;
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        return new SongHolder(myView);
    }

    @Override
    public void onBindViewHolder(SongHolder holder, int position) {
        final SongInfo c = songs.get(position);
        holder.tv1.setText(c.getSongName());
        holder.tv2.setText(c.getArtistName());
        holder.b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(holder.b1,view,c,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {

        TextView tv1,tv2;
        Button b1;

        public SongHolder(View itemView) {
            super(itemView);
            tv1= itemView.findViewById(R.id.textView2);
            tv2= itemView.findViewById(R.id.textView3);
            b1= itemView.findViewById(R.id.button);
        }
    }
}
