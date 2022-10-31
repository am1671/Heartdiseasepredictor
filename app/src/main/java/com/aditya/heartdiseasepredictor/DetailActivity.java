package com.aditya.heartdiseasepredictor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private EditText cp, thalach, slope, restecg, chol, trestbps, fbs, oldpeak, ageEdit, gender, exang, ca, thal;
    private Button predict;
    private ImageButton info1, info2, info3, info4, info5, info6, info7, info8;
    private TextView result;
    private Button tips;
    String url = "https://hearth-disease-prediction-app.herokuapp.com/predict";

    // Trials

    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    // creating a variable for
    // our object class
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        cp = findViewById(R.id.cp);
        thalach = findViewById(R.id.thalach);
        slope = findViewById(R.id.slope);
        restecg = findViewById(R.id.restecg);
        chol = findViewById(R.id.chol);
        trestbps = findViewById(R.id.trestbps);
        fbs = findViewById(R.id.fbs);
        oldpeak = findViewById(R.id.oldpeak);
        predict = findViewById(R.id.predict);
        ageEdit = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        exang = findViewById(R.id.exang);
        ca = findViewById(R.id.ca);
        thal = findViewById(R.id.thal);
        info1 = findViewById(R.id.info1);
        info2 = findViewById(R.id.info2);
        info3 = findViewById(R.id.info3);
        info4 = findViewById(R.id.info4);
        info5 = findViewById(R.id.info5);
        info6 = findViewById(R.id.info6);
        info7 = findViewById(R.id.info7);
        info8 = findViewById(R.id.info8);
        result = findViewById(R.id.result);
        tips = findViewById(R.id.tips);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("UserInfo");

        userInfo = new UserInfo();

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cp.getText().toString().isEmpty() || (!cp.getText().toString().equals("0")
                        && !cp.getText().toString().equals("1") && !cp.getText().toString().equals("2") && !cp.getText().toString().equals("3"))){
                    cp.setError("Should be in 0-3 range");
                }else if (thalach.getText().toString().isEmpty() || Integer.parseInt(thalach.getText().toString()) < 0){
                    thalach.setError("Cannot be Empty");
                }else if (slope.getText().toString().isEmpty() || (!slope.getText().toString().equals("0")
                        && !slope.getText().toString().equals("1") && !slope.getText().toString().equals("2"))){
                    slope.setError("Should be in 0-2 range");
                }else if (restecg.getText().toString().isEmpty() || (!restecg.getText().toString().equals("0")
                        && !restecg.getText().toString().equals("1") && !restecg.getText().toString().equals("2"))){
                    restecg.setError("Should be in 0-2 range");
                }else if (chol.getText().toString().isEmpty() || Integer.parseInt(chol.getText().toString()) < 0){
                    chol.setError("Cannot be Empty");
                }else if (trestbps.getText().toString().isEmpty() || Integer.parseInt(trestbps.getText().toString()) < 0){
                    trestbps.setError("Cannot be Empty");
                }else if (fbs.getText().toString().isEmpty() || (!fbs.getText().toString().equals("0") && !fbs.getText().toString().equals("1"))){
                    fbs.setError("Should be in 0-1 range");
                }else if (oldpeak.getText().toString().isEmpty() || Float.parseFloat(oldpeak.getText().toString()) < 0){
                    oldpeak.setError("Cannot be Empty");
                }else {
                    //API -> Volley

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @SuppressLint("WrongConstant")
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String data = jsonObject.getString("hearth_disease");
                                        tips.setVisibility(1);

                                        if (data.equals("0")){
                                            result.setTextColor(Color.parseColor("#5bdeac"));
                                            result.setText("Patient likely does not have Heart Disease");
                                            String ageSave = ageEdit.getText().toString();
                                            addDatatoFirebase(ageSave);
                                        }else {
                                            result.setTextColor(Color.parseColor("#EC4C4C"));
                                            result.setText("Patient likely has Heart Disease");
                                            String ageSave = ageEdit.getText().toString();
                                            addDatatoFirebase(ageSave);
                                        }

                                        cp.setText("");
                                        thalach.setText("");
                                        slope.setText("");
                                        restecg.setText("");
                                        chol.setText("");
                                        trestbps.setText("");
                                        fbs.setText("");
                                        oldpeak.setText("");
                                        ageEdit.setText("");
                                        gender.setText("");
                                        ca.setText("");
                                        thal.setText("");
                                        exang.setText("");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String err = (error.getMessage()==null)?"Failed! Please Try Again":error.getMessage();
                            Toast.makeText(DetailActivity.this,err,Toast.LENGTH_SHORT).show();
                            Log.d("API ERROR : ", err);
                        }
                    }){
                        @Override
                        protected Map<String,String> getParams(){

                            Map<String,String> params = new HashMap<String, String>();
                            params.put("cp",cp.getText().toString());
                            params.put("thalach",thalach.getText().toString());
                            params.put("slope",slope.getText().toString());
                            params.put("restecg",restecg.getText().toString());
                            params.put("chol",chol.getText().toString());
                            params.put("trestbps",trestbps.getText().toString());
                            params.put("fbs",fbs.getText().toString());
                            params.put("oldpeak",oldpeak.getText().toString());

                            return params;
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
                    queue.add(stringRequest);
                }
            }
        });


        info1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "It should display the type of chest-pain experienced by the individual using the following format :\n" +
                        "0 = typical angina\n" +
                        "1 = atypical angina\n" +
                        "2 = non — anginal pain\n" +
                        "3 = asymptotic";
                infoDialog("Chest-pain type:",info);
            }
        });

        info2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "It should display the max heart rate achieved by an individual.";
                infoDialog("Max heart rate:",info);
            }
        });

        info3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "Peak exercise ST segment :\n" +
                        "0 = upsloping\n" +
                        "1 = flat\n" +
                        "2 = downsloping";
                infoDialog("Exercise ST:",info);
            }
        });

        info4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "It should display resting electrocardiographic results\n" +
                        "0 = normal\n" +
                        "1 = having ST-T wave abnormality\n" +
                        "2 = left ventricular hyperthrophy";
                infoDialog("Resting ECG:",info);
            }
        });

        info5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "It should display the serum cholesterol in mg/dl (unit)";
                infoDialog("Cholestrol:",info);
            }
        });

        info6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "It should display the resting blood pressure value of an individual in mmHg (unit)";
                infoDialog("Resting Blood Pressure:",info);
            }
        });

        info7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "It compares the fasting blood sugar value of an individual with 120mg/dl.\n" +
                        "If fasting blood sugar > 120mg/dl then : 1 (true)\n" +
                        "else : 0 (false)";
                infoDialog("Fasting Blood Sugar:",info);
            }
        });

        info8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = "ST depression induced by exercise relative to rest, should display the value which is an integer or float. Write zero (0) if nothing.";
                infoDialog("ST depression:",info);
            }
        });

        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this,InfoActivity.class));
                finish();
            }
        });
    }

    private void addDatatoFirebase(String ageSave) {
        // below 3 lines of code is used to set
        // data in our object class.
        userInfo.setAges(ageSave);

        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                databaseReference.setValue(userInfo);

                // after adding this data we are showing toast message.
                Toast.makeText(DetailActivity.this, "data added", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(DetailActivity.this, "Fail to add data " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void infoDialog(String i, String string) {

        Dialog dialog;
        dialog = new Dialog(DetailActivity.this);

        dialog.setContentView(R.layout.info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView close = dialog.findViewById(R.id.closeDialog);
        TextView nameDialog = dialog.findViewById(R.id.nameDialog);
        TextView infoDialog = dialog.findViewById(R.id.infoDialog);

        nameDialog.setText(""+i);
        infoDialog.setText(string);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

  }
