package com.unixverso.anotai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "AnotAIPrefs";
    public static final String KEY_SORT_ORDER = "sortOrder";
    public static final String KEY_AUTO_FILL = "autoFill";
    public static final String KEY_DARK_MODE = "darkMode";
    public static final String KEY_LANG = "language";

    public static final int SORT_AZ = 0;
    public static final int SORT_ZA = 1;
    
    public static final String LANG_EN = "en";
    public static final String LANG_PT = "pt";

    private RadioGroup radioGroupSort, radioGroupLang;
    private CheckBox checkBoxAutoFill;
    private SwitchCompat switchDarkMode;
    private Button btnRestore;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.settings);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        initComponents();
        loadPreferences();
        setupListeners();
    }

    private void initComponents() {
        radioGroupSort = findViewById(R.id.radioGroupSort);
        radioGroupLang = findViewById(R.id.radioGroupLang);
        checkBoxAutoFill = findViewById(R.id.checkBoxAutoFill);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        btnRestore = findViewById(R.id.btnRestoreDefaults);
    }

    private void loadPreferences() {
        int sortOrder = sharedPreferences.getInt(KEY_SORT_ORDER, SORT_AZ);
        if (sortOrder == SORT_AZ) {
            radioGroupSort.check(R.id.radioSortAZ);
        } else {
            radioGroupSort.check(R.id.radioSortZA);
        }

        String lang = sharedPreferences.getString(KEY_LANG, LANG_PT);
        if (lang.equals(LANG_EN)) {
            radioGroupLang.check(R.id.radioLangEn);
        } else {
            radioGroupLang.check(R.id.radioLangPt);
        }

        checkBoxAutoFill.setChecked(sharedPreferences.getBoolean(KEY_AUTO_FILL, false));
        switchDarkMode.setChecked(sharedPreferences.getBoolean(KEY_DARK_MODE, false));
    }

    private void setupListeners() {
        radioGroupSort.setOnCheckedChangeListener((group, checkedId) -> {
            int sortOrder = (checkedId == R.id.radioSortAZ) ? SORT_AZ : SORT_ZA;
            sharedPreferences.edit().putInt(KEY_SORT_ORDER, sortOrder).apply();
        });

        radioGroupLang.setOnCheckedChangeListener((group, checkedId) -> {
            String lang = (checkedId == R.id.radioLangEn) ? LANG_EN : LANG_PT;
            String currentLang = sharedPreferences.getString(KEY_LANG, LANG_PT);
            if (!lang.equals(currentLang)) {
                sharedPreferences.edit().putString(KEY_LANG, lang).apply();
                updateLocale(lang);
                recreate(); // Recria a tela para aplicar o novo idioma
            }
        });

        checkBoxAutoFill.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_AUTO_FILL, isChecked).apply();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            applyDarkMode(isChecked);
        });

        btnRestore.setOnClickListener(v -> restoreDefaults());
    }

    private void updateLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    private void applyDarkMode(boolean isEnabled) {
        if (isEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void restoreDefaults() {
        sharedPreferences.edit().clear().apply();
        loadPreferences();
        applyDarkMode(false);
        updateLocale(LANG_PT);
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}