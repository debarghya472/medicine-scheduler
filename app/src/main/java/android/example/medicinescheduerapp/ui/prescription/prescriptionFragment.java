package android.example.medicinescheduerapp.ui.prescription;

import android.content.Context;
import android.content.SharedPreferences;
import android.example.medicinescheduerapp.JsonPlaceholderApi;
import android.example.medicinescheduerapp.ui.LoadDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.example.medicinescheduerapp.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.example.medicinescheduerapp.ui.findPatientActivity.buttonCount;
import static android.example.medicinescheduerapp.ui.findPatientActivity.mlistItem;


public class prescriptionFragment extends Fragment {
    private EditText patWeight;
    private EditText patSymptoms;
    private EditText patDate;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private JsonPlaceholderApi jsonPlaceholderApi;
    private Bundle bundle;
    private LoadDialog loadDialog;

    public prescriptionFragment(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prescription, container, false);
        patSymptoms= root.findViewById(R.id.pres_symptoms);
        patWeight= root.findViewById(R.id.pres_weight);
        patDate =root.findViewById(R.id.pres_date);

        FloatingActionButton add_med = root.findViewById(R.id.add_med);
        add_med.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCount=buttonCount+1;
                Log.d("TAg","mess  "+buttonCount);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_auth_container_1,new addMedPresFragment(bundle))
                        .commit();
            }
        });
        Button backpress =root.findViewById(R.id.backbutton);
        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle=patientPrescribeFragment.getBundle();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_auth_container_1,new patientPrescribeFragment(bundle))
                        .commit();

            }
        });

//        String pat_email = info.getString("patientEmail",null);
//        String medName = info.getString("medName",null);
//        String medDur= info.getString("medDur",null);
//        String medDos = info.getString("medDos",null);

        recyclerView = root.findViewById(R.id.recycler_view_medicine);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        if(this.getArguments()!=null){
            String medname = this.getArguments().getString("medname");
            String meddur = this.getArguments().getString("meddur");
            String meddos = this.getArguments().getString("meddos");
            if(medname!=null|| meddur!=null|| meddos!=null){
                Log.d("TAg","mess  "+buttonCount);
                mlistItem.add((buttonCount-1),new Medicines(medname,meddur,meddos));

            }
        }
        
        adapter = new PrescriptionAdapter(mlistItem,getActivity());
        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.save_pres_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.save_pres: savePres();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    private void savePres(){
        loadDialog =new LoadDialog(getActivity());
        loadDialog.startLoad();
        Gson gson = new GsonBuilder().serializeNulls().create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.SECONDS )
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://darshil.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi.class);

        String pat_symptoms = patSymptoms.getText().toString();
        int pat_weight = Integer.parseInt(patWeight.getText().toString());
        String pat_date =patDate.getText().toString();
        String email = bundle.getString("DataEmail");

        SharedPreferences info =getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        String token = info.getString("token","Null");

//        Log.d("Tag","mess  "+pat_symptoms+" "+ pat_date);

        PrescriptionPost prescriptionPost =new PrescriptionPost(email,pat_date,pat_weight,pat_symptoms,mlistItem,null);
        Call<PrescriptionPost> call =jsonPlaceholderApi.addPrescription("Bearer "+token,prescriptionPost);
        call.enqueue(new Callback<PrescriptionPost>() {
            @Override
            public void onResponse(Call<PrescriptionPost> call, Response<PrescriptionPost> response) {
                if(response.isSuccessful())
                    Toast.makeText(getActivity(),"Prescription added Successfully",Toast.LENGTH_SHORT).show();
                loadDialog.dismissLoad();
            }

            @Override
            public void onFailure(Call<PrescriptionPost> call, Throwable t) {
                Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
                loadDialog.dismissLoad();
            }

        });
    }

}