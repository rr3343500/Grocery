package Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import Config.BaseURL;
import Config.SharedPref;
import tech.iwish.onhome.MainActivity;
import tech.iwish.onhome.R;
import util.ConnectivityReceiver;
import util.DatabaseHandler;
import util.Session_management;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 29/6/2017.
 */

public class Delivery_payment_detail_fragment extends Fragment {

    private static String TAG = Delivery_payment_detail_fragment.class.getSimpleName();

    private TextView tv_timeslot, tv_address, tv_total;
    private LinearLayout btn_order;

    private String getlocation_id = "";
    private String gettime = "";
    private String getdate = "";
    private String getuser_id = "";
    private String getstore_id = "";

    private int deli_charges;
    Double total;
SharedPreferences preferences;
    private DatabaseHandler db_cart;
    private Session_management sessionManagement;

    public Delivery_payment_detail_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_order, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.payment));

        db_cart = new DatabaseHandler(getActivity());
        sessionManagement = new Session_management(getActivity());

        tv_timeslot = (TextView) view.findViewById(R.id.textTimeSlot);
        tv_address = (TextView) view.findViewById(R.id.txtAddress);
        //tv_item = (TextView) view.findViewById(R.id.textItems);
        //tv_total = (TextView) view.findViewById(R.id.textPrice);
        tv_total = (TextView) view.findViewById(R.id.txtTotal);

        btn_order = (LinearLayout) view.findViewById(R.id.btn_order_now);

        getdate = getArguments().getString("getdate");

        preferences = getActivity().getSharedPreferences("lan", MODE_PRIVATE);
        String language=preferences.getString("language","");
        if (language.contains("spanish")) {
            gettime = getArguments().getString("time");

            gettime=gettime.replace("PM","م");
            gettime=gettime.replace("AM","ص");

        }else {
            gettime = getArguments().getString("time");

        }
        getlocation_id = getArguments().getString("location_id");
        getstore_id = getArguments().getString("store_id");
        deli_charges = Integer.parseInt(getArguments().getString("deli_charges"));
        String getaddress = getArguments().getString("address");

        tv_timeslot.setText(getdate + " " + gettime);
        tv_address.setText(getaddress);

        total = Double.parseDouble(db_cart.getTotalAmount()) + deli_charges;

        //tv_total.setText("" + db_cart.getTotalAmount());
        //tv_item.setText("" + db_cart.getWishlistCount());
        tv_total.setText(getResources().getString(R.string.tv_cart_item) + db_cart.getCartCount() + "\n" +
                getResources().getString(R.string.amount) + db_cart.getTotalAmount() + "\n" +
                getResources().getString(R.string.delivery_charge) + deli_charges + "\n" +
                getResources().getString(R.string.total_amount) +
                db_cart.getTotalAmount() + " + " + deli_charges + " = " + total+ getResources().getString(R.string.currency));


        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectivityReceiver.isConnected()) {
                    Fragment fm = new Payment_fragment();
                    Bundle args = new Bundle();
                    args.putString("total", String.valueOf(total));
                    args.putString("getdate", getdate);
                    args.putString("gettime", gettime);
                    args.putString("getlocationid", getlocation_id);
                    args.putString("getstoreid", getstore_id);
                    fm.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                    SharedPref.putString(getActivity(),BaseURL.TOTAL_AMOUNT, String.valueOf(total));
                } else {
                    ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                }
            }
        });

        return view;
    }

//    private void attemptOrder() {
//        // retrive data from cart database
//        ArrayList<HashMap<String, String>> items = db_cart.getCartAll();
//        if (items.size() > 0) {
//            JSONArray passArray = new JSONArray();
//            for (int i = 0; i < items.size(); i++) {
//                HashMap<String, String> map = items.get(i);
//                JSONObject jObjP = new JSONObject();
//                try {
//                    jObjP.put("product_id", map.get("product_id"));
//                    jObjP.put("qty", map.get("qty"));
//                    jObjP.put("unit_value", map.get("unit_value"));
//                    jObjP.put("unit", map.get("unit"));
//                    jObjP.put("price", map.get("price"));
//
//                    passArray.put(jObjP);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
//
//            if (ConnectivityReceiver.isConnected()) {
//
//                Log.e(TAG, "from:" + gettime + "\ndate:" + getdate +
//                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id + "\ndata:" + passArray.toString());
//
//                makeAddOrderRequest(getdate, gettime, getuser_id, getlocation_id, passArray);
//            }
//        }
//    }

    /**
     * Method to make json object request where json response starts wtih
     */
//    private void makeAddOrderRequest(String date, String gettime, String userid, String location, JSONArray passArray) {
//        String tag_json_obj = "json_add_order_req";
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("date", date);
//        params.put("time", gettime);
//        params.put("user_id", userid);
//        params.put("location", location);
//        params.put("data", passArray.toString());
//        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
//                BaseURL.ADD_ORDER_URL, params, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, response.toString());
//
//                try {
//                    Boolean status = response.getBoolean("responce");
//                    if (status) {
//
//                        String msg = response.getString("data");
//
////                        db_cart.clearCart();
////                        ((MainActivity) getActivity()).setCartCounter("" + db_cart.getWishlistCount());
//                      //  Double total = Double.parseDouble(db_cart.getTotalAmount()) + deli_charges;
//                        Bundle args = new Bundle();
//                        Fragment fm = new Payment_fragment();
//                        args.putString("msg", msg);
//
//                        args.putString("total", String.valueOf(total));
//                        fm.setArguments(args);
//                        FragmentManager fragmentManager = getFragmentManager();
//                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                                .addToBackStack(null).commit();
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//    }

}
