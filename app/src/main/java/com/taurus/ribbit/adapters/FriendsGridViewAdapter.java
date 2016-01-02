package com.taurus.ribbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.taurus.ribbit.R;
import com.taurus.ribbit.utilities.MD5Util;

import java.util.List;

/**
 * Created by Emin on 12/22/2015.
 */
public class FriendsGridViewAdapter extends ArrayAdapter<ParseUser> {


    private static final String TAG = FriendsGridViewAdapter.class.getSimpleName();
    private final int layoutId;
    private Context context;
    private List<ParseUser> mUsers;
    private LayoutInflater mInflater;
    private ViewHolder mHolder;

    public FriendsGridViewAdapter(Context context,int layoutId , List<ParseUser> users){
        super(context, layoutId, users);

        this.context = context;
        this.mUsers = users;
        this.layoutId = layoutId;

        mInflater = LayoutInflater.from( context );
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public ParseUser getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mUsers.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = mInflater.inflate(layoutId, null);

            mHolder = new ViewHolder();
            mHolder.nameLabel = (TextView)convertView.findViewById(R.id.name_label);
            mHolder.userImageView = (ImageView)convertView.findViewById(R.id.user_imageview);
            mHolder.checkImageView = (ImageView)convertView.findViewById(R.id.check_imageview);
            convertView.setTag(mHolder);
        }
        else{
            //Get viewholder we already created
            mHolder = (ViewHolder)convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();

        if(email.equals("")){
            mHolder.userImageView.setImageResource(R.drawable.avatar_empty);
        }
        else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash +
                    "?s=204&d=404";
            Picasso.with(getContext())
                    .load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty)
                    .into(mHolder.userImageView);

        }

        mHolder.nameLabel.setText(user.getUsername());

        GridView gridView = (GridView) parent;
        if(gridView.isItemChecked(position)){
            mHolder.checkImageView.setVisibility(View.VISIBLE);
        }
        else {
            mHolder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;

    }

    public void refill(List<ParseUser> users){
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
