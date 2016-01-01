package com.taurus.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.taurus.ribbit.ParseConstants;
import com.taurus.ribbit.R;

import java.util.Date;
import java.util.List;

/**
 * Created by Emin on 12/20/2015.
 */
public class MessageListViewAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private final int resource;
    private List<ParseObject> objects;
    private ViewHolder mHolder;
    private LayoutInflater mInflater;

    public MessageListViewAdapter(Context context, int resource, List<ParseObject> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        mInflater = LayoutInflater.from( context );
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public ParseObject getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return objects.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(resource, null);

            mHolder = new ViewHolder();
            mHolder.iconImageView = (ImageView) convertView.findViewById(R.id.message_icon_image_view);
            mHolder.nameLabel = (TextView) convertView.findViewById(R.id.sender_text_view);
            mHolder.timeLabel = (TextView) convertView.findViewById(R.id.time_label);

            convertView.setTag(mHolder);

        }
        else{
            //Get viewholder we already created
            mHolder = (ViewHolder)convertView.getTag();
        }

        ParseObject message = objects.get(position);

        Date createdAt = message.getCreatedAt();
        //Use DateUtils to convert createdAt time
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(
                createdAt.getTime(),
                now,DateUtils.SECOND_IN_MILLIS).toString();

        mHolder.timeLabel.setText(convertedDate);

        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){

            mHolder.iconImageView.setImageResource(R.drawable.ic_image);
        }
        else{
            mHolder.iconImageView.setImageResource(R.drawable.ic_video);
        }
        mHolder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
    }

    public void refill(List<ParseObject> messages){
        objects.clear();
        objects.addAll(messages);
        notifyDataSetChanged();
    }

}


