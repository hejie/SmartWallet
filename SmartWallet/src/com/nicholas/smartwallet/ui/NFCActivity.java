package com.nicholas.smartwallet.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

import com.nicholas.smartwallet.model.*;
import com.nicholas.smartwallet.util.NFCUtil;
import com.nicholas.smartwallet.data.*;

public class NFCActivity extends Activity {

	private static final String TAG = "NFC_SmartWallet";
	private static final String DELIMETER = "-";
	private static final boolean SHOW_PROMPT = false;

	private static final String PACKAGE_NAME = "com.nicholas.smartwallet.ui";

	private Context mCtx = null;

	private static final int MESSAGE_SENT = 1;

	private boolean mResumed = false;
	private boolean mWriteMode = false;

	private NfcAdapter mNfcAdapter = null;
	private PendingIntent mNfcPendingIntent;
	private IntentFilter[] mWriteTagFilters;
	private IntentFilter[] mNdefExchangeFilters;
	
	private TextView nfc_title;
	private TextView nfc_content_header;
	private ListView nfc_content_list;
	
	private database SQLiteAdapter;
	
	private Bundle bundle;
	
	private String[] NFC_content;
	private ArrayList<NFCInfoModel> NFC_infos = new ArrayList<NFCInfoModel>();
	private NFCItemAdapter nfcitemadapter;
	
	private String direction;
	private String transID;
	private String datetime;
	
	private RecordModel record;
	
 	/*** initialize a pending intent ***/
	private void initNFC() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// Handle all of our received NFC intents in this activity.
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
		
		nfc_title = (TextView) this.findViewById(R.id.nfc_title_text);
		nfc_content_header = (TextView) this.findViewById(R.id.nfc_content_header);
		nfc_content_list = (ListView) this.findViewById(R.id.nfc_content_list);
		
		// set up database for writting
		SQLiteAdapter = new database(this.getApplicationContext());
		SQLiteAdapter.openToWrite();
		
		// getting data parameters from Main Activity/Transaction Activity
		bundle = getIntent().getExtras();
		
		// create record model
		createRecordModel();
		
		// initiate UI components
		nfc_title.setText("Tap Now");
		nfc_title.setClickable(false);

		direction = bundle.getString("direction");
		NFC_infos.clear();
		if(direction.equals("Incoming"))
		{
			nfc_content_header.setText("Issue Receipt");
			transID = "8172741";
			NFC_infos.add(new NFCInfoModel("Transaction ID",transID));
			NFC_infos.add(new NFCInfoModel("Payee ID",record.getPayeeId()));
			NFC_infos.add(new NFCInfoModel("Payee Name",record.getPayeeName()));
			NFC_infos.add(new NFCInfoModel("Category",record.getCategory()));
		}
		else if(direction.equals("Outgoing"))
		{
			transID = "8172742";
			nfc_content_header.setText("Payment");
			NFC_infos.add(new NFCInfoModel("Transaction ID",transID));
			NFC_infos.add(new NFCInfoModel("Payer ID",record.getPayeeId()));
			NFC_infos.add(new NFCInfoModel("Payer Name",record.getPayeeName()));
			NFC_infos.add(new NFCInfoModel("Amount",String.valueOf(record.getAmount())));
			NFC_infos.add(new NFCInfoModel("Currency",record.getCurrency()));
		}
		else
		{
			nfc_content_header.setText("Undefined");
		}
		
		nfcitemadapter = new NFCItemAdapter(this,NFC_infos);
		nfc_content_list.setAdapter(nfcitemadapter);
		
		constructNFCContent();
		
		// initialize NFC
		try {
			initNFC();
			if (!mNfcAdapter.isEnabled()) {
				toast("Turn on NFC in Settings -> Wireles & Networks");
				finish();
			}
		} catch (Exception e) {
			toast("NFC Not Enabled! Transaction terminated");
			finish();
		}
		//
		NdefMessage ndefMsg = NFCUtil.createNdefMsg(NFC_content,
				NdefRecord.createApplicationRecord(PACKAGE_NAME));

		// Used to send static information of the activity.
		mNfcAdapter.setNdefPushMessage(ndefMsg, this, this);

		// Register callback to send current state of the app.
		mNfcAdapter
				.setNdefPushMessageCallback(mCreateNdefMessageCallback, this);

		// Register callback to listen for message-sent success
		mNfcAdapter.setOnNdefPushCompleteCallback(mOnNdefPushCompleteCallback,
				this);
	}

	private void createRecordModel()
	{
		record = new RecordModel();
		record.setAccName(bundle.getString("accName"));
		record.setAmount(Double.parseDouble(bundle.getString("amount")));
		record.setCategory(bundle.getString("category"));
		record.setCurrency(bundle.getString("currency"));
		record.setDirection(direction);
		record.setLocation(bundle.getString("location"));
		record.setPayee(bundle.getString("payeeName"));
		record.setPayeeID(bundle.getString("payeeID"));
	}
	
	private void constructNFCContent()
	{
		ArrayList<String> NFC_content_arrlist = new ArrayList<String>();
		if(direction.equals("Incoming"))
		{
			NFC_content_arrlist.add(bundle.getString("direction")); // 1. direction
			NFC_content_arrlist.add(transID);						// 2. transID
			NFC_content_arrlist.add(bundle.getString("payeeID"));	// 3. payeeID
			NFC_content_arrlist.add(bundle.getString("payeeName")); // 4. payee name
			NFC_content_arrlist.add(bundle.getString("category"));  // 5. category
		}
		else if(direction.equals("Outgoing"))
		{
			NFC_content_arrlist.add(bundle.getString("direction"));	// 1. direction
			NFC_content_arrlist.add(transID);						// 2. transID
			NFC_content_arrlist.add(bundle.getString("payeeID"));	// 3. payerID
			NFC_content_arrlist.add(bundle.getString("payeeName"));	// 4. payer name
			NFC_content_arrlist.add(bundle.getString("amount"));	// 5. amount
			NFC_content_arrlist.add(bundle.getString("currency"));	// 6. currency
		}
		
		NFC_content= new String[NFC_content_arrlist.size()];
		for(int i=0;i<NFC_content_arrlist.size();i++)
			NFC_content[i] = NFC_content_arrlist.get(i);
	}
	
	private CreateNdefMessageCallback mCreateNdefMessageCallback = new CreateNdefMessageCallback() {

		@Override
		public NdefMessage createNdefMessage(NfcEvent arg0) {
			NdefMessage ndefMsg = NFCUtil.createNdefMsg(NFC_content,
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
				Toast.makeText(getApplicationContext(), "Transaction successful !",
						Toast.LENGTH_LONG).show();
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

	private void promptForContent(final String[] state) {
		new AlertDialog.Builder(this)
				.setTitle("Replace current content?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								updateUI(state);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).show();
	}

	/*** after transaction is made ***/
	public void updateUI(String[] message) {
		nfc_title.setText("Click to Continue");
		nfc_title.setClickable(true);
		nfc_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
            	SQLiteAdapter.close();
            	finish();
            }
        });
		
		if(message.length < 5 || message[0].equals(direction))
		{
			toast("Error in transaction!");
			return;
		}
		
		if(message[0].equals("Incoming") && message.length == 5)
		{
			nfc_content_header.setText("Receipt Acknowledgement");
			NFC_infos.clear();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			//get current date time with Date()
			Date date = new Date();
			datetime = dateFormat.format(date);
			NFC_infos.add(new NFCInfoModel("Data, Time", datetime)); record.setDateTime(datetime);
			NFC_infos.add(new NFCInfoModel("Transaction ID",message[1]));	record.setTransID(message[1]);
			NFC_infos.add(new NFCInfoModel("Payee ID",message[2])); 	record.setPayeeID(message[2]);
			NFC_infos.add(new NFCInfoModel("Payee Name",message[3]));	record.setPayee(message[3]);
			NFC_infos.add(new NFCInfoModel("Category",message[4]));	record.setCategory(message[4]);			
		}
		else if(message[0].equals("Outgoing") && message.length == 6)
		{
			nfc_content_header.setText("Received Payment");
			NFC_infos.clear();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			//get current date time with Date()
			Date date = new Date();
			datetime = dateFormat.format(date);
			NFC_infos.add(new NFCInfoModel("Data, Time", datetime)); record.setDateTime(datetime);
			NFC_infos.add(new NFCInfoModel("Transaction ID",message[1]));	record.setTransID(message[1]);
			NFC_infos.add(new NFCInfoModel("Payer ID",message[2]));	record.setPayeeID(message[2]);
			NFC_infos.add(new NFCInfoModel("Payer Name",message[3]));	record.setPayee(message[3]);
			NFC_infos.add(new NFCInfoModel("Amount",String.valueOf(message[4])));	record.setAmount(Double.parseDouble(message[4]));
			NFC_infos.add(new NFCInfoModel("Currency",message[5]));	record.setCurrency(message[5]);	
		}
		else
		{
			toast("Error in transaction!");
			return;
		}
		
		
		nfcitemadapter.notifyDataSetChanged();
		
		insertRecord();
	}

	private void insertRecord()
	{
		SQLiteAdapter.insertRecord(record.getTransId(), record.getPayeeId(), record.getDirection(), 
				record.getAmount(), record.getCurrency(), record.getCategory(), 
				record.getAccName(), record.getPayeeName(), record.getDateTime(), record.getLocation());
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
				updateUI(message );
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
			String[] state = body.split(DELIMETER);
			if (SHOW_PROMPT) {
				promptForContent(state);
			} else {
				updateUI(state);
			}
		}
		// Tag writing mode
		if (mWriteMode
				&& NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			NdefMessage ndefMsg = NFCUtil.createNdefMsg(NFC_content,
					NdefRecord.createApplicationRecord(PACKAGE_NAME));

			Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NFCUtil.writeTag(ndefMsg, detectedTag, this);
		}
	}

	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
}