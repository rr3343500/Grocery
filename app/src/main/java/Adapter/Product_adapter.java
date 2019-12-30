package Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Config.BaseURL;
import Config.Constants;
import Connection.ConnectionServer;
import Connection.JsonHelper;
import Model.Product_model;
import tech.iwish.onhome.MainActivity;
import tech.iwish.onhome.R;
import util.DatabaseHandler;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.strip;


public class Product_adapter extends RecyclerView.Adapter<Product_adapter.MyViewHolder>{

    private List<Product_model> modelList;
    private Context context;
    private DatabaseHandler dbcart;
    String language;
    SharedPreferences preferences;
    public  String id;

    ArrayList<String >map= new ArrayList<>();
    private int getid;
    private HashMap<String, String> mapManager;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_title, tv_price, tv_reward, tv_total, tv_contetiy, tv_add , old_price;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove;
        public Spinner  spinner;
        public Double reward;
        String id;

        public MyViewHolder(View view) {
            super(view);

            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            old_price = (TextView) view.findViewById(R.id.tv_subcat_price_old);
            //old_price.setText("old_price");
            old_price.setPaintFlags(old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv_reward = (TextView) view.findViewById(R.id.tv_reward_point);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            tv_add = (TextView) view.findViewById(R.id.tv_subcat_add);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);
            spinner = (Spinner) view.findViewById(R.id.spinner);


            iv_remove.setVisibility(View.GONE);
            iv_minus.setOnClickListener(this);
            iv_plus.setOnClickListener(this);
            tv_add.setOnClickListener(this);
            iv_logo.setOnClickListener(this);

            CardView cardView = (CardView) view.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);








        }




        @Override
        public void onClick(View view) {
            int id = view.getId();
//            int id = getitid();
            int position = getAdapterPosition();
            if (id == R.id.iv_subcat_plus) {
                int qty = Integer.valueOf(tv_contetiy.getText().toString());
                qty = qty + 1;
                tv_contetiy.setText(valueOf(qty));

            } else if (id == R.id.iv_subcat_minus) {

                int qty = 0;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    tv_contetiy.setText(valueOf(qty));
                }

            } else if (id == R.id.tv_subcat_add) {
                HashMap<String, String> map = new HashMap<>();
                preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
                language=preferences.getString("language","");


                map.put("product_id", modelList.get(position).getProduct_id());
                map.put("product_name", modelList.get(position).getProduct_name());
                map.put("category_id", modelList.get(position).getCategory_id());
                map.put("product_description", modelList.get(position).getProduct_description());
                map.put("deal_price", modelList.get(position).getDeal_price());
                map.put("start_date", modelList.get(position).getStart_date());
                map.put("start_time", modelList.get(position).getStart_time());
                map.put("end_date", modelList.get(position).getEnd_date());
                map.put("end_time", modelList.get(position).getEnd_time());
                map.put("price", modelList.get(position).getPrice());
                map.put("product_image", modelList.get(position).getProduct_image());
                map.put("status", modelList.get(position).getStatus());
                map.put("in_stock", modelList.get(position).getIn_stock());
                map.put("unit_value", modelList.get(position).getUnit_value());
                map.put("unit", modelList.get(position).getUnit());
                map.put("increament", modelList.get(position).getIncreament());
                map.put("rewards", modelList.get(position).getRewards());
                map.put("stock", modelList.get(position).getStock());
                map.put("title", modelList.get(position).getTitle());


                if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
                    if (dbcart.isInCart(map.get("product_id"))) {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    } else {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    }
                } else {
                    dbcart.removeItemFromCart(map.get("product_id"));
                    tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
                }
                Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));

                Double price = Double.parseDouble(map.get("price").trim());
                Double reward = Double.parseDouble(map.get("rewards"));
                tv_reward.setText("" + reward * items);
                tv_total.setText("" + price * items);
                ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());

            } else if (id == R.id.iv_subcat_img) {
                preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
                language=preferences.getString("language","");
                Log.d("lang",language);
//                if (language.contains("english")) {
//                    showProductDetail(modelList.get(position).getProduct_image(),
//                            modelList.get(position).getProduct_name(),
//                            modelList.get(position).getProduct_description(),
//                            "",
//                            position, tv_contetiy.getText().toString());
//                }else {
//                    showProductDetail(modelList.get(position).getProduct_image(),
//                            modelList.get(position).getProduct_name_arb(),
//                            modelList.get(position).getProduct_description_arb(),
//                            "",
//                            position, tv_contetiy.getText().toString());
//                }
            }

        }

    }

    private int getitid() {

       return getid;
    }

    private void setitid(int id)
    {
        getid = id;
    }


    public Product_adapter(List<Product_model> modelList, Context context) {
        this.modelList = modelList;
        dbcart = new DatabaseHandler(context);
    }

    @Override
    public void onBindViewHolder(final Product_adapter.MyViewHolder holder, int position) {
        final Product_model mList = modelList.get(position);

        //get select data
        ConnectionServer connectionServer= new ConnectionServer();
        connectionServer.requestedMethod("POST");
        connectionServer.set_url(Constants.DATA);
        if (mList.getProduct_name().contains (",")){
            //the string only contains numbers and commas
            connectionServer.buildParameter("name",mList.getProduct_name().substring(0, mList.getProduct_name().indexOf(",")));
        } else {
            //to do if there are invalid characters
            connectionServer.buildParameter("name",mList.getProduct_name());
        }

//        connectionServer.buildParameter("name",mList.getProduct_name().substring(0, mList.getProduct_name().indexOf(",")));
        connectionServer.execute(new ConnectionServer.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JsonHelper jsonHelper = new JsonHelper(output);
                Log.e("output" , output);
                if (jsonHelper.isValidJson()) {
                    String response = jsonHelper.GetResult("response");
                    if (response.equals("TRUE")) {
                        JSONArray jsonArray = jsonHelper.setChildjsonArray(jsonHelper.getCurrentJsonObj(), "data");

                        Log.e("json", valueOf(jsonArray));

//                        map.clear();

                        map =new ArrayList<>();
                        mapManager = new HashMap<String,String>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonHelper.setChildjsonObj(jsonArray, i);
                           map.add(jsonHelper.GetResult("product_name")+", Rs,"+jsonHelper.GetResult("price"));


                           mapManager.put(jsonHelper.GetResult("product_name")+", Rs,"+jsonHelper.GetResult("price"),jsonHelper.GetResult("product_id"));

                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,map);

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner

                     holder.spinner.setAdapter(dataAdapter);

                     holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                         @Override
                         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                             TextView selectedText = (TextView) parent.getChildAt(0);


                                String keyer = parent.getItemAtPosition(position).toString();
//                             ((TextView) spinner.getSelectedView()).setTextColor(getResources().getColor(R.color.Blue));

//                             Toast.makeText(context,holder.id, Toast.LENGTH_SHORT).show();
                             if (mapManager.size()==1) {

                             }
                             else
                             {
                                 String valueee = mapManager.get(keyer);
                                 // String[] separated = holder.id.split(",");
                                 String[] separated = keyer.split(",");
//                             Log.e("ljmioio",String.valueOf(separated[2]));
//                             String[] price= separated[2].split(",");
//                             Log.e("jknjkj",String.valueOf(price));
//                             Toast.makeText(context,price[2], Toast.LENGTH_SHORT).show();
//                             setitid(Integer.parseInt(separated[2]));
                                 mList.getProduct_name().isEmpty();
                                 mList.getProduct_id().isEmpty();
                                 mList.getPrice().isEmpty();
                                 mList.setProduct_name(separated[1]);
                                 mList.setPrice(separated[3]);
                                 mList.setProduct_id(valueee);

                             }


//
                         }

                         @Override
                         public void onNothingSelected(AdapterView<?> parent) {

                         }
                     });




                    }



                }

            }
        });








        //end of get select data

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mList.getProduct_image())
                .centerCrop()
                .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);
        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");
        if (language.contains("english")) {
            holder.tv_title.setText(mList.getProduct_name());
        }
        else {
            holder.tv_title.setText(mList.getProduct_name_arb());
        }
        holder.tv_reward.setText(mList.getRewards());
//-------------------------------------

        //if(Integer.valueOf(modelList.get(position).getPrice())== Integer.valueOf(modelList.get(position).getOldPrice())){
//        if(Integer.parseInt(modelList.get(position).getPrice())== Integer.valueOf(modelList.get(position).getOldPrice())){


        if(Integer.parseInt(modelList.get(position).getPrice())== Integer.valueOf(modelList.get(position).getOldPrice())){
            holder.tv_price.setText("Price " + mList.getPrice()+ context.getResources().getString(R.string.currency));  // price
            holder.old_price.setText("Price 0.00"+ context.getResources().getString(R.string.currency));  // price
            holder.old_price.setVisibility(View.GONE);
        }else{

//            Log.e("sno",modelList.get(position).getProduct_id());
//            Log.e("getprice", Integer.valueOf(modelList.get(position).getPrice()).toString());

//            Log.e("getprice", Integer.valueOf(modelList.get(position).getOldPrice()).toString());

            holder.tv_price.setText("Price " + mList.getPrice()+ context.getResources().getString(R.string.currency));  // price

            holder.old_price.setText("Price: "+mList.getOldPrice());  // old price
            holder.old_price.setVisibility(View.VISIBLE);
        }
//-----------------------------------------

//        holder.tv_price.setText("Price " + mList.getPrice()+ context.getResources().getString(R.string.currency));  // price
//
//        Log.e("price", mList.getPrice());
//
//        holder.old_price.setText("Price: "+mList.getOldPrice());  // old price

        // if(mList.getOldPrice()==null)
        // holder.old_price.setVisibility(View.GONE);


//        Log.e("sno",modelList.get(position).getProduct_id());
//        Log.e("stock",Integer.valueOf(modelList.get(position).getStock()).toString());
//        Log.e("get_in_stock",Integer.valueOf(modelList.get(position).getIn_stock()).toString());

        if ((Integer.parseInt(modelList.get(position).getStock())<=0) || (Integer.valueOf(modelList.get(position).getIn_stock())<=0)){

            holder.tv_add.setText(R.string.tv_out);
            holder.tv_add.setTextColor(context.getResources().getColor(R.color.black));
            holder.tv_add.setBackgroundColor(context.getResources().getColor(R.color.gray));
            holder.tv_add.setEnabled(false);
            holder.iv_minus.setEnabled(false);
            holder.iv_plus.setEnabled(false);

        }

        else if (dbcart.isInCart(mList.getProduct_id())) {
            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            holder.tv_contetiy.setText(dbcart.getCartItemQty(mList.getProduct_id()));

        } else {
            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
            holder.tv_add.setBackgroundColor(context.getResources().getColor(R.color.green));
            holder.tv_add.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_add.setEnabled(true);
            holder.iv_minus.setEnabled(true);
            holder.iv_plus.setEnabled(true);
        }

        Double items = Double.parseDouble(dbcart.getInCartItemQty(mList.getProduct_id()));
        Double price = Double.parseDouble(mList.getPrice());
        Double reward = Double.parseDouble(mList.getRewards());
        holder.tv_total.setText("" + price * items);
        holder.tv_reward.setText("" + reward * items);

    }



    @Override
    public Product_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_rv, parent, false);
        context = parent.getContext();
        return new Product_adapter.MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    private void showImage(String image) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.product_image_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        ImageView iv_image_cancle = (ImageView) dialog.findViewById(R.id.iv_dialog_cancle);
        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_dialog_img);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + image)
                .centerCrop()
                .placeholder(R.drawable.icon)
                .crossFade()
                .into(iv_image);

        iv_image_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void showProductDetail(String image, String title, String description, String detail, final int position, String qty) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_product_detail);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_product_detail_img);
        final ImageView iv_minus = (ImageView) dialog.findViewById(R.id.iv_subcat_minus);
        final ImageView iv_plus = (ImageView) dialog.findViewById(R.id.iv_subcat_plus);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_product_detail_title);
        TextView tv_detail = (TextView) dialog.findViewById(R.id.tv_product_detail);
        final TextView tv_contetiy = (TextView) dialog.findViewById(R.id.tv_subcat_contetiy);
        final TextView tv_add = (TextView) dialog.findViewById(R.id.tv_subcat_add);

        tv_title.setText(title);
        tv_detail.setText(detail);
        tv_contetiy.setText(qty);
        tv_detail.setText(description);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + image)
                .crossFade()
                .into(iv_image);
        if (Integer.valueOf(modelList.get(position).getStock())<=0){
            tv_add.setText(R.string.tv_out);
            tv_add.setTextColor(context.getResources().getColor(R.color.black));
            tv_add.setBackgroundColor(context.getResources().getColor(R.color.green));   //   change
            tv_add.setEnabled(false);
            iv_minus.setEnabled(false);
            iv_plus.setEnabled(false);
        }

        else if (dbcart.isInCart(modelList.get(position).getProduct_id())) {
            tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            tv_contetiy.setText(dbcart.getCartItemQty(modelList.get(position).getProduct_id()));
        } else {
            tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
        }

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
                language=preferences.getString("language","");

                map.put("product_id", modelList.get(position).getProduct_id());
                map.put("product_name", modelList.get(position).getProduct_name());
                map.put("category_id", modelList.get(position).getCategory_id());
                map.put("product_description", modelList.get(position).getProduct_description());
                map.put("deal_price", modelList.get(position).getDeal_price());
                map.put("start_date", modelList.get(position).getStart_date());
                map.put("start_time", modelList.get(position).getStart_time());
                map.put("end_date", modelList.get(position).getEnd_date());
                map.put("end_time", modelList.get(position).getEnd_time());
                map.put("price", modelList.get(position).getPrice());
                map.put("product_image", modelList.get(position).getProduct_image());
                map.put("status", modelList.get(position).getStatus());
                map.put("in_stock", modelList.get(position).getIn_stock());
                map.put("unit_value", modelList.get(position).getUnit_value());
                map.put("unit", modelList.get(position).getUnit());
                map.put("increament", modelList.get(position).getIncreament());
                map.put("rewards", modelList.get(position).getRewards());
                map.put("stock", modelList.get(position).getStock());
                map.put("title", modelList.get(position).getTitle());

                if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
                    if (dbcart.isInCart(map.get("product_id"))) {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    } else {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    }
                } else {
                    dbcart.removeItemFromCart(map.get("product_id"));
                    tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
                }
                Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
                Double price = Double.parseDouble(map.get("price"));
                ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());

                notifyItemChanged(position);

            }
        });

        iv_plus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int qty = Integer.valueOf(tv_contetiy.getText().toString());
                        qty = qty + 1;

                        tv_contetiy.setText(valueOf(qty));
                    }
                });

        iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 0;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    tv_contetiy.setText(valueOf(qty));
                }
            }
        });

    }





}