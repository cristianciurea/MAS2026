package ro.ase.ism_2026;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView tvDate;
    EditText etEUR, etUSD, etGBP, etXAU;

    InputStream ist = null;
    static FXRate cv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDate = findViewById(R.id.tvDate);
        etEUR = findViewById(R.id.editTextEUR);
        etUSD = findViewById(R.id.editTextUSD);
        etGBP = findViewById(R.id.editTextGBP);
        etXAU = findViewById(R.id.editTextXAU);

        Spinner spinnerLocations = findViewById(R.id.spinnerLocation);
        String[] locations = {"INTERNAL FILE SYSTEM", "LOCAL DATABASE", "FIREBASE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                locations);
        spinnerLocations.setAdapter(adapter);

        Button btnShow = findViewById(R.id.btnShow);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(getApplicationContext(),
                        "Click on the button!", Toast.LENGTH_LONG).show();*/
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("https://curs.bnr.ro/nbrfxrates.xml");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            ist = connection.getInputStream();
                            cv = parseXML(ist);

                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                tvDate.setText(cv.getDate());
                                etEUR.setText(cv.getEuro());
                                etUSD.setText(cv.getDolar());
                                etGBP.setText(cv.getPound());
                                etXAU.setText(cv.getGold());
                            }
                        });
                    }
                });
            }
        });

        Log.e("lifecycle", "Call method onCreate");

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = spinnerLocations.getSelectedItemPosition();

                FXRate fxRate = new FXRate(tvDate.getText().toString(),
                        etEUR.getText().toString(), etUSD.getText().toString(),
                        etGBP.getText().toString(), etXAU.getText().toString());

                if(position==0)
                {
                    try {
                        writeToFile("file.dat", fxRate);
                        fxRate=null;
                        fxRate = readFromFile("file.dat");
                        Toast.makeText(getApplicationContext(),
                                fxRate.toString(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else
                    if(position==1)
                    {
                        FXRatesDB database = FXRatesDB.getInstance(getApplicationContext());
                        database.getFXRatesDao().insert(fxRate);
                        Toast.makeText(getApplicationContext(),
                                "Saved into database!", Toast.LENGTH_LONG).show();
                    }
                    else
                        if(position==2)
                        {
                            writeFXRateInFirebase(fxRate);
                            Toast.makeText(getApplicationContext(),
                                    "Saved into Firebase!", Toast.LENGTH_LONG).show();
                        }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("lifecycle", "Call method onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("lifecycle", "Call method onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("lifecycle", "Call method onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("lifecycle", "Call method onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("lifecycle", "Call method onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("lifecycle", "Call method onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.option1)
        {
            Intent intent = new Intent(this, SendSMSActivity.class);
            startActivity(intent);
            return true;
        }
        else
        if(item.getItemId()==R.id.option2)
        {
            //TO DO
            return true;
        }
        else
        if(item.getItemId()==R.id.option3)
        {
            //TO DO
            return true;
        }

        return false;
    }

    public static Node getNodeByName(String nodeName, Node parentNode) throws Exception {

        if (parentNode.getNodeName().equals(nodeName)) {
            return parentNode;
        }

        NodeList list = parentNode.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = getNodeByName(nodeName, list.item(i));
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    public static String getAttributeValue(Node node, String attrName) {
        try {
            return ((Element)node).getAttribute(attrName);
        }
        catch (Exception e) {
            return "";
        }
    }
    public FXRate parseXML(InputStream ist)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document domDoc = db.parse(ist);
            domDoc.getDocumentElement().normalize();

            cv = new FXRate();

            Node cube = getNodeByName("Cube", domDoc.getDocumentElement());
            if(cube!=null)
            {
                String dataCurs = getAttributeValue(cube, "date");
                cv.setDate(dataCurs);

                NodeList listaCopii = cube.getChildNodes();
                for(int i=0;i<listaCopii.getLength();i++)
                {
                    Node rate = listaCopii.item(i);
                    String atribut = getAttributeValue(rate, "currency");
                    if(atribut.equals("EUR"))
                        cv.setEuro(rate.getTextContent());
                    if(atribut.equals("USD"))
                        cv.setDolar(rate.getTextContent());
                    if(atribut.equals("GBP"))
                        cv.setPound(rate.getTextContent());
                    if(atribut.equals("XAU"))
                        cv.setGold(rate.getTextContent());
                }
            }

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cv;
    }

    private void writeToFile(String fileName, FXRate fxRate)
            throws IOException
    {
        FileOutputStream fileOutputStream = openFileOutput(fileName, Activity.MODE_PRIVATE);
        DataOutputStream dos = new DataOutputStream(fileOutputStream);
        dos.writeUTF(fxRate.getDate());
        dos.writeUTF(fxRate.getEuro());
        dos.writeUTF(fxRate.getDolar());
        dos.writeUTF(fxRate.getPound());
        dos.writeUTF(fxRate.getGold());
        dos.flush();
        fileOutputStream.close();
    }

    private FXRate readFromFile(String fileName)
            throws IOException
    {
        FileInputStream fileInputStream = openFileInput(fileName);
        DataInputStream dis = new DataInputStream(fileInputStream);
        String date = dis.readUTF();
        String euro = dis.readUTF();
        String dolar = dis.readUTF();
        String pound = dis.readUTF();
        String gold = dis.readUTF();
        FXRate fxRate = new FXRate(date, euro, dolar, pound, gold);
        fileInputStream.close();
        return fxRate;
    }

    private void writeFXRateInFirebase(FXRate fxRate)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("ism2026-default-rtdb");

        myRef.child("ism2026-default-rtdb").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                fxRate.setUid(myRef.child("ism2026-default-rtdb").push().getKey());
                myRef.child("ism2026-default-rtdb").child(fxRate.getUid()).setValue(fxRate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}