package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MusicListViewAdapter extends BaseAdapter {
    List<MainActivity.MusicDTO> music_list;
    View view;
    LayoutInflater inflater;

    public MusicListViewAdapter(View view, ArrayList<MainActivity.MusicDTO> music_list) {
        this.music_list = music_list;
        this.view = view;
        this.inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return music_list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.music_list_view_item, parent, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(layoutParams);
        }

        ImageView imageView = convertView.findViewById(R.id.music_album);
        Bitmap albumImage = (music_list.get(position)).getAlbumImage();
        imageView.setImageBitmap(albumImage);

        TextView title = convertView.findViewById(R.id.music_title);
        title.setText(music_list.get(position).getTitle());

        TextView artist = convertView.findViewById(R.id.music_artist);
        artist.setText(music_list.get(position).getArtist());

        return convertView;
    }

    private static final BitmapFactory.Options options = new BitmapFactory.Options();

}
