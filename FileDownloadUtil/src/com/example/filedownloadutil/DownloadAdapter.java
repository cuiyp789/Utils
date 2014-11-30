package com.example.filedownloadutil;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ztb.charger.download.NetFile;

public class DownloadAdapter extends BaseAdapter {
    Context context;
    ListView listview;
    List<NetFile> list;
    public DownloadAdapter(Context context, ListView listview, List<NetFile> list){
        this.context = context;
        this.listview = listview;
        this.list = list;
    }
    
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView == null){
            holder = new Holder();
            convertView = View.inflate(context, R.layout.item_download_file, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvProgress = (TextView) convertView.findViewById(R.id.tv_progress);
            holder.pb = (ProgressBar) convertView.findViewById(R.id.progress);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        
        NetFile item = list.get(position);
        holder.tvName.setText(item.url);
        holder.tvProgress.setText(item.progress+"%");
        holder.pb.setProgress(item.progress);
        
        View contain = convertView.findViewById(R.id.layout_contain);
        contain.setTag(item.url);
        
        return convertView;
    }
    
    public void updateProgress(NetFile item){
        try{
        View contain = listview.findViewWithTag(item.url);
        TextView tvName = (TextView) contain.findViewById(R.id.tv_name);
        TextView tvProgress = (TextView) contain.findViewById(R.id.tv_progress);
        ProgressBar pb = (ProgressBar) contain.findViewById(R.id.progress);
        
        tvName.setText(item.url);
        tvProgress.setText(item.progress+"%");
        pb.setProgress(item.progress);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    class Holder{
        ImageView iv;
        TextView tvName, tvProgress;
        ProgressBar pb;
    }
}
