package Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.Cart_adapter;
import Config.BaseURL;
import Config.Constants;
import Config.NameChecker;
import Connection.ConnectionServer;
import Connection.JsonHelper;
import tech.iwish.onhome.AppController;
import tech.iwish.onhome.LoginActivity;
import tech.iwish.onhome.MainActivity;
import tech.iwish.onhome.R;
import util.ConnectivityReceiver;
import util.DatabaseHandler;
import util.Session_management;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Cart_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Cart_fragment.class.getSimpleName();

    private RecyclerView rv_cart;
    private TextView tv_clear, tv_total, tv_item;
    private RelativeLayout btn_checkout;

    private DatabaseHandler db;

    private Session_management sessionManagement;
    private TextView tv_dv_charge;
    private int dilevery_charge;
    private TextView tv_cart_tota3;
    private Activity parentActivity ;
    private Object String;
    private String user_email;
    private List<NameChecker> nameCheckers = new ArrayList<>();
    public Cart_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.cart));


        Map<String, String> params = new HashMap<String, String>();
        final HashMap<String, String> data;

        Session_management session_management = new Session_management(getActivity());

        data =  session_management.getUserDetails();
        user_email = data.get(user_email);

//        Log.e("arraty",data.get());

        sessionManagement = new Session_management(getActivity());
        sessionManagement.cleardatetime();


        tv_clear = (TextView) view.findViewById(R.id.tv_cart_clear);
        tv_total = (TextView) view.findViewById(R.id.tv_cart_total);
        tv_item = (TextView) view.findViewById(R.id.tv_cart_item);
        btn_checkout = (RelativeLayout) view.findViewById(R.id.btn_cart_checkout);
        rv_cart = (RecyclerView) view.findViewById(R.id.rv_cart);
        tv_dv_charge = (TextView) view.findViewById(R.id.tv_dv_charge);
        rv_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
        tv_cart_tota3 = (TextView) view.findViewById(R.id.tv_cart_tota3);
        db = new DatabaseHandler(getActivity());

        ArrayList<HashMap<String, String>> map = db.getCartAll();

        Cart_adapter adapter = new Cart_adapter(getActivity(), map);
        rv_cart.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        updateData();

        tv_clear.setOnClickListener(this);
        btn_checkout.setOnClickListener(this);

        makeGetLimiteRequest();

        if(sessionManagement.isLoggedIn()) {
            //Toast.makeText(getActivity(), "no", Toast.LENGTH_SHORT).show();
            ConnectionServer connectionServer = new ConnectionServer();
            connectionServer.set_url(Constants.CHECK_LOGIN);
            connectionServer.requestedMethod("POST");
            connectionServer.buildParameter("email_name",data.get("user_email"));
            Log.e("mail",data.get("user_email"));
            connectionServer.execute(new ConnectionServer.AsyncResponse() {
                @Override
                public void processFinish(java.lang.String output) {
                    Log.e("output",output);
                    JsonHelper jsonHelper = new JsonHelper(output);
                    if(jsonHelper.isValidJson()){
                        String response = jsonHelper.GetResult("response");
                        if(response.equals("TRUE")){
                            JSONArray jsonArray = jsonHelper.setChildjsonArray(jsonHelper.getCurrentJsonObj(), "data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                jsonHelper.setChildjsonObj(jsonArray,i);
                                nameCheckers.add(new NameChecker(jsonHelper.GetResult("status")));
                            }
                            String status = jsonHelper.GetResult("status");
                            if(status.equals("1")){
                            }else{
                                Intent intent = new Intent(getActivity(),LoginActivity.class);
                                startActivity(intent);
                            }

                        }
                    }
                }
            });
            }
        else{
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.tv_cart_clear) {
            // showdialog
            showClearDialog();
        } else if (id == R.id.btn_cart_checkout) {

            if (ConnectivityReceiver.isConnected()) {
                if (sessionManagement.isLoggedIn()) {
                    Bundle args = new Bundle();
                    Fragment fm = new Delivery_fragment();
                    args.putInt("dilvery_charges", dilevery_charge);
                    fm.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                } else {
                    //Toast.makeText(getActivity(), "Please login or regiter.\ncontinue", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                }


            } else {
                ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
            }

        }
    }

    // update UI
    private void updateData() {
        tv_total.setText("" + db.getTotalAmount());
        tv_item.setText("" + db.getCartCount());
        ((MainActivity) getActivity()).setCartCounter("" + db.getCartCount());
    }

    private void showClearDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(getResources().getString(R.string.sure_del));
        alertDialog.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // clear cart data
                db.clearCart();
                ArrayList<HashMap<String, String>> map = db.getCartAll();
                Cart_adapter adapter = new Cart_adapter(getActivity(), map);
                rv_cart.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                updateData();

                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    /**
     * Method to make json array request where json response starts wtih
     */
    private void makeGetLimiteRequest() {

        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_LIMITE_SETTING_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                     //   Log.d("Limit_req", response.toString());

                        Double total_amount = Double.parseDouble(db.getTotalAmount());


                        try {
                            // Parsing json array response
                            // loop through each json object

                            boolean issmall = false;
                            boolean isbig = false;

                            // arraylist list variable for store data;
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = (JSONObject) response.get(i);
                                int value;

                               // Log.d("id",jsonObject.getString("id"));
                                if (jsonObject.getString("id").trim().equals("1")) {
                                    value = Integer.parseInt(jsonObject.getString("value").trim());
                                    dilevery_charge = Integer.parseInt(jsonObject.getString("delivery_charge").trim());
                                    Log.e("value",value+"" );
                                    Log.e("total_amount",total_amount+"" );

                                    if (total_amount < value) {
                                        issmall = false;
                                        //Toast.makeText(getActivity(), "" + jsonObject.getString("title") + " : " + value, Toast.LENGTH_SHORT).show();
                                        tv_dv_charge.setText(dilevery_charge+"");
                                       double f_total_amt = total_amount+dilevery_charge;
                                        tv_cart_tota3.setText(f_total_amt+"");
                                    }
                                    else
                                    {
                                        dilevery_charge = 0;
                                        tv_dv_charge.setText(dilevery_charge+"");
                                        double f_total_amt = total_amount+dilevery_charge;
                                        tv_cart_tota3.setText(f_total_amt+"");
                                    }
                                } else if (jsonObject.getString("id").trim().equals("2")) {
                                    value = Integer.parseInt(jsonObject.getString("value"));


                                    if (total_amount > value) {
                                        isbig = false;
                                        Toast.makeText(getActivity(), "" + jsonObject.getString("title") + " : " + value, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "Connection Time out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister reciver
        getActivity().unregisterReceiver(mCart);
    }

    @Override
    public void onResume() {
        super.onResume();
        // register reciver
        getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
    }

    // broadcast reciver for receive data
    private BroadcastReceiver mCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("update")) {
                updateData();
            }
        }
    };





}
