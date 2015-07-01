package nyc.monorail.fragmentbillingtest;

import nyc.monorail.fragmentbillingtest.util.IabHelper;
import nyc.monorail.fragmentbillingtest.util.IabResult;
import nyc.monorail.fragmentbillingtest.util.Inventory;
import nyc.monorail.fragmentbillingtest.util.Purchase;

import android.content.Intent;
import android.content.SharedPreferences;
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

    public final static String EXTRA_MESSAGE = "nyc.monorail.fragmentbillingtest.MESSAGE";
    int counter;
    IabHelper mHelper;
    private static final String TAG = "nyc.monorail.fragmentbi";
    private Button oneButton;
    private Button fiveButton;
    private Button tenButton;
    static final String ONE_ITEM_SKU = "android.test.purchased";
    static final String FIVE_ITEM_SKU = "android.test.purchased";
    static final String TEN_ITEM_SKU = "android.test.purchased";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        loadData();
        Intent intent=getIntent();
        TextView textView=(TextView) findViewById(R.id.counterText);
        String str= Integer.toString(counter);
        textView.setText(str);


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
                    Log.d(TAG, "Setup successful. Querying inventory.");
                    mHelper.queryInventoryAsync(mGotInventoryListener);

                }
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.d(TAG,"Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */
            // Check for item delivery -- if we own an item, we should use it immediately
            Purchase oneItemPurchase = inventory.getPurchase(ONE_ITEM_SKU);
            if (oneItemPurchase != null && verifyDeveloperPayload(oneItemPurchase)) {
                Log.d(TAG, "We have an item. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(ONE_ITEM_SKU), mConsumeFinishedListener);
                return;
            }

            updateUi();
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
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
            if(result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ONE_ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                if(purchase.getSku().equals(ONE_ITEM_SKU)) {
                    Log.d(TAG, "Consumption successful. Provisioning.");
                    CharSequence charSequence = "You just purchased one item.";
                    Toast.makeText(getApplicationContext(),charSequence,Toast.LENGTH_SHORT).show();
                    counter++;
                    saveData();
                }
            }
            else {
                Log.d(TAG,"Error while consuming: " + result);
            }
            updateUi();
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

    public void updateUi(){
        TextView textView = (TextView)findViewById(R.id.counterText);
        textView.setText(Integer.toString(counter)) ;
    }

    public void returnToMainActivity(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    void saveData() {

        /*
         * WARNING: on a real application, we recommend you save data in a secure way to
         * prevent tampering. For simplicity in this sample, we simply store the data using a
         * SharedPreferences.
         */

        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        spe.putInt("items", counter);
        spe.commit();
        Log.d(TAG, "Saved data: items = " + String.valueOf(counter));
    }

    void loadData() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        counter = sp.getInt("items", 0);
        Log.d(TAG, "Loaded data: items = " + String.valueOf(counter));
    }
}
