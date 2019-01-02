package p.arieftaufikh.cobathesis4;

import android.app.Activity;
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

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Created by asus on 31/07/2018.
 */

public class RideFragment extends Fragment implements View.OnClickListener {

    public Button button;
    //public Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ride,container,false);

        NavigationView navigation = (NavigationView)getActivity().findViewById(R.id.nav_view);
        Menu drawer_menu = navigation.getMenu();
        MenuItem menuItem;
        menuItem = drawer_menu.findItem(R.id.nav_ride);
        if(!menuItem.isChecked())
        {
            menuItem.setChecked(true);
        }

        button = (Button)view.findViewById(R.id.btnScan);
        button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Ride Bus");
    }

    @Override
    public void onClick(View v) {
        if (v == button){
            ((MainActivity)getActivity()).Scan();
            //((MainActivity)getActivity()).showDeatils();
            //((MainActivity)getActivity()).onActivityResult();
        }
    }
}
