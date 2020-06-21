package com.hansung.android.smart_parking;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
/*장애인 (Disabled) 에 대한 정보의 어댑터*/
public class PicamAdapter_d extends BaseAdapter {

    private ArrayList<PicamData> mInfoList = new ArrayList<>();
    private Activity context = null;

    public PicamAdapter_d(Activity context, ArrayList<PicamData> list) {
        this.context = context;
        this.mInfoList = list;
    }

    @Override
    public int getCount() {
        return mInfoList.size();
    }

    @Override
    public PicamData getItem(int position) {
        return mInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        /* 'listview_d' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_d, parent, false);
        }
        ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img_d);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        PicamData myItem = getItem(position);
        tv_name.setText(myItem.get_piName());

        /*불법 주차시 알람 깜빡이기*/
        if (myItem.get_piTF().equals("F")) {
            iv_img.setImageResource(R.drawable.ic_error_black_24dp);
            Animation startAnimation = AnimationUtils.loadAnimation(context, R.anim.blink);
            iv_img.startAnimation(startAnimation);

        }


        return convertView;
    }


}
