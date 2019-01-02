package p.arieftaufikh.cobathesis4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class CobaAcitivity extends AppCompatActivity {

    private TextView textView1,textView2,textView3,textView4;
    private EditText editText,editText2,editText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coba_acitivity);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        textView1 = (TextView)findViewById(R.id.Coba1);
        textView2 = (TextView)findViewById(R.id.Coba2);
        textView3 = (TextView)findViewById(R.id.Coba3);
        textView4 = (TextView)findViewById(R.id.Coba4);
        editText = (EditText)findViewById(R.id.editText5);
        editText2 = (EditText)findViewById(R.id.editText6);
        editText3 = (EditText)findViewById(R.id.editText7);

        textView1.setText(SharedPrefManager.getInstance(this).getUsername());
        textView2.setText(SharedPrefManager.getInstance(this).getUserEmail());
        textView3.setText(SharedPrefManager.getInstance(this).getUserFullname());
        textView4.setText(SharedPrefManager.getInstance(this).getUserPhonenumber());
        editText.setText(SharedPrefManager.getInstance(this).getUsername());
        editText2.setText(SharedPrefManager.getInstance(this).getUserEmail());
        editText3.setText(SharedPrefManager.getInstance(this).getUserPhonenumber());


    }
}
