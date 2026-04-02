package com.unixverso.anotai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextSuggestion, editTextObs;
    private Spinner spinnerCategory;
    private RadioGroup radioGroupPrior;
    private RadioButton radioLow, radioMedium, radioHigh;
    private CheckBox checkBoxFromFriend, checkBoxHighLevel, checkBoxIsSeries;
    private ArrayList<String> categories;
    
    private Suggestion suggestionToEdit;
    private int editPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String[] array = getResources().getStringArray(R.array.categories_array);
        categories = new ArrayList<>(Arrays.asList(array));

        initComponents();
        setupSpinner();

        Intent intent = getIntent();
        if (intent.hasExtra("suggestion")) {
            suggestionToEdit = (Suggestion) intent.getSerializableExtra("suggestion");
            editPosition = intent.getIntExtra("position", -1);
            populateForm();
            setTitle(getString(R.string.editar));
        } else {
            setTitle(getString(R.string.title_register_suggestion));
            checkAutoFill();
        }
    }

    private void checkAutoFill() {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        boolean autoFill = prefs.getBoolean(SettingsActivity.KEY_AUTO_FILL, false);
        
        if (autoFill) {
            String[] suggestions = getResources().getStringArray(R.array.suggestion_name);
            int randomIndex = new Random().nextInt(suggestions.length);
            editTextSuggestion.setText(suggestions[randomIndex]);
            
            // Categoria aleatória
            spinnerCategory.setSelection(new Random().nextInt(categories.size()));
            
            // Prioridade aleatória
            int randomPrior = new Random().nextInt(3);
            if (randomPrior == 0) radioLow.setChecked(true);
            else if (randomPrior == 1) radioMedium.setChecked(true);
            else radioHigh.setChecked(true);
        }
    }

    private void initComponents() {
        editTextSuggestion = findViewById(R.id.editTextSuggestion);
        editTextObs = findViewById(R.id.editTextObs);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        radioGroupPrior = findViewById(R.id.radioGroupPrior);
        radioLow = findViewById(R.id.radioBaixa);
        radioMedium = findViewById(R.id.radioMedia);
        radioHigh = findViewById(R.id.radioAlta);
        checkBoxFromFriend = findViewById(R.id.checkBoxFromFriend);
        checkBoxHighLevel = findViewById(R.id.checkBoxHighLevel);
        checkBoxIsSeries = findViewById(R.id.checkBoxIsSeries);
    }

    private void setupSpinner() {
        if (categories != null && categories.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    categories
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
        }
    }

    private void populateForm() {
        if (suggestionToEdit != null) {
            editTextSuggestion.setText(suggestionToEdit.getName());
            editTextObs.setText(suggestionToEdit.getObs());
            
            if (suggestionToEdit.getPriority() == EnumPriority.ALTA) {
                radioHigh.setChecked(true);
            } else if (suggestionToEdit.getPriority() == EnumPriority.MEDIA) {
                radioMedium.setChecked(true);
            } else {
                radioLow.setChecked(true);
            }

            checkBoxFromFriend.setChecked(suggestionToEdit.isFriendSuggestion());
            checkBoxHighLevel.setChecked(suggestionToEdit.isUrgent());
            checkBoxIsSeries.setChecked(suggestionToEdit.isSerie());

            // Usa o índice salvo para selecionar a categoria, garantindo a tradução correta
            spinnerCategory.setSelection(suggestionToEdit.getCategoryIndex());
        }
    }

    private void clearForm() {
        editTextSuggestion.setText("");
        editTextObs.setText("");

        radioGroupPrior.clearCheck();
        radioLow.setChecked(true);

        checkBoxFromFriend.setChecked(false);
        checkBoxHighLevel.setChecked(false);
        checkBoxIsSeries.setChecked(false);

        spinnerCategory.setSelection(0);

        Toast.makeText(
                this,
                R.string.form_cleared,
                Toast.LENGTH_SHORT).show();

        editTextSuggestion.requestFocus();
    }

    private void salvarForm() {
        String nome = editTextSuggestion.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, R.string.fill_suggestion, Toast.LENGTH_SHORT).show();
            editTextSuggestion.requestFocus();
            return;
        }

        int selectedRadioId = radioGroupPrior.getCheckedRadioButtonId();
        if (selectedRadioId == -1) {
            Toast.makeText(this, R.string.choose_prior, Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryIndex = spinnerCategory.getSelectedItemPosition();
        String obs = editTextObs.getText().toString();

        EnumPriority priority;
        if (radioHigh.isChecked()) {
            priority = EnumPriority.ALTA;
        } else if (radioMedium.isChecked()) {
            priority = EnumPriority.MEDIA;
        } else {
            priority = EnumPriority.BAIXA;
        }

        Suggestion suggestion;
        if (suggestionToEdit != null) {
            suggestion = suggestionToEdit;
            suggestion.setName(nome);
            suggestion.setCategoryIndex(categoryIndex);
            suggestion.setPriority(priority);
            suggestion.setFriendSuggestion(checkBoxFromFriend.isChecked());
            suggestion.setUrgent(checkBoxHighLevel.isChecked());
            suggestion.setSerie(checkBoxIsSeries.isChecked());
            suggestion.setObs(obs);
        } else {
            suggestion = new Suggestion(
                    System.currentTimeMillis(),
                    nome,
                    categoryIndex,
                    priority,
                    checkBoxFromFriend.isChecked(),
                    checkBoxHighLevel.isChecked(),
                    checkBoxIsSeries.isChecked(),
                    obs
            );
        }

        Intent intent = new Intent();
        intent.putExtra("suggestion", suggestion);
        if (editPosition != -1) {
            intent.putExtra("position", editPosition);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuItemSalvar) {
            salvarForm();
            return true;
        } else if (id == R.id.menuItemLimpar) {
            clearForm();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}