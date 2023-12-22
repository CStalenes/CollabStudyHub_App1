package com.example.collabstudyhub_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SearchHubmates_Activity extends AppCompatActivity {

    private RecyclerView searResult_RV;
    private Button search_Btn;
    private EditText critere_ET;
    private TextView backToHome_TextView;

    private StudentAdapter studentAdapter;
    private Spinner spinner, spinner_levels_search;
    private ArrayAdapter<CharSequence> levelAdaptersearch;
    private String selectedLevelSearch;
    private String entered_condition_value;

    private String rv_query_condition = "studentname";
    private String Filter_query_condition = "studentname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hubmates);

        getSupportActionBar().setTitle("CollabStudy Hub");

        String[] conditionToQuery = {"Nom", "Email adresse", "Niveau","Etablissement"};

        searResult_RV = findViewById(R.id.recyclerView_searchResult);
        critere_ET = findViewById(R.id.editText_critere);
        search_Btn = findViewById(R.id.button_Search);
        backToHome_TextView = findViewById(R.id.tvBackToHome);

        searResult_RV.setLayoutManager(new LinearLayoutManager(this));
        spinner = findViewById(R.id.spinnerForQuery);

        // spinner for level and fields
        spinner_levels_search = findViewById(R.id.spinnerlevelSearch);

        // populate levelAdapter using string array from ressource
        levelAdaptersearch = ArrayAdapter.createFromResource(SearchHubmates_Activity.this,R.array.levels, android.R.layout.simple_spinner_item);
        levelAdaptersearch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_levels_search.setAdapter(levelAdaptersearch);

        levelAdaptersearch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // when any item if spinner_level is selected
        // obtain the selected level
        spinner_levels_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                // obtain the selected level
                selectedLevelSearch = spinner_levels_search.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // hide spinner level and field
        spinner_levels_search.setVisibility(View.GONE);

        // populate the spinner for search condition
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(SearchHubmates_Activity.this, android.R.layout.simple_spinner_item, conditionToQuery);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);

        // get selected condition spinner item
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selected_value = parent.getItemAtPosition(position).toString();

                if (selected_value == "Nom")
                {
                    rv_query_condition = "studentname";

                    entered_condition_value = "1";

                    // show spinner field and edit text or hide them
                    spinner_levels_search.setVisibility(View.GONE);
                    critere_ET.setVisibility(View.VISIBLE);

                } else if (selected_value == "Email adresse")
                {
                    rv_query_condition = "studentemail";
                    entered_condition_value = "1";

                    // show spinner field and edit text or hide them
                    spinner_levels_search.setVisibility(View.GONE);
                    critere_ET.setVisibility(View.VISIBLE);

                } else if (selected_value == "Etablissement")
                {
                    rv_query_condition = "studentetablissement";
                    entered_condition_value = "1";

                    // show spinner field and edit text or hide them
                    spinner_levels_search.setVisibility(View.GONE);
                    critere_ET.setVisibility(View.VISIBLE);

                }else if (selected_value == "Niveau")
                {
                    rv_query_condition = "studentniveau";
                    entered_condition_value = "2";

                    // show spinner field and edit text or hide them
                    spinner_levels_search.setVisibility(View.VISIBLE);
                    critere_ET.setVisibility(View.GONE);

                }

                critere_ET.setHint(rv_query_condition);
                critere_ET.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        search_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // test if user want to search according to name, email or school
                if(entered_condition_value == "1")
                {
                    String edit_text_data = critere_ET.getText().toString().trim();
                    // if user didnt enter  a condition data into edit text
                    if (edit_text_data.isEmpty())
                    {
                        Toast.makeText(SearchHubmates_Activity.this, "Veuillez entrer ce que vous recherchez", Toast.LENGTH_SHORT).show();
                    }
                    // if user entered a condition data into edit text
                    else
                    {
                        searchHubMates(rv_query_condition, edit_text_data);
                    }
                }
                // test if user want to search according to level
                else if (entered_condition_value == "2")
                {
                    searchHubMates(rv_query_condition, selectedLevelSearch);
                }
            }
        });

        backToHome_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchHubmates_Activity.this,MainActivity.class));
                finish();
            }
        });
    }

    private void searchHubMates(String conditionSearch, String valueSearch) {

        Query query = FirebaseDatabase.getInstance().getReference("Students")
                .orderByChild(conditionSearch)
                .equalTo(valueSearch);

        FirebaseRecyclerOptions<ModelStudent> options =
                new FirebaseRecyclerOptions.Builder<ModelStudent>()
                        .setQuery(query, ModelStudent.class)
                        .build();

           studentAdapter = new StudentAdapter(options,SearchHubmates_Activity.this);
           searResult_RV.setAdapter(studentAdapter);
           studentAdapter.startListening();

    }

    // for filtre menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.filtre_serach_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtFilter(query);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    private void txtFilter(String str)
    {
        FirebaseRecyclerOptions<ModelStudent> options = new FirebaseRecyclerOptions.Builder<ModelStudent>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Students").orderByChild(Filter_query_condition).startAt(str.toLowerCase()).endAt(str.toLowerCase() + "~"),ModelStudent.class)
                .build();

        studentAdapter = new StudentAdapter(options, SearchHubmates_Activity.this);
        searResult_RV.setAdapter(studentAdapter);
        studentAdapter.startListening();
    }

}