package com.example.notesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    LayoutInflater inflater;
    List<Note> notes;

    Adapter(Context context, List<Note> notes){
        this.inflater = LayoutInflater.from(context);
        this.notes = notes;
    }


    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        String title = notes.get(position).getTitle();
        String date = notes.get(position).getDate();
        String time = notes.get(position).getTime();
        String content = notes.get(position).getContent();

        holder.txtTitle.setText(title);// setare componente pentru fiecare notita
        holder.txtDate.setText(date);   // din lista de notite luata ca parametru
        holder.txtTime.setText(time);                           //in constructor
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(holder.imgLock.getContext());
        boolean isLocked = sharedPreferences.getBoolean("lock",false);
        if (notes.get(position).getLocked() == 1 && isLocked){
            holder.imgLock.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.txtContent.setText(content);
            holder.txtContent.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = holder.itemView.findViewById(R.id.containterItem).getLayoutParams();
            params.height = 500;
            holder.itemView.findViewById(R.id.containterItem).setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle, txtDate, txtTime, txtContent;
        ImageView imgLock;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // mapari din layout-ul pentru fiecare notita
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgLock = itemView.findViewById(R.id.imgLock);
            txtContent = itemView.findViewById(R.id.txtListContent);

            // onClick pentru fiecare notita vizibila din RecyclerView-ul din main
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),ViewNote.class);
                    intent.putExtra("ID",notes.get(getAdapterPosition()).getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
