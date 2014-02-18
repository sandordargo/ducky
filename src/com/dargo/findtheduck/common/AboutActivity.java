package com.dargo.findtheduck.common;

import java.math.BigDecimal;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dargo.findtheduck.R;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

public class AboutActivity extends Activity implements OnClickListener
{

	private static final int PAYPAL_BUTTON_ID = 0;
	private static final String CONFIG_ENVIRONMENT = PaymentActivity.ENVIRONMENT_LIVE;

	// note that these credentials will differ between live & sandbox environments.
	private static final String CONFIG_CLIENT_ID = "AZ2HgxDaSTTKy6ALdkinZ4GoOiwOgu9YlR3TDhcxiTU3XrDQLMRfbRvwAOLK";
	// when testing in sandbox, this is likely the -facilitator email address. 
	private static final String CONFIG_RECEIVER_EMAIL = "sandor.dargo@gmail.com";
	
    Context myContext;
    CheckoutButton launchPayPalButton;
    
    boolean _paypalLibraryInit;
    private AdView adView;
    
	protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_the_app);
        myContext = this;
	    Intent intent = new Intent(this, PayPalService.class);
	
	    intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, CONFIG_ENVIRONMENT);
	    intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
	    intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL, CONFIG_RECEIVER_EMAIL);
	
	    startService(intent);
	    
	    initLibrary();
	    showPayPalButton();
        Utilities.showActionBar(this);
        adView = (AdView) findViewById(R.id.about_me_ad);
        adView.loadAd(new AdRequest());

	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	try
    	{
    		startActivity(Utilities.clickedOnMainMenu(item, this));
            return super.onOptionsItemSelected(item);	
    	}
    	catch (NullPointerException ex)
    	{
    		super.onBackPressed();
    		return false;
    	}
    }
    
	
	public void initLibrary() {
		PayPal pp = PayPal.getInstance();

		if (pp == null) {  // Test to see if the library is already initialized

		// This main initialization call takes your Context, AppID, and target server
		pp = PayPal.initWithAppID(this, "AZ2HgxDaSTTKy6ALdkinZ4GoOiwOgu9YlR3TDhcxiTU3XrDQLMRfbRvwAOLK", PayPal.ENV_LIVE);

		// Required settings:

		// Set the language for the library
		pp.setLanguage("en_US");

		// Some Optional settings:

		// Sets who pays any transaction fees. Possible values are:
		// FEEPAYER_SENDER, FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER, and FEEPAYER_SECONDARYONLY
		pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);

		// true = transaction requires shipping
		pp.setShippingEnabled(true);

		_paypalLibraryInit = true;
		}
		}
	
	
	@SuppressLint("InlinedApi")
	private void showPayPalButton() {

		// Generate the PayPal checkout button and save it for later use
		PayPal pp = PayPal.getInstance();
		launchPayPalButton = pp.getCheckoutButton(this, PayPal.BUTTON_278x43, CheckoutButton.TEXT_PAY);

		// The OnClick listener for the checkout button
		launchPayPalButton.setOnClickListener(this);

		// Add the listener to the layout
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (LayoutParams.WRAP_CONTENT,
		LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.bottomMargin = 10;
		launchPayPalButton.setLayoutParams(params);
		launchPayPalButton.setId(PAYPAL_BUTTON_ID);
		((LinearLayout) findViewById(R.id.about_the_app)).addView(launchPayPalButton);
		((LinearLayout) findViewById(R.id.about_the_app)).setGravity(Gravity.CENTER_HORIZONTAL);
		}
	
	public void PayPalButtonClick(View arg0, String iDonationValue) {
		
		PayPalPayment payment = new PayPalPayment(new BigDecimal(iDonationValue), "EUR", "Rose 'n Beer");

	    Intent intent = new Intent(this, PaymentActivity.class);
	    intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, CONFIG_ENVIRONMENT);
	    intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
	    intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL, CONFIG_RECEIVER_EMAIL);
	    //intent.putExtra(PaymentActivity.EXTRA_PAYER_ID, "");
	    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
	    startActivityForResult(intent, 0);
    	
		}
	
	public void PayPalActivityResult(int requestCode, int resultCode, Intent intent) {
		
		}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		EditText myDonationValue = (EditText) findViewById(R.id.donateValueET);
		String aDonationValueStr = myDonationValue.getText().toString();
		try
		{
			if (Double.parseDouble(aDonationValueStr) < 1)
			{
				Toast.makeText(myContext, "Sorry, you have to donate at least 1 $!", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		catch (Exception e)
		{
			Toast.makeText(myContext, "Sorry, you have to donate at least 1 $!", Toast.LENGTH_SHORT).show();
			return;
		}
		PayPalButtonClick(v, aDonationValueStr);
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
	    if (resultCode == Activity.RESULT_OK) {
	        PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
	        if (confirm != null) {
	            try {
	                Log.i("paymentExample", confirm.toJSONObject().toString(4));

	                // TODO: send 'confirm' to your server for verification.
	                // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
	                // for more details.

	            } catch (JSONException e) {
	                Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
	            }
	        }
	    }
	    else if (resultCode == Activity.RESULT_CANCELED) {
	        Log.i("paymentExample", "The user canceled.");
	    }
	    else if (resultCode == PaymentActivity.RESULT_PAYMENT_INVALID) {
	        Log.i("paymentExample", "An invalid payment was submitted. Please see the docs.");
	    }
	}


}
