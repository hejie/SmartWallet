package com.nicholas.smartwallet.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.juliansuarez.libwizardpager.wizard.model.AbstractWizardModel;
import co.juliansuarez.libwizardpager.wizard.model.ModelCallbacks;
import co.juliansuarez.libwizardpager.wizard.model.Page;
import co.juliansuarez.libwizardpager.wizard.model.ReviewItem;
import co.juliansuarez.libwizardpager.wizard.ui.PageFragmentCallbacks;
import co.juliansuarez.libwizardpager.wizard.ui.ReviewFragment;
import co.juliansuarez.libwizardpager.wizard.ui.StepPagerStrip;

import de.keyboardsurfer.android.widget.crouton.*;

import com.nicholas.smartwallet.model.AccountModel;
import com.nicholas.smartwallet.model.TransactionWizardModel;
import com.nicholas.smartwallet.data.database;
import android.database.Cursor;
import com.nicholas.smartwallet.ui.R;

public class TransactionActivity extends FragmentActivity implements
		PageFragmentCallbacks, ReviewFragment.Callbacks, ModelCallbacks {
	private ViewPager mPager;
	private MyPagerAdapter mPagerAdapter;

	private boolean mEditingAfterReview;

	private AbstractWizardModel mWizardModel;

	private boolean mConsumePageSelectedEvent;

	private Button mNextButton;
	private Button mPrevButton;

	private List<Page> mCurrentPageSequence;
	private StepPagerStrip mStepPagerStrip;
	
	private database SQLiteAdapter;
	
	private ArrayList<AccountModel> accList = new ArrayList<AccountModel>();
	private String[] accNames;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);

		SQLiteAdapter = new database(this.getApplicationContext());
		SQLiteAdapter.openToRead();
		
		/*** get account list ***/	
		getAccounts();
		
		mWizardModel  = new TransactionWizardModel(this,accNames);
		
		if (savedInstanceState != null) {
			mWizardModel.load(savedInstanceState.getBundle("model"));
		}

		mWizardModel.registerListener(this);

		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);
		mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
		mStepPagerStrip
				.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
					@Override
					public void onPageStripSelected(int position) {
						position = Math.min(mPagerAdapter.getCount() - 1,
								position);
						if (mPager.getCurrentItem() != position) {
							mPager.setCurrentItem(position);
						}
					}
				});

		mNextButton = (Button) findViewById(R.id.next_button);
		mPrevButton = (Button) findViewById(R.id.prev_button);

		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mStepPagerStrip.setCurrentPage(position);

				if (mConsumePageSelectedEvent) {
					mConsumePageSelectedEvent = false;
					return;
				}

				mEditingAfterReview = false;
				updateBottomBar();
			}
		});

		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
					
					/*** get review items ***/
					List<ReviewItem> mCurrentReviewItems;
			        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
			        for (Page page : mCurrentPageSequence) {
			            page.getReviewItems(reviewItems);
			        } 
			        mCurrentReviewItems = reviewItems;
			        
			        /*** complete NFC infos ***/
			        String direction="";
			        String accName="";
			        String amount="-1";
			        String comments ="";
			        String accID = "";
			        String currency="";  
			        String balance="";
			        String expense="";
			        String income="";
			        
			        for(ReviewItem curReviewItem:mCurrentReviewItems)
			        {
			        	String title = curReviewItem.getTitle();
			        	String value = curReviewItem.getDisplayValue();
			            if (TextUtils.isEmpty(value)) {
			                value = "(None)";
			            }
			            if(title.equals("Transaction Type"))
			            	direction = value;
			            if(title.equals("To Account") || title.equals("From Account"))
			            	accName = value;
			            if(title.equals("Amount"))
			            	amount = value;
			            if(title.equals("Comments"));
			            	comments = value;
			        }
			        AccountModel curAccount = new AccountModel();
			        for(AccountModel account : accList)
			        {
			        	if(account.getAccName().equals(accName))
			        	{
			        		curAccount = account;
			        	}
			        }
			        accID = curAccount.getAccID();
			        currency = curAccount.getCurrency();
			        balance = String.format("%.2f", curAccount.getBalance());
			        expense = String.format("%.2f", curAccount.getExpense());
			        income = String.format("%.2f", curAccount.getIncome());
			        
			        /*** check for condition before making transaction ***/
			        if(direction.equals("Outgoing"))	
			        {
			        	if(Double.parseDouble(amount) > curAccount.getBalance())	// if outgoing amount exceeded balance
			        	{
			        		Crouton.makeText((Activity) view.getContext(),  "Not enough balance for this account!", Style.ALERT).show();
			        		return;
			        	}
			        }
			        
			        //Create the intent
			        Intent resultIntent = new Intent();
			        //Get the bundle from Main Activity
			        Bundle bundle = getIntent().getExtras();
			        bundle.putString("direction",direction);
			        bundle.putString("accName", accName);
			        bundle.putString("amount",amount);
			        bundle.putString("comments", comments);
			        bundle.putString("accID", accID);
			        bundle.putString("currency", currency);
			        bundle.putString("balance", balance);
			        bundle.putString("expense", expense);
			        bundle.putString("income", income);
			        //Add the bundle to the intent
			        resultIntent.putExtras(bundle);
			        // return result to Main Activity
			        setResult(Activity.RESULT_OK,resultIntent);
			        finish();
				} else {
					if (mEditingAfterReview) {
						mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
					} else {
						mPager.setCurrentItem(mPager.getCurrentItem() + 1);
					}
				}
			}
		});

		mPrevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			}
		});

		onPageTreeChanged();
		updateBottomBar();
	}
	
	public void getAccounts()
	{
		accList.clear();
		Cursor acc_all_cursor = SQLiteAdapter.query_Account_ALL();	
		if(acc_all_cursor != null)
		{
			if(acc_all_cursor.moveToFirst())
			{
				do{
					AccountModel account = new AccountModel();
					account.setAccID(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_KEY_ID)));
					account.setAccName(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_NAME)));
					account.setColor(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_COL)));
					account.setCurrency(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_CUR)));
					account.setDescription(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_DESC)));
					account.setType(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_TYPE)));
					account.setBalance(acc_all_cursor.getFloat(acc_all_cursor.getColumnIndex(database.ACC_BAL)));
					account.setBudget(acc_all_cursor.getFloat(acc_all_cursor.getColumnIndex(database.ACC_BUDG)));
					account.setExpense(acc_all_cursor.getFloat(acc_all_cursor.getColumnIndex(database.ACC_EXP)));
					account.setIncome(acc_all_cursor.getFloat(acc_all_cursor.getColumnIndex(database.ACC_INC)));
					accList.add(account);
				}while(acc_all_cursor.moveToNext());
			}
		}
		acc_all_cursor.close();	
		
		accNames = new String[accList.size()];
		for(int i=0;i<accList.size();i++)
			accNames[i] = accList.get(i).getAccName();
	}
	
	
	@Override
	public void onPageTreeChanged() {
		mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
		recalculateCutOffPage();
		mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 =
																		// review
																		// step
		mPagerAdapter.notifyDataSetChanged();
		updateBottomBar();
	}

	@SuppressLint("ResourceAsColor")
	private void updateBottomBar() {
		int position = mPager.getCurrentItem();
		if (position == mCurrentPageSequence.size()) {
			mNextButton.setText(R.string.finish);
			mNextButton.setBackgroundResource(R.drawable.finish_background);
			mNextButton.setTextColor(R.color.clouds);
		} else {
			mNextButton.setText(mEditingAfterReview ? R.string.review
					: R.string.next);
			mNextButton
					.setBackgroundResource(R.drawable.buttonbar_button_background);
			TypedValue v = new TypedValue();
			getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v,
					true);
			mNextButton.setTextAppearance(this, v.resourceId);
			mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
		}

		mPrevButton
				.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWizardModel.unregisterListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBundle("model", mWizardModel.save());
	}

	@Override
	public AbstractWizardModel onGetModel() {
		return mWizardModel;
	}

	@Override
	public void onEditScreenAfterReview(String key) {
		for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
			if (mCurrentPageSequence.get(i).getKey().equals(key)) {
				mConsumePageSelectedEvent = true;
				mEditingAfterReview = true;
				mPager.setCurrentItem(i);
				updateBottomBar();
				break;
			}
		}
	}

	@Override
	public void onPageDataChanged(Page page) {
		if (page.isRequired()) {
			if (recalculateCutOffPage()) {
				mPagerAdapter.notifyDataSetChanged();
				updateBottomBar();
			}
		}
	}

	@Override
	public Page onGetPage(String key) {
		return mWizardModel.findByKey(key);
	}

	private boolean recalculateCutOffPage() {
		// Cut off the pager adapter at first required page that isn't completed
		int cutOffPage = mCurrentPageSequence.size() + 1;
		for (int i = 0; i < mCurrentPageSequence.size(); i++) {
			Page page = mCurrentPageSequence.get(i);
			if (page.isRequired() && !page.isCompleted()) {
				cutOffPage = i;
				break;
			}
		}

		if (mPagerAdapter.getCutOffPage() != cutOffPage) {
			mPagerAdapter.setCutOffPage(cutOffPage);
			return true;
		}

		return false;
	}
	
	public class MyPagerAdapter extends FragmentStatePagerAdapter {
		private int mCutOffPage;
		private Fragment mPrimaryItem;

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			if (i >= mCurrentPageSequence.size()) {
				return new ReviewFragment();
			}

			return mCurrentPageSequence.get(i).createFragment();
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO: be smarter about this
			if (object == mPrimaryItem) {
				// Re-use the current fragment (its position never changes)
				return POSITION_UNCHANGED;
			}

			return POSITION_NONE;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			mPrimaryItem = (Fragment) object;
		}

		@Override
		public int getCount() {
			return Math.min(mCutOffPage + 1, mCurrentPageSequence == null ? 1
					: mCurrentPageSequence.size() + 1);
		}

		public void setCutOffPage(int cutOffPage) {
			if (cutOffPage < 0) {
				cutOffPage = Integer.MAX_VALUE;
			}
			mCutOffPage = cutOffPage;
		}

		public int getCutOffPage() {
			return mCutOffPage;
		}
	}
}
