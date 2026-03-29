package com.unixverso.anotai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listViewSuggestion;
    private List<Suggestion> suggestionList;
    private SuggestionAdapter suggestionAdapter;
    //private ActivityResultLauncher<Intent> registerLauncher; //moderno
    private static final int REQUEST_REGISTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle(getString(R.string.title_list_suggestion));
        listViewSuggestion = findViewById(R.id.listViewSuggestion);
        listViewSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Suggestion selectedSuggestion = suggestionList.get(i);
                String message = "Sua sugestão clicada é \""
                        +selectedSuggestion.getCategory()+": "
                        + selectedSuggestion.getName() + "\"\n";


                Toast.makeText(ListActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        initializeSuggestionList();

        //setupCadastroLauncher();
    }

    private void initializeSuggestionList() {
        suggestionList = new ArrayList<>();
        suggestionAdapter = new SuggestionAdapter(this, suggestionList);
        listViewSuggestion.setAdapter(suggestionAdapter);
//        String[] names = getResources().getStringArray(R.array.suggestion_name);
//        String[] categories = getResources().getStringArray(R.array.categories);
//        int[] priors = getResources().getIntArray(R.array.priority);
//        int[] isFriendSuggestions = getResources().getIntArray(R.array.is_friend_suggestion);
//        int[] isUrgents = getResources().getIntArray(R.array.is_urgent);
//        int[] isSeries = getResources().getIntArray(R.array.is_serie);
//
//        EnumPriority[] priorities = EnumPriority.values();
//        suggestionList = new ArrayList<>();
//
//        for (int i = 0; i < names.length; i++) {
//            EnumPriority priority;
//            switch (priors[i]) {
//                case 0:
//                    priority = EnumPriority.ALTA;
//                    break;
//                case 1:
//                    priority = EnumPriority.MEDIA;
//                    break;
//                case 2:
//                    priority = EnumPriority.BAIXA;
//                    break;
//                default:
//                    priority = EnumPriority.MEDIA;
//            }
//
//            Suggestion suggestion = new Suggestion(
//                    Long.valueOf(i),
//                    names[i],
//                    categories[i],
//                    priority,
//                    isFriendSuggestions[i] == 1 ? true : false,
//                    isUrgents[i] == 1 ? true : false,
//                    isSeries[i] == 1 ? true : false,
//                    "Nada"
//            );
//
//            suggestionList.add(suggestion);
//        }
//
//        ArrayAdapter<Suggestion> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_list_item_1,
//                suggestionList
//        );
//
//        suggestionAdapter = new SuggestionAdapter(this, suggestionList);
//
//        listViewSuggestion.setAdapter(suggestionAdapter);
    }


//    private void setupCadastroLauncher() {
//        registerLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//
//                        Intent data = result.getData();
//
//                        Suggestion suggestion = (Suggestion) data.getSerializableExtra("suggestion");
//
//                        if (suggestion != null) {
//                            suggestionList.add(suggestion);
//                            suggestionAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//        );
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_REGISTER && resultCode == RESULT_OK && data != null) {

            Suggestion suggestion = (Suggestion) data.getSerializableExtra("suggestion");

            if (suggestion != null) {
                suggestionList.add(suggestion);
                suggestionAdapter.notifyDataSetChanged();
            }
        }
    }
    public void openAbout(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
        public void openRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_REGISTER);
        //registerLauncher.launch(intent);//moderno
    }
}