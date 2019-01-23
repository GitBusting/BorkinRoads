package com.bitirme.gitbusters.borkinroads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.List;

public class DogButtonAdapter extends RecyclerView.Adapter<DogButtonAdapter.ViewHolder> {

    private List<Doggo> doggobarlist;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context _context;

    // data is passed into the constructor
    DogButtonAdapter(Context context, List<Doggo> doggobarlist) {
        this.mInflater = LayoutInflater.from(context);
        this.doggobarlist = doggobarlist;
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
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            holder.dog.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Bitmap b = BitmapFactory.decodeFile(_context.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + "SysTemp.jpg", options);
            holder.dog.setImageBitmap(b);
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
        ImageButton dog;

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