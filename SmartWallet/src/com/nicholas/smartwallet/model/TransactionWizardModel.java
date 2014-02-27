
package com.nicholas.smartwallet.model;

import android.content.Context;
import co.juliansuarez.libwizardpager.wizard.model.AbstractWizardModel;
import co.juliansuarez.libwizardpager.wizard.model.BranchPage;
import co.juliansuarez.libwizardpager.wizard.model.NumberPage;
import co.juliansuarez.libwizardpager.wizard.model.PageList;
import co.juliansuarez.libwizardpager.wizard.model.SingleFixedChoicePage;
import co.juliansuarez.libwizardpager.wizard.model.TextPage;

public class TransactionWizardModel extends AbstractWizardModel {
	
	public TransactionWizardModel(Context context, String...strings) {
		super(context,strings);
	}
	
	@Override
	protected PageList onNewRootPageList(String...strings) {
		
		return new PageList(new BranchPage(this, "Transaction Type").addBranch(
				"Incoming",
				new SingleFixedChoicePage(this, "To").setChoices(strings)
						.setRequired(true))
		.addBranch(
				"Outgoing",
				new SingleFixedChoicePage(this, "From").setChoices(strings)
						.setRequired(true),					
				new NumberPage(this, "Amount").setRequired(true)).setRequired(true),
				new TextPage(this, "Comments"));
	}
	

}
