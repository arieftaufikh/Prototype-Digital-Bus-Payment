package p.arieftaufikh.cobathesis4;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserRegistartionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername, editTextPassword, editTextPhoneNumber, editTextFullname, editTextEmail;
    private Button buttonRegister;
    private ProgressDialog progressDialog;

    private TextView textViewLogin;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registartion);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        editTextFullname = (EditText) findViewById(R.id.editTextFullName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        textViewLogin = (TextView) findViewById(R.id.textViewLogin);

        buttonRegister = (Button) findViewById(R.id.btnRegister);
        buttonRegister.setOnClickListener(this);

        textViewLogin.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    private void registerUser() {
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String phonenumber = editTextPhoneNumber.getText().toString().trim();
        final String fullname = editTextFullname.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        progressDialog.setMessage("Processing user register");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            String check = jsonObject.getString("error");
                            if (check.equals("2")) {
                                editTextUsername.setText("");
                                editTextPassword.setText("");
                                editTextEmail.setText("");
                            } else if (check.equals("1")) {
                                editTextUsername.setText("");
                                editTextPassword.setText("");
                                editTextEmail.setText("");
                                editTextFullname.setText("");
                                editTextPhoneNumber.setText("");

                                handler.postDelayed(runnable,1000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("phone_number", phonenumber);
                params.put("fullname", fullname);
                params.put("email", email);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            registerUser();
        } else if (v == textViewLogin) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    };
}
