package com.unixverso.anotai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextSuggestion,editTextObs;
    private Spinner spinnerCategory;
    private RadioGroup radioGroupPrior;
    private RadioButton radioLow,radioMedium,radioHigh;
    private CheckBox checkBoxFromFriend,checkBoxHighLevel,checkBoxIsSeries;
    private Button btnSave,btnClear;
    private ArrayList<String> categories;

//    private final String[] categories = {
//            getString(R.string.movie),
//            getString(R.string.serie),
//            getString(R.string.book),
//            getString(R.string.restaurant),
//            getString(R.string.travel),
//            getString(R.string.tour),
//            getString(R.string.other)
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        String[] array = getResources().getStringArray(R.array.categories_array);
        categories = new ArrayList<>(Arrays.asList(array));

        initComponents();
        setupSpinner();
        setupButtons();
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
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);
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


    private void setupButtons() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarForm();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearForm();
            }
        });
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
                "Formulário limpo com sucesso!",
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

        RadioButton radioButtonSelecionado = findViewById(selectedRadioId);
        String prior = radioButtonSelecionado.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String obs = editTextObs.getText().toString();

        EnumPriority priority;
        if (radioHigh.isChecked()) {
            priority = EnumPriority.ALTA;
        } else if (radioMedium.isChecked()) {
            priority = EnumPriority.MEDIA;
        } else {
            priority = EnumPriority.BAIXA;
        }

        Suggestion suggestion = new Suggestion(
                System.currentTimeMillis(), // id simples
                nome,
                category,
                priority,
                checkBoxFromFriend.isChecked(),
                checkBoxHighLevel.isChecked(),
                checkBoxIsSeries.isChecked(),
                obs
        );

        Intent intent = new Intent();
        intent.putExtra("suggestion", suggestion);

        setResult(RESULT_OK, intent);
        finish();
    }
}