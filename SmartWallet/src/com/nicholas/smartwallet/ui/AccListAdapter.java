package com.nicholas.smartwallet.ui;

import java.util.ArrayList;

import com.nicholas.smartwallet.model.AccountModel;
import com.nicholas.smartwallet.ui.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AccListAdapter extends BaseAdapter implements OnClickListener {

	/*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<AccountModel> data;
    private static LayoutInflater inflater=null;
    public Resources res;
    AccountModel tempValues=null;
    int i=0;
     
    /*************  CustomAdapter Constructor *****************/
    public AccListAdapter(Activity a, ArrayList<AccountModel> d,Resources resLocal) {
         
           /********** Take passed values **********/
            activity = a;
            data=d;
            res = resLocal;
         
            /***********  Layout inflator to call external xml layout () ***********/
             inflater = ( LayoutInflater )activity.
                                         getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
    }
 
    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
         
        if(data.size()<=0)
            return 1;
        return data.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
     
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
         
        public TextView accName_text;
        public TextView balance_text;
        public TextView description_text;
        public ImageView accIcon_image;
    }
 
    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {
         
        View vi = convertView;
        ViewHolder holder;
        
        if(convertView==null){
             
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.listitem_acc, null);
             
            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.accName_text = (TextView) vi.findViewById(R.id.accName_text);
            holder.description_text = (TextView)vi.findViewById(R.id.accDesc_text);
            holder.balance_text=(TextView)vi.findViewById(R.id.accBalance_text);
            holder.accIcon_image=(ImageView)vi.findViewById(R.id.accIcon_image);
            
           /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else 
            holder=(ViewHolder)vi.getTag();
         
        if(data.size()<=0)
        {
            holder.accName_text.setText("No account added");
             
        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = (AccountModel ) data.get( position );
             
            /************  Set Model values in Holder elements ***********/

             holder.accName_text.setText(" " + tempValues.getAccName() );
             holder.description_text.setText(tempValues.getDescription());
             holder.balance_text.setText(tempValues.getCurrency() + " " + String.format("%.2f", tempValues.getBalance()));
             // get color from parsing argb
             int accColor = res.getColor(res.getIdentifier(tempValues.getColor(),"color", parent.getContext().getPackageName()));
             // set color of view
             GradientDrawable accView_bg = (GradientDrawable) vi.getBackground();
             accView_bg.setColor(accColor);
             // set color of icon
             Mode mMode = Mode.SRC_ATOP;
             Drawable accIcon = res.getDrawable(res.getIdentifier("img_"+tempValues.getType() ,"drawable",parent.getContext().getPackageName()));
             accIcon.setColorFilter(res.getColor(R.color.clouds),mMode);
             // then, assign icon to imageview
             holder.accIcon_image.setImageDrawable(accIcon);
             /******** Set Item Click Listener for LayoutInflater for each row *******/
             vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }
     
    @Override
    public void onClick(View v) {
            Log.v("CustomAdapter", "=====Row button clicked=====");
    }
     
    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
         
        OnItemClickListener(int position){
             mPosition = position;
        }
         
        @Override
        public void onClick(View arg0) {

          MainActivity sct = (MainActivity)activity;

         /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onAccItemClick(mPosition);
        }               
    }   

}
