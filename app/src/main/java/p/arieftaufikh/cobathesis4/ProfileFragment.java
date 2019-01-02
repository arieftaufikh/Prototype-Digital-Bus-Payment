package p.arieftaufikh.cobathesis4;

import android.app.ProgressDialog;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 31/07/2018.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private EditText etFullName, etEmail, etPhoneNumber;
    private Button btnSave;

    private ProgressDialog progressDialog;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            etEmail.setText(SharedPrefManager.getInstance(getActivity()).getUserEmail());
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        NavigationView navigation = (NavigationView)getActivity().findViewById(R.id.nav_view);
        Menu drawer_menu = navigation.getMenu();
        MenuItem menuItem;
        menuItem = drawer_menu.findItem(R.id.nav_profile);
        if(!menuItem.isChecked())
        {
            menuItem.setChecked(true);
        }

        etEmail = (EditText)view.findViewById(R.id.editTextEmailProfile);
        etFullName = (EditText)view.findViewById(R.id.editTextFullNameProfile);
        etPhoneNumber = (EditText)view.findViewById(R.id.editTextPhoneNumberProfile);

        etFullName.setText(SharedPrefManager.getInstance(getActivity()).getUserFullname());
        etEmail.setText(SharedPrefManager.getInstance(getActivity()).getUserEmail());
        etPhoneNumber.setText(SharedPrefManager.getInstance(getActivity()).getUserPhonenumber());

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        progressDialog = new ProgressDialog(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Profile Information");
        etEmail = (EditText)view.findViewById(R.id.editTextEmailProfile);
        etFullName = (EditText)view.findViewById(R.id.editTextFullNameProfile);
        etPhoneNumber = (EditText)view.findViewById(R.id.editTextPhoneNumberProfile);
    }

    private void updateUser(){
        final String fullname,email,phonenumber;
        final String username = SharedPrefManager.getInstance(getActivity()).getUsername();
        final String newfullname = etFullName.getText().toString().trim();
        final String newemail = etEmail.getText().toString().trim();
        final String newphonenumber = etPhoneNumber.getText().toString().trim();



        if (newfullname.equals("")){
            fullname = SharedPrefManager.getInstance(getActivity()).getUserFullname();
        }else{
            fullname = newfullname;
        }

        if (newemail.equals("")){
            email = SharedPrefManager.getInstance(getActivity()).getUserEmail();
        }else{
            email = newemail;
        }

        if (newphonenumber.equals("")){
            phonenumber = SharedPrefManager.getInstance(getActivity()).getUserPhonenumber();
        }else{
            phonenumber = newphonenumber;
        }



        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_UPDATE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                SharedPrefManager.getInstance(getActivity().getApplicationContext()).updateUser(
                                        jsonObject.getString("fullname"),
                                        jsonObject.getString("email"),
                                        jsonObject.getString("phonenumber")
                                );
                                progressDialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                                handler.postDelayed(runnable,500);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("fullname",fullname);
                params.put("email",email);
                params.put("phonenumber",phonenumber);
                params.put("username",username);
                return  params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave){
            updateUser();
            refresh();
        }
    }

    public void refresh(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.commit();
    }
}
