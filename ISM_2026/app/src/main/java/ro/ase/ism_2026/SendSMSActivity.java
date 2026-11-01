package ro.ase.ism_2026;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigInteger;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SendSMSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_smsactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText etText = findViewById(R.id.editTextMessage);
        EditText etPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        Spinner spinnerContacts = findViewById(R.id.spinnerContacts);
        Button btnSend = findViewById(R.id.btnSend);

        String[] contacts = {"1 Gigel 0721345678", "2 Dorel 0723456789"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                contacts);
        spinnerContacts.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = etPhoneNumber.getText().toString();
                if(phoneNumber.equals(""))
                {
                    String[] array = spinnerContacts.getSelectedItem().toString().split(" ");
                    phoneNumber = array[array.length-1];
                    etPhoneNumber.setText(phoneNumber);
                }

                String message = etText.getText().toString();
                if(message.equals(""))
                    Toast.makeText(getApplicationContext(),
                            "Please fill in the message!", Toast.LENGTH_LONG).show();
                else
                {
                    BigInteger plainText = new BigInteger(message.getBytes());
                    RSA rsa = new RSA(1024);
                    BigInteger cipherText = rsa.encrypt(plainText);

                    if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(SendSMSActivity.this,
                                new String[]{Manifest.permission.SEND_SMS}, 0);
                    }

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber,null,
                            cipherText.toString(), null, null);
                    Toast.makeText(getApplicationContext(),
                            "SMS Sent!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}