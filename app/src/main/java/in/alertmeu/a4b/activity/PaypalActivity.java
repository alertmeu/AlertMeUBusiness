package in.alertmeu.a4b.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import in.alertmeu.a4b.R;

public class PaypalActivity extends AppCompatActivity {
   /* private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("AcM6kabGNlXE-72B6uZduts2kWwszMl54seHK1-lNzWN4JRw4b3Dh1Dc40Xtp1NHJ5_nqoGaUcf3T1Vx");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);
        final Button button = (Button) findViewById(R.id.paypal_button);

    }

    public void beginPayment(View view) {
        Intent serviceConfig = new Intent(this, PayPalService.class);
        serviceConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(serviceConfig);

        PayPalPayment payment = new PayPalPayment(new BigDecimal("5.65"),
                "USD", "My Awesome Item", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent paymentConfig = new Intent(this, PaymentActivity.class);
        paymentConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        paymentConfig.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(paymentConfig, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(
                    PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("alertmeuapp", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification

                } catch (JSONException e) {
                    Log.e("alertmeuapp", "no confirmation data: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("alertmeuapp", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("alertmeuapp", "Invalid payment / config set");
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }*/
}
