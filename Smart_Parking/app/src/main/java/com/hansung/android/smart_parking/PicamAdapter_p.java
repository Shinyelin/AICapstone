package com.hansung.android.smart_parking;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
/*임산부 (Pregnant) 에 대한 정보의 어댑터*/

public class PicamAdapter_p extends BaseAdapter {

    private ArrayList<PicamData> mInfoList = new ArrayList<>();
    private Activity context = null;

    public PicamAdapter_p(Activity context, ArrayList<PicamData> list) {
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
            convertView = inflater.inflate(R.layout.listview_p, parent, false);
        }
        ImageView iv_imgp = (ImageView) convertView.findViewById(R.id.iv_img_p);

        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name2);

        PicamData myItem = getItem(position);

        tv_name.setText(myItem.get_piName());
        Log.d("TF", myItem.get_piTF());
        /*불법주차 시 알림 깜빡이기*/
        if (myItem.get_piTF().equals("F")) {
            iv_imgp.setImageResource(R.drawable.ic_error_black_24dp);
            Animation startAnimation = AnimationUtils.loadAnimation(context, R.anim.blink);
            iv_imgp.startAnimation(startAnimation);

        }


        return convertView;
    }


}
