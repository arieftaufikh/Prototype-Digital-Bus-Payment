package p.arieftaufikh.cobathesis4;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class BalanceFragment extends Fragment implements View.OnClickListener {

    private TextView textViewBalance;
    private EditText etCode;
    private ProgressDialog progressDialog;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_balance,container,false);

        NavigationView navigation = (NavigationView)getActivity().findViewById(R.id.nav_view);
        Menu drawer_menu = navigation.getMenu();
        MenuItem menuItem;
        menuItem = drawer_menu.findItem(R.id.nav_balance);
        if(!menuItem.isChecked())
        {
            menuItem.setChecked(true);
        }

        textViewBalance=(TextView)view.findViewById(R.id.textViewBalance);
        if (SharedPrefManager.getInstance(getActivity()).getUserBalance()==null){
            textViewBalance.setText("0");
        }else{
            textViewBalance.setText(SharedPrefManager.getInstance(getActivity()).getUserBalance());
        }

        etCode=(EditText)view.findViewById(R.id.editTextBalance);
        button=(Button)view.findViewById(R.id.buttonRedeem);
        button.setOnClickListener(this);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing..");


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Account Balance");
    }

    public void topUp(){
        final String username = SharedPrefManager.getInstance(getActivity()).getUsername();
        final String code = etCode.getText().toString().trim();
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_TOP_UP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                SharedPrefManager.getInstance(getActivity().getApplicationContext()).updateBalance(
                                        jsonObject.getString("balance"),
                                        jsonObject.getString("balance_riel")
                                );
                                textViewBalance.setText(jsonObject.getString("balance"));
                                etCode.setText("");
                                Toast.makeText(getActivity().getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("code",code);
                return  params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v==button){
            topUp();
        }
    }
}
