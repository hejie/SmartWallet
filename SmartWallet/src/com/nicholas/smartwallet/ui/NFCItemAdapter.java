package com.nicholas.smartwallet.ui;

import com.nicholas.smartwallet.model.NFCInfoModel;

  
import java.util.ArrayList;

import com.nicholas.smartwallet.ui.R;
 
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class NFCItemAdapter extends BaseAdapter{
     
    private Activity activity;
    private ArrayList<NFCInfoModel> NFCInfos;
    private NFCInfoModel curNFCInfo;
     
    public NFCItemAdapter(Activity activity, ArrayList<NFCInfoModel> NFCInfos){
        this.activity = activity;
        this.NFCInfos= NFCInfos;
    }
 
    @Override
    public int getCount() {
        return NFCInfos.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return NFCInfos.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
        public TextView nfcTitleText;
        public TextView nfcValueText;        
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
    	
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.listitem_nfc, null);
            
            holder = new ViewHolder();
            holder.nfcTitleText = (TextView) convertView.findViewById(R.id.nfc_item_title);
            holder.nfcValueText= (TextView) convertView.findViewById(R.id.nfc_item_value);
              
            convertView.setTag(holder);
        }
        else
        {
        	holder=(ViewHolder)convertView.getTag();
        }
        
        if(NFCInfos.size()<=0)
        {
            holder.nfcTitleText.setText("(None)");
             
        }
        else
        {
        	curNFCInfo = null;
        	curNFCInfo = NFCInfos.get(position);
        	
        	holder.nfcTitleText.setText(curNFCInfo.getTitle());
        	holder.nfcValueText.setText(curNFCInfo.getValue());
        }
         
        return convertView;
    }

 
}