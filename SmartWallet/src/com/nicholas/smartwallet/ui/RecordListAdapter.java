
package com.nicholas.smartwallet.ui;
  
import java.util.ArrayList;

import com.nicholas.smartwallet.model.RecordModel;
import com.nicholas.smartwallet.ui.R;
 
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class RecordListAdapter extends BaseAdapter{
     
    private Activity activity;
    private Resources res;
    private ArrayList<RecordModel> transRecords;
    private RecordModel curTransRecord;
     
    public RecordListAdapter(Activity activity, ArrayList<RecordModel> transRecords){
        this.activity = activity;
        this.res = activity.getResources();
        this.transRecords = transRecords;
    }
 
    @Override
    public int getCount() {
        return transRecords.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return transRecords.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
      
        public ImageView recIcon;
        public TextView catText;
        public TextView payeeText;
        public TextView amountText;
        public TextView locationText;
          
    }
    
    @SuppressLint("DefaultLocale")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
    	
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.listitem_record, null);
            
            holder = new ViewHolder();
            holder.recIcon = (ImageView) convertView.findViewById(R.id.recordIcon_image);
            holder.catText = (TextView) convertView.findViewById(R.id.recordCat_text);
            holder.payeeText = (TextView) convertView.findViewById(R.id.recordPayee_text);
            holder.amountText = (TextView) convertView.findViewById(R.id.recordAmount_text);
            holder.locationText = (TextView) convertView.findViewById(R.id.recordLocation_text);
              
            convertView.setTag(holder);
        }
        else
        {
        	holder=(ViewHolder)convertView.getTag();
        }
        
        if(transRecords.size()<=0)
        {
            holder.catText.setText("No records");
             
        }
        else
        {
        	curTransRecord = null;
        	curTransRecord = transRecords.get(position);
        	
        	Drawable icon = res.getDrawable(res.getIdentifier("img_"+curTransRecord.getCategory().toLowerCase(),
        			"drawable",parent.getContext().getPackageName()));
        	holder.recIcon.setImageDrawable(icon);
        	holder.catText.setText(curTransRecord.getCategory());
        	holder.payeeText.setText(curTransRecord.getPayeeName());
        	holder.amountText.setText(curTransRecord.getCurrency() + " " +  String.format("%.2f",curTransRecord.getAmount()));
        	holder.locationText.setText(curTransRecord.getLocation());
        }
         
        return convertView;
    }

 
}