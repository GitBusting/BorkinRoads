package com.bitirme.gitbusters.borkinroads;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.File;
import java.util.List;

public class DogButtonAdapter extends RecyclerView.Adapter<DogButtonAdapter.ViewHolder> {

    private final List<Doggo> doggobarlist;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Context _context;

    // data is passed into the constructor
    DogButtonAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.doggobarlist = Doggo.doggos;
        _context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.bar_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doggo tempDoggo = doggobarlist.get(position);
        String path = _context.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + tempDoggo.getName() + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            if (new File(path).exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                holder.dog.setImageBitmap(bitmap);
            } else {
                Resources r = _context.getResources();
                holder.dog.setImageDrawable(ResourcesCompat.getDrawable(r, R.drawable.plusicon, _context.getTheme()));/*
                Bitmap b = BitmapFactory.decodeFile(_context.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + "SysTemp.jpg", options);
                holder.dog.setImageBitmap(b);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return doggobarlist.size();
    }

    // convenience method for getting data at click position
    public Doggo getItem(int id) {
        return doggobarlist.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageButton dog;

        ViewHolder(View itemView) {
            super(itemView);
            dog = itemView.findViewById(R.id.imageButton);
            itemView.setOnClickListener(this);
            dog.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}