package p.arieftaufikh.cobathesis4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment = null;
    final Activity activity= this;
    public ProgressDialog progressDialog;
    TextView name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        name = (TextView)header.findViewById(R.id.txtMenuName);
        email = (TextView)header.findViewById(R.id.txtMenuEmail);
        name.setText(SharedPrefManager.getInstance(getApplicationContext()).getUserFullname());
        email.setText(SharedPrefManager.getInstance(getApplicationContext()).getUserEmail());
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_ride);


        NavigationView navigation = (NavigationView)findViewById(R.id.nav_view);
        Menu drawer_menu = navigation.getMenu();
        int size = drawer_menu.size();
        for (int i=0;i<size;i++){
            drawer_menu.getItem(i).setChecked(false);
        }

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displaySelectedScreen(int itemId) {
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                //startActivity(new Intent(this,MainActivity.class));
                //relativeLayout.setVisibility(View.GONE);
                break;
            case R.id.nav_balance:
                fragment = new BalanceFragment();
                //relativeLayout.setVisibility(View.GONE);
                break;
            case R.id.nav_ride:
                fragment = new RideFragment();
                //relativeLayout.setVisibility(View.GONE);
                //finish();
                break;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        //replacing the fragment
        if (fragment != null) {
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout){
            SharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            displaySelectedScreen(id);
        }
        return true;
    }

    public void Scan(){
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Find QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if (result.getContents()==null){
                Toast.makeText(this,"Scan cancelled",Toast.LENGTH_LONG).show();
            }else{
                //Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                transaction(SharedPrefManager.getInstance(getApplicationContext()).getUsername(),result.getContents());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showDeatils(String username, String account, String bus_plate, String bus_fare, String date_time, String balance){
        String detailUsername = username;
        String detailAccount = account;
        String detailBusPlate = bus_plate;
        String detailBusFare = bus_fare;
        String detailDateTime = date_time;
        String detailRemainingBalance = balance;


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Details ");
        alertDialog.setMessage(
                        "Username : "+detailUsername+"\n"+
                        "User Account Number : "+detailAccount+"\n"+
                        "Bus Plate : "+detailBusPlate+"\n"+
                        "Bus Fare : "+detailBusFare+"\n"+
                        "Date Time : "+detailDateTime+"\n"+
                        "Remaining Balance : "+detailRemainingBalance+"\n\n"+
                        "Thank You. \n Please Becareful"
        );
        //alertDialog.setMessage(message1+"\n"+message2+"\n"+message3);
        alertDialog.setCancelable(true);
        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    public void transaction(String user, String id){
        final String username = user;
        final String bus_id = id;
        progressDialog.setMessage("Please Wait");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_RIDE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            /*SharedPrefManager.getInstance(getApplicationContext()).ride(
                                    jsonObject.getString("balance"),
                                    jsonObject.getString("balance_riel")
                            );
                            //Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            showDeatils(
                                    SharedPrefManager.getInstance(getApplicationContext()).getUsername(),
                                    jsonObject.getString("account_id"),
                                    jsonObject.getString("bus_plate"),
                                    jsonObject.getString("bus_fare"),
                                    jsonObject.getString("date_time"),
                                    jsonObject.getString("balance")
                            );*/
                            if (jsonObject.getBoolean("error")==false){
                                SharedPrefManager.getInstance(getApplicationContext()).ride(
                                        jsonObject.getString("balance"),
                                        jsonObject.getString("balance_riel")
                                );
                                //Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                                showDeatils(
                                        SharedPrefManager.getInstance(getApplicationContext()).getUsername(),
                                        jsonObject.getString("account_id"),
                                        jsonObject.getString("bus_plate"),
                                        jsonObject.getString("bus_fare"),
                                        jsonObject.getString("date_time"),
                                        jsonObject.getString("balance")
                                );
                            }else{
                                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage() ,Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("bus_id",bus_id);
                return  params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
