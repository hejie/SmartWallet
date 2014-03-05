package com.nicholas.smartwallet.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class database {
	public static final String DATABASE_NAME = "SW_DATABASE";
	public static final int DATABASE_VERSION = 1;

	// account table
	public static final String ACCOUNT_TABLE = "AT";
	public static final String ACC_KEY_ID = "_id";
	public static final String ACC_NAME = "NAME";
	public static final String ACC_TYPE = "TYPE";
	public static final String ACC_DESC = "DESC";
	public static final String ACC_COL = "COL";
	public static final String ACC_CUR = "CUR";
	public static final String ACC_BAL = "BAL";
	public static final String ACC_INC= "INC";
	public static final String ACC_EXP = "EXP";
	public static final String ACC_BUDG = "BUDG";
	

	// record table
	public static final String RECORD_TABLE = "RT";
	public static final String REC_KEY_ID = "_id";
	public static final String REC_TRANS_ID = "tID";
	public static final String REC_PAY_ID = "pID";
	public static final String REC_DIR = "DIR";
	public static final String REC_AMT = "AMT";
	public static final String REC_CUR = "CUR";
	public static final String REC_CAT = "CAT";
	public static final String REC_ACC_ID = "ACCID";
	public static final String REC_ACC_NAME = "ACCNAME";
	public static final String REC_PAY_NAME = "PAYNAME";
	public static final String REC_DATETIME = "DATETIME";
	public static final String REC_LOC = "LOC";
	public static final String REC_COMNT = "COMNT";

	//create table SW_DATABASE (ID integer primary key, Content text not null);
	private static final String SCRIPT_CREATE_DATABASE =
			"create table if not exists " + ACCOUNT_TABLE + " ("
					+ ACC_KEY_ID + " integer primary key autoincrement, "
					+ ACC_NAME+ " text not null, "
					+ ACC_TYPE + " text not null,"
					+ ACC_DESC + " text not null,"
					+ ACC_COL  + " text not null,"
					+ ACC_BAL + " real not null,"
					+ ACC_INC + " real not null,"
					+ ACC_EXP + " real not null,"
					+ ACC_BUDG + " real not null,"
					+ ACC_CUR  + " text not null);";

	private static final String SCRIPT_CREATE_DATABASE1 =
			"create table if not exists " + RECORD_TABLE + " ("
					+ REC_KEY_ID + " integer primary key autoincrement, "
					+ REC_TRANS_ID + " text not null, "
					+ REC_PAY_ID + " text not null,"
					+ REC_DIR + " text not null,"
					+ REC_AMT + " real not null,"
					+ REC_CUR + " text not null,"
					+ REC_CAT + " text not null,"
					+ REC_ACC_ID + " text not null,"
					+ REC_ACC_NAME+ " text not null,"
					+ REC_PAY_NAME+ " text not null,"
					+ REC_DATETIME + " text not null,"
					+ REC_LOC + " text not null,"
					+ REC_COMNT + " text not null);";
	
	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;
	private Context context;

	public database(Context c){
		context = c;
	}

	public database openToRead() throws android.database.SQLException {

		sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;
	}

	public database openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this;
	}

	public void close(){
		sqLiteHelper.close();
	}
	
	public long insertAccount(String acc_name, String acc_type,String acc_desc, String acc_col, String acc_cur){

		ContentValues contentValues = new ContentValues();
		contentValues.put(ACC_NAME, acc_name);
		contentValues.put(ACC_TYPE,acc_type);
		contentValues.put(ACC_DESC,acc_desc);
		contentValues.put(ACC_COL,acc_col);
		contentValues.put(ACC_CUR,acc_cur);
		contentValues.put(ACC_BAL,0f);
		contentValues.put(ACC_INC,0f);
		contentValues.put(ACC_EXP,0f);
		contentValues.put(ACC_BUDG,0f);
		return sqLiteDatabase.insert(ACCOUNT_TABLE, null, contentValues);
	}
	
	/*** method created ONLY FOR DEVELOPMENT stage, deprecate after deployment ***/
	public long insertAccount_TEST(String acc_name, String acc_type,String acc_desc, String acc_col, String acc_cur,double acc_bal){

		ContentValues contentValues = new ContentValues();
		contentValues.put(ACC_NAME, acc_name);
		contentValues.put(ACC_TYPE,acc_type);
		contentValues.put(ACC_DESC,acc_desc);
		contentValues.put(ACC_COL,acc_col);
		contentValues.put(ACC_CUR,acc_cur);
		contentValues.put(ACC_BAL,acc_bal);
		contentValues.put(ACC_INC,0f);
		contentValues.put(ACC_EXP,0f);
		contentValues.put(ACC_BUDG,0f);
		return sqLiteDatabase.insert(ACCOUNT_TABLE, null, contentValues);
	}
	
	
	public long insertRecord(String rec_trans_id, String rec_pay_id ,String rec_dir,double rec_amt, String rec_cur, String rec_cat, 
			String rec_acc_id, String rec_acc_name,String rec_pay_name,String rec_datetime, String rec_loc, String rec_comnt){

		ContentValues contentValues = new ContentValues();
		contentValues.put(REC_TRANS_ID,rec_trans_id);
		contentValues.put(REC_PAY_ID,rec_pay_id);
		contentValues.put(REC_DIR,rec_dir);
		contentValues.put(REC_AMT,rec_amt);
		contentValues.put(REC_CUR,rec_cur);
		contentValues.put(REC_CAT,rec_cat);
		contentValues.put(REC_ACC_ID, rec_acc_id);
		contentValues.put(REC_ACC_NAME,rec_acc_name);
		contentValues.put(REC_PAY_NAME,rec_pay_name);
		contentValues.put(REC_DATETIME,rec_datetime);
		contentValues.put(REC_LOC,rec_loc);
		contentValues.put(REC_COMNT, rec_comnt);
		
		return sqLiteDatabase.insert(RECORD_TABLE, null, contentValues);
	}
	
	public long updateAccount(String acc_name, String acc_type,String acc_desc,String acc_col, String acc_cur, int id){
		ContentValues contentValues = new ContentValues();
		contentValues.put(ACC_NAME, acc_name);
		contentValues.put(ACC_TYPE,acc_type);
		contentValues.put(ACC_DESC,acc_desc);
		contentValues.put(ACC_COL,acc_col);
		contentValues.put(ACC_CUR,acc_cur);
		return sqLiteDatabase.update(ACCOUNT_TABLE, contentValues, ACC_KEY_ID+"="+id, null);
	}
	
	public long updateAccountBalance(double acc_bal, int id)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(ACC_BAL, acc_bal);
		return sqLiteDatabase.update(ACCOUNT_TABLE, contentValues, ACC_KEY_ID+"="+id, null);
	}
	
	public long updateAccountIncome(double acc_inc, int id)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(ACC_INC, acc_inc);
		return sqLiteDatabase.update(ACCOUNT_TABLE, contentValues, ACC_KEY_ID+"="+id, null);
	}
	
	public long updateAccountExpense(double acc_exp, int id)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(ACC_EXP, acc_exp);
		return sqLiteDatabase.update(ACCOUNT_TABLE, contentValues,  ACC_KEY_ID+"="+id, null);
	}
	
	public long updateAccountBudget(double acc_budg, int id)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(ACC_BUDG, acc_budg);
		return sqLiteDatabase.update(ACCOUNT_TABLE, contentValues,  ACC_KEY_ID+"="+id, null);
	}
	
	public int delete_AccountTable(){
		return sqLiteDatabase.delete(ACCOUNT_TABLE, null, null);
	}

	public int delete_RecordTable(){
		return sqLiteDatabase.delete(RECORD_TABLE, null, null);
	}
	
	public void delete_Account_byID(int id){
		sqLiteDatabase.delete(ACCOUNT_TABLE, ACC_KEY_ID+"="+id, null);
	}
	
	public void delete_Record_byID(int id){
		sqLiteDatabase.delete(RECORD_TABLE, REC_KEY_ID+"="+id, null);
	}
	
	public Cursor query_Account_ALL(){
		String a="SELECT * FROM AT";

		Cursor cursor=sqLiteDatabase.rawQuery(a, null);

		return cursor;
	}
	
	public Cursor query_Record_ALL(){
		String a="SELECT * FROM RT ORDER BY datetime("+REC_DATETIME+") DESC";

		Cursor cursor=sqLiteDatabase.rawQuery(a, null);

		return cursor;
	}
	
	public Cursor query_Record_byDateGroup(){
		String a="SELECT "+REC_DATETIME+", COUNT("+REC_KEY_ID+") FROM RT " +
				"GROUP BY "+REC_DATETIME+" ORDER BY date("+REC_DATETIME+") DESC";

		Cursor cursor=sqLiteDatabase.rawQuery(a, null);

		return cursor;
	}
	
	public Cursor queue_Record_byAccount(String acc_key_id){

		String a="SELECT * FROM RT WHERE " + REC_ACC_ID + "=" + acc_key_id+" ORDER BY datetime("+REC_DATETIME+") DESC";
		Cursor cursor=sqLiteDatabase.rawQuery(a, null);
		return cursor;
	}
	

	public class SQLiteHelper extends SQLiteOpenHelper {
		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SCRIPT_CREATE_DATABASE);
			db.execSQL(SCRIPT_CREATE_DATABASE1);
		}


		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}
	}

}