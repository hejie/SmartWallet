package com.nicholas.smartwallet.ui;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.keyboardsurfer.android.widget.crouton.*;

import com.nicholas.smartwallet.model.*;
import com.nicholas.smartwallet.util.NFCUtil;
import com.nicholas.smartwallet.data.*;

public class NFCActivity extends Activity {

	private static final String TAG = "nfc_SmartWallet";
	private static final String DELIMETER = ";";
	private static final boolean SHOW_PROMPT = false;

	private static final String PACKAGE_NAME = "com.nicholas.smartwallet.ui";

	private Context mCtx = null;

	private static final int MESSAGE_SENT = 1;

	private boolean mResumed = false;
	private boolean mWriteMode = false;
	private boolean nfcExchanged = false;

	private NfcAdapter mNfcAdapter = null;
	private PendingIntent mNfcPendingIntent;
	private IntentFilter[] mWriteTagFilters;
	private IntentFilter[] mNdefExchangeFilters;
	
	private TextView nfc_title;
	private TextView nfc_content_header;
	private ListView nfc_content_list;
	private View nfc_panel;
	
	private database SQLiteAdapter;
	
	private Bundle bundle;
	
	private String[] nfc_content_SEND;
	private String[] nfc_content_RECEIVE = new String[1];
	private ArrayList<NFCInfoModel> nfc_infos = new ArrayList<NFCInfoModel>();
	private NFCItemAdapter nfcitemadapter;
	
	private String direction;
	private String transID;
	private String datetime;
	
	private RecordModel record;
	
 	/*** initialize a pending intent ***/
	private void initnfc() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// Handle all of our received nfc intents in this activity.
		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Intent filters for reading a note from a tag or exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("state/data");
		} catch (MalformedMimeTypeException e) {
			// Handle Properly
		}
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

		// Intent filters for writing to a tag
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };

	}

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_nfc);
		mCtx = this;
		
		nfc_panel = this.findViewById(R.id.nfc_panel);
		nfc_panel.setClickable(false);

		nfc_title = (TextView) this.findViewById(R.id.nfc_title_text);
		nfc_content_header = (TextView) this.findViewById(R.id.nfc_content_header);
		nfc_content_list = (ListView) this.findViewById(R.id.nfc_content_list);
		
		// set up database for writting
		SQLiteAdapter = new database(this.getApplicationContext());
		SQLiteAdapter.openToWrite();
		
		// getting data parameters from Main Activity/Transaction Activity
		bundle = getIntent().getExtras();
		direction = bundle.getString("direction");
		
		// generate reference number for transaction
		Random rnd = new Random();
		int randno = rnd.nextInt(9999999);
		NumberFormat numFormat = new DecimalFormat("#######");
		transID = "#" + numFormat.format(randno); 
		
		// create record model
		createRecordModel();
		
		// initiate UI components
		nfc_title.setText("Bring Both Devices Together");
		
		nfc_infos.clear();
		if(direction.equals("Incoming"))
		{	
			nfc_content_header.setText("Issue Receipt");
			nfc_infos.add(new NFCInfoModel("Ref Number",transID));
			nfc_infos.add(new NFCInfoModel("Payee ID",record.getPayeeId()));
			nfc_infos.add(new NFCInfoModel("Payee Name",record.getPayeeName()));
			nfc_infos.add(new NFCInfoModel("Category",record.getCategory()));
		}
		else if(direction.equals("Outgoing"))
		{
			nfc_content_header.setText("Payment");
			nfc_infos.add(new NFCInfoModel("Payer ID",record.getPayeeId()));
			nfc_infos.add(new NFCInfoModel("Payer Name",record.getPayeeName()));
			nfc_infos.add(new NFCInfoModel("Amount",record.getCurrency() + "  " + String.format("%.2f", record.getAmount())));
			nfc_infos.add(new NFCInfoModel("Comments",record.getComments()));
		}
		else
		{
			nfc_content_header.setText("Undefined");
		}
		
		nfcitemadapter = new NFCItemAdapter(this,nfc_infos);
		nfc_content_list.setAdapter(nfcitemadapter);
		
		constructnfcContent();
		
		// initialize nfc
		try {
			initnfc();
			if (!mNfcAdapter.isEnabled()) {
				toast("Turn on NFC in Settings -> Wireles & Networks");
				Intent resultIntent = new Intent();
				setResult(Activity.RESULT_CANCELED,resultIntent);
				finish();
			}
		} catch (Exception e) {
			toast("NFC Not Enabled! Transaction terminated");
			Intent resultIntent = new Intent();
			setResult(Activity.RESULT_CANCELED,resultIntent);
			finish();
		}
		//
		NdefMessage ndefMsg = NFCUtil.createNdefMsg(nfc_content_SEND,
				NdefRecord.createApplicationRecord(PACKAGE_NAME));

		// Used to send static information of the activity.
		mNfcAdapter.setNdefPushMessage(ndefMsg, this, this);

		// Register callback to send current state of the app.
		mNfcAdapter.setNdefPushMessageCallback(mCreateNdefMessageCallback, this);

		// Register callback to listen for message-sent success
		mNfcAdapter.setOnNdefPushCompleteCallback(mOnNdefPushCompleteCallback, this);
	}

	private void createRecordModel()
	{
		record = new RecordModel();
		record.setTransID(transID);
		record.setAccID(bundle.getString("accID"));
		record.setAccName(bundle.getString("accName"));
		record.setAmount(Double.parseDouble(bundle.getString("amount")));
		record.setCategory(bundle.getString("category"));
		record.setCurrency(bundle.getString("currency"));
		record.setDirection(direction);
		record.setLocation(bundle.getString("location"));
		record.setPayee(bundle.getString("payeeName"));
		record.setPayeeID(bundle.getString("payeeID"));
		record.setComments(bundle.getString("comments"));	
	}
	
	private void constructnfcContent()
	{
		ArrayList<String> nfc_content_arrlist = new ArrayList<String>();
		if(direction.equals("Incoming"))
		{
			nfc_content_arrlist.add(direction); 			// 1. direction
			nfc_content_arrlist.add(transID);				// 2. transID
			nfc_content_arrlist.add(record.getPayeeId());	// 3. payeeID
			nfc_content_arrlist.add(record.getPayeeName()); // 4. payee name
			nfc_content_arrlist.add(record.getCategory());  // 5. category
		}
		else if(direction.equals("Outgoing"))
		{
			nfc_content_arrlist.add(direction);									// 1. direction
			nfc_content_arrlist.add(record.getPayeeId());						// 2. payerID
			nfc_content_arrlist.add(record.getPayeeName());						// 3. payer name
			nfc_content_arrlist.add(String.format("%.2f", record.getAmount()));	// 4. amount
			nfc_content_arrlist.add(record.getCurrency());						// 5. currency
			nfc_content_arrlist.add(record.getComments());						// 6. comments
		}
		
		nfc_content_SEND = new String[nfc_content_arrlist.size()];
		for(int i=0;i<nfc_content_arrlist.size();i++)
			nfc_content_SEND[i] = nfc_content_arrlist.get(i);
	}
	
	private CreateNdefMessageCallback mCreateNdefMessageCallback = new CreateNdefMessageCallback() {

		@Override
		public NdefMessage createNdefMessage(NfcEvent arg0) {
			NdefMessage ndefMsg = NFCUtil.createNdefMsg(nfc_content_SEND,
					NdefRecord.createApplicationRecord(PACKAGE_NAME));
			return ndefMsg;
		}
	};

	private OnNdefPushCompleteCallback mOnNdefPushCompleteCallback = new OnNdefPushCompleteCallback() {

		@Override
		public void onNdefPushComplete(NfcEvent arg0) {
			// A handler is needed to send messages to the activity when this
			// callback occurs, because it happens from a binder thread
			mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();

		}
	};

	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				nfcExchanged = true;
				updateUI();
				break;
			}
		}
	};

	public void onShare(View v) {
		mWriteMode = true;

		// Write to a tag for as long as the dialog is shown.
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mWriteTagFilters, null);

		new AlertDialog.Builder(NFCActivity.this)
				.setTitle("Touch tag to write the state of the game!")
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						mNfcAdapter.enableForegroundDispatch((Activity) mCtx,
								mNfcPendingIntent, mNdefExchangeFilters, null);
						mWriteMode = false;
					}
				}).create().show();
	}

	private void promptForContent(final String[] message) {
		new AlertDialog.Builder(this)
				.setTitle("Replace current content?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								receivenfcMsg(message);
								updateUI();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).show();
	}

	/*** after transaction is made ***/
	public void receivenfcMsg(String[] message)
	{
		nfc_content_RECEIVE = message;
	}
	
	public void updateUI() {
			
		/*** if message is not received ***/
		if(nfc_content_RECEIVE.length <= 1)	
		{
			nfc_title.setText("Please Try Again");
			nfc_content_header.setBackgroundColor(getResources().getColor(R.color.sunflower));
			Crouton.makeText(this,  "Please try again", Style.INFO).show();
			return;
		}
		
		nfc_title.setText("Touch here to continue");
		nfc_panel.setClickable(true);
		nfc_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
            	Intent resultIntent = new Intent();
            	if(nfcExchanged)
            	{
            		insertRecord();
            		setResult(Activity.RESULT_OK,resultIntent);
            	}
            	else
            	{
            		setResult(Activity.RESULT_CANCELED,resultIntent);
            	}
            	SQLiteAdapter.close();
				finish();
            }
        });
		
		/*** if both parties are in same mode (Incoming-Incoming, Outgoing-Outgoing) ***/
		if(nfc_content_RECEIVE[0].equals(direction))	
		{
			nfc_content_header.setText("Transaction Error");
			nfc_content_header.setBackgroundColor(getResources().getColor(R.color.alizarin));
			Crouton.makeText(this,  "Error in transaction!", Style.ALERT).show();
			return;
		}
		
		/*** get current date time with Date() ***/
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		datetime = dateFormat.format(date);
		
		if(nfc_content_RECEIVE[0].equals("Incoming") && nfc_content_RECEIVE.length == 5)
		{		
			nfc_content_header.setText("Receipt Acknowledgement");
			nfc_content_header.setBackgroundColor(getResources().getColor(R.color.emerald));	
			
			// display received message
			nfc_infos.clear();	
			nfc_infos.add(new NFCInfoModel("Date, Time", datetime)); 
			nfc_infos.add(new NFCInfoModel("Ref Number",nfc_content_RECEIVE[1]));				
			nfc_infos.add(new NFCInfoModel("Payee ID",nfc_content_RECEIVE[2])); 				
			nfc_infos.add(new NFCInfoModel("Payee Name",nfc_content_RECEIVE[3]));			
			nfc_infos.add(new NFCInfoModel("Category",nfc_content_RECEIVE[4]));	
					
			// set record parameters
			record.setDateTime(datetime);
			record.setTransID(nfc_content_RECEIVE[1]);
			record.setPayeeID(nfc_content_RECEIVE[2]);
			record.setPayee(nfc_content_RECEIVE[3]);
			record.setCategory(nfc_content_RECEIVE[4]);

			Crouton.makeText(this,  "Transaction successful !", Style.CONFIRM).show();
		}
		else if(nfc_content_RECEIVE[0].equals("Outgoing") && nfc_content_RECEIVE.length == 6)
		{
			nfc_content_header.setText("Received Payment");
			nfc_content_header.setBackgroundColor(getResources().getColor(R.color.emerald));
			
			// display received message
			nfc_infos.clear();	
			nfc_infos.add(new NFCInfoModel("Date, Time", datetime)); 
			nfc_infos.add(new NFCInfoModel("Payer ID",nfc_content_RECEIVE[1]));	
			nfc_infos.add(new NFCInfoModel("Payer Name",nfc_content_RECEIVE[2]));	
			nfc_infos.add(new NFCInfoModel("Amount",nfc_content_RECEIVE[4] + "  " + nfc_content_RECEIVE[3]));	
			nfc_infos.add(new NFCInfoModel("Comments",nfc_content_RECEIVE[5]));		
			
			// set record parameters
			record.setDateTime(datetime);
			record.setPayeeID(nfc_content_RECEIVE[2]);
			record.setPayee(nfc_content_RECEIVE[2]);
			record.setAmount(Double.parseDouble(nfc_content_RECEIVE[3]));
			record.setCurrency(nfc_content_RECEIVE[4]);	
			record.setComments(nfc_content_RECEIVE[5]);
			
			Crouton.makeText(this,  "Transaction successful !", Style.CONFIRM).show();
		}
		else
		{
			nfc_content_header.setText("Transaction Error");
			nfc_content_header.setBackgroundColor(getResources().getColor(R.color.alizarin));
			toast("Error in transaction!");
			return;
		}
		
		nfcitemadapter.notifyDataSetChanged();	
	}

	private void insertRecord()
	{
		// insert into transaction record
		SQLiteAdapter.insertRecord(record.getTransId(), record.getPayeeId(), record.getDirection(), 
				record.getAmount(), record.getCurrency(), record.getCategory(), 
				record.getAccID(), record.getAccName(), record.getPayeeName(), record.getDateTime(),
				record.getLocation(),record.getComments());
		
		// update balance
		double oldBalance = Double.parseDouble(bundle.getString("balance"));
		double amount = record.getAmount();
		if(direction.equals("Outgoing"))	// deduct balance, record expense
		{ 
			double newBalance = oldBalance - amount;
			double expense = Double.parseDouble(bundle.getString("expense")) + amount;
			SQLiteAdapter.updateAccountBalance(newBalance,Integer.parseInt(record.getAccID()));
			SQLiteAdapter.updateAccountExpense(expense,Integer.parseInt(record.getAccID()));	
		}
		else if(direction.equals("Incoming"))	// increase balance, record income
		{
			double newBalance = oldBalance + amount;
			double income = Double.parseDouble(bundle.getString("income")) + amount;
			SQLiteAdapter.updateAccountBalance(newBalance,Integer.parseInt(record.getAccID()));
			SQLiteAdapter.updateAccountIncome(income,Integer.parseInt(record.getAccID()));	
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mResumed = true;
		// NDEF received from Android
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			NdefMessage[] messages = NFCUtil.getNdefMessages(getIntent());
			String body = new String(messages[0].getRecords()[0].getPayload());
			String[] message = body.split(DELIMETER);
			if (SHOW_PROMPT) {
				promptForContent(message);
			} else {
				receivenfcMsg(message);
				updateUI();
			}
			setIntent(new Intent());
		}

		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mNdefExchangeFilters, null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mResumed = false;
		mNfcAdapter.disableForegroundDispatch(this);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// NDEF exchange mode
		if (!mWriteMode
				&& NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] msgs = NFCUtil.getNdefMessages(intent);
			String body = new String(msgs[0].getRecords()[0].getPayload());
			String[] message = body.split(DELIMETER);
			if (SHOW_PROMPT) {
				promptForContent(message);
			} else {
				receivenfcMsg(message);
				updateUI();
			}
		}
		// Tag writing mode
		if (mWriteMode
				&& NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			NdefMessage ndefMsg = NFCUtil.createNdefMsg(nfc_content_SEND,
					NdefRecord.createApplicationRecord(PACKAGE_NAME));

			Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NFCUtil.writeTag(ndefMsg, detectedTag, this);
		}
	}

	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onBackPressed() {
	}
	
}