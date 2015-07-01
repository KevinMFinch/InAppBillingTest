package nyc.monorail.fragmentbillingtest;

import nyc.monorail.fragmentbillingtest.util.IabHelper;
import nyc.monorail.fragmentbillingtest.util.IabResult;
import nyc.monorail.fragmentbillingtest.util.Inventory;
import nyc.monorail.fragmentbillingtest.util.Purchase;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class StoreActivity extends ActionBarActivity {

    int counter;
    IabHelper mHelper;
    private static final String TAG = "nyc.monorail.fragmentbi";
    private Button oneButton;
    private Button fiveButton;
    private Button tenButton;
    static final String ONE_ITEM_SKU = "android.test.purchased";
    static final String FIVE_ITEM_SKU = "nyc.monorail.fiveitems";
    static final String TEN_ITEM_SKU = "nyc.monorail.tenitems";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        /*Intent intent=getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        counter=Integer.parseInt(message);
        TextView textView=(TextView) findViewById(R.id.counterText);
        textView.setText(" "+counter);*/


        String base64EncodedPublicKey=
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmmC26lmYrI39mb" +
                        "5wXI3btkVOxkxJP+CXq31ZnaZCU2vCse/TeLQr2CLabtLAs7hmWp5lDbqDQoIf6uQRc" +
                        "potoLr0a17SfkuTSzuzzsujaUweEgD688NXjIlwzQvoV9vA8weGBjy3/6RrTreKAFugwaLtTtUf" +
                        "b8xsfa8sIX/43AxaTfLE9CBcDaruakNUBghgpDEJDqUomX+dJX9QhCV6NtJClfdgPWLy/0LYsgBRAtV9qy+llR1" +
                        "EOI/WcnepaulvbitFKEaEnG7ht54VUgxHjlLjziuRGsz7QruUUc7cBGsYsiThYTYA9OoxUCn+cWLrzhmup8gt6ZodU" +
                        "x2Cup2YpQIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                               public void onIabSetupFinished(IabResult result) {
                                   if (!result.isSuccess()) {
                                       Log.d(TAG, "In-app Billing setup failed: " +
                                               result);
                                   } else {
                                       Log.d(TAG, "In-app Billing is set up OK");
                                   }
                               }
                           });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(ONE_ITEM_SKU)) {
                consumeItem();
            }
            else if(purchase.getSku().equals(FIVE_ITEM_SKU)){
                consumeItem();
            }
            else if(purchase.getSku().equals(TEN_ITEM_SKU)){
                consumeItem();
            }

        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ONE_ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                CharSequence sentence = "You have successfuly purchased one item!";
                Toast.makeText(getApplicationContext(),sentence,Toast.LENGTH_SHORT).show();

            } else {
                // handle error
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    public void buyTen(View view) {
        mHelper.launchPurchaseFlow(this, TEN_ITEM_SKU, 10001,
                mPurchaseFinishedListener, "mypurchasetoken");
    }

    public void buyFive(View view) {
        mHelper.launchPurchaseFlow(this, FIVE_ITEM_SKU, 10001,
                mPurchaseFinishedListener, "mypurchasetoken");
    }

    public void buyOne(View view) {
        mHelper.launchPurchaseFlow(this, ONE_ITEM_SKU, 10001,
                mPurchaseFinishedListener, "mypurchasetoken");
    }

    public void returnToMainActivity(View view) {
        //Implement
    }
}
