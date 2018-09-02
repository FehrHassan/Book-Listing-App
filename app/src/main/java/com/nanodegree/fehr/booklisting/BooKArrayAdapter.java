package com.nanodegree.fehr.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Fehr on 16-Aug-17.
 */

public class BooKArrayAdapter extends ArrayAdapter<Book> {


    public BooKArrayAdapter(Context context, ArrayList<Book> bookArrayList) {
        super(context, 0, bookArrayList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.titleTextView = (TextView) convertView.findViewById(R.id.book_title);
        holder.authorsTextView = (TextView) convertView.findViewById(R.id.book_authors);
        holder.descriptionTextView = (TextView) convertView.findViewById(R.id.book_description);

        Book currentBook = getItem(position);


        holder.titleTextView.setText(currentBook.getBookTitle());

        String authors = currentBook.generateStringOfAuthor();
        if (authors == "")
            holder.authorsTextView.setText(getContext().getString(R.string.no_authors));
        else
            holder.authorsTextView.setText(authors);


        String description = currentBook.getBookDescription();
        if (description == "")
            holder.descriptionTextView.setText(getContext().getString(R.string.no_description));
        else
            holder.descriptionTextView.setText(description);

        return convertView;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView authorsTextView;
        TextView descriptionTextView;
    }
}
