package com.unixverso.anotai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    private ListView listViewSuggestion;
    private List<Suggestion> suggestionList;
    private SuggestionAdapter suggestionAdapter;
    private static final int REQUEST_REGISTER = 1;
    private static final int REQUEST_EDIT = 2;

    private TextView textViewFeatured, textViewFeaturedValue;
    private static final String KEY_SUGGESTION_LIST = "suggestionListJson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySettings();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        updateTitle();

        listViewSuggestion = findViewById(R.id.listViewSuggestion);
        textViewFeatured = findViewById(R.id.textViewFeatured);
        textViewFeaturedValue = findViewById(R.id.textViewFeaturedValue);
        
        loadSuggestionList();
        setupContextMenu();

        listViewSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Suggestion selectedSuggestion = suggestionList.get(i);
                String[] categories = getResources().getStringArray(R.array.categories_array);
                String category = "";
                if (selectedSuggestion.getCategoryIndex() >= 0 && selectedSuggestion.getCategoryIndex() < categories.length) {
                    category = categories[selectedSuggestion.getCategoryIndex()];
                }
                
                String message = getString(R.string.app_name) + ": "
                        + category + " - "
                        + selectedSuggestion.getName();

                Toast.makeText(ListActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTitle() {
        setTitle(getString(R.string.title_list_suggestion));
    }

    private void applySettings() {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        
        // Aplicar Idioma
        String lang = prefs.getString(SettingsActivity.KEY_LANG, SettingsActivity.LANG_PT);
        updateLocale(lang);

        // Aplicar Modo Noturno
        boolean isDark = prefs.getBoolean(SettingsActivity.KEY_DARK_MODE, false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void updateLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Aplica as configurações novamente ao voltar da tela de Settings
        applySettings();
        
        // Atualiza o título da tela se o idioma mudou
        updateTitle();

        // Recarrega a lista para garantir persistência e ordenação
        loadSuggestionList();
        
        // Atualiza visibilidade do destaque
        if (suggestionList != null && !suggestionList.isEmpty()) {
            textViewFeatured.setVisibility(View.VISIBLE);
            textViewFeaturedValue.setVisibility(View.VISIBLE);
        } else {
            textViewFeatured.setVisibility(View.GONE);
            textViewFeaturedValue.setVisibility(View.GONE);
        }

        sortList();
        suggestionAdapter.notifyDataSetChanged();
    }

    private void sortList() {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int sortOrder = prefs.getInt(SettingsActivity.KEY_SORT_ORDER, SettingsActivity.SORT_AZ);

        if (suggestionList != null && !suggestionList.isEmpty()) {
            Collections.sort(suggestionList, (s1, s2) -> {
                if (sortOrder == SettingsActivity.SORT_AZ) {
                    return s1.getName().compareToIgnoreCase(s2.getName());
                } else {
                    return s2.getName().compareToIgnoreCase(s1.getName());
                }
            });
        }
    }

    private void setupContextMenu() {
        listViewSuggestion.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewSuggestion.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int selectedCount = listViewSuggestion.getCheckedItemCount();
                mode.setTitle(selectedCount + " " + getString(R.string.item_escolhido));
                
                Menu menu = mode.getMenu();
                MenuItem editItem = menu.findItem(R.id.menuItemEditar);
                if (editItem != null) {
                    editItem.setVisible(selectedCount == 1);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu_list, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuItemEditar) {
                    editSelected(mode);
                    return true;
                } else if (id == R.id.menuItemExcluir) {
                    deleteSelected(mode);
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    private void editSelected(ActionMode mode) {
        SparseBooleanArray checked = listViewSuggestion.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                Suggestion suggestion = suggestionList.get(position);
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("suggestion", suggestion);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_EDIT);
                break;
            }
        }
        mode.finish();
    }

    private void deleteSelected(ActionMode mode) {
        SparseBooleanArray checked = listViewSuggestion.getCheckedItemPositions();
        for (int i = checked.size() - 1; i >= 0; i--) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                suggestionList.remove(position);
            }
        }
        saveSuggestionList();
        suggestionAdapter.notifyDataSetChanged();
        mode.finish();
    }

    private void loadSuggestionList() {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_SUGGESTION_LIST, null);
        
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Suggestion>>() {}.getType();
            suggestionList = gson.fromJson(json, type);
        } else {
            suggestionList = new ArrayList<>();
        }
        
        if (suggestionAdapter == null) {
            suggestionAdapter = new SuggestionAdapter(this, suggestionList);
            listViewSuggestion.setAdapter(suggestionAdapter);
        } else {
            // Se o adaptador já existe, apenas atualiza a referência da lista
            // Isso evita criar novos adaptadores no onResume desnecessariamente
            // mas garante que a lista atual seja a carregada do SharedPreferences
            try {
                java.lang.reflect.Field field = SuggestionAdapter.class.getDeclaredField("listaPessoas");
                field.setAccessible(true);
                field.set(suggestionAdapter, suggestionList);
            } catch (Exception e) {
                // Fallback se a reflexão falhar
                suggestionAdapter = new SuggestionAdapter(this, suggestionList);
                listViewSuggestion.setAdapter(suggestionAdapter);
            }
        }
    }

    private void saveSuggestionList() {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(suggestionList);
        editor.putString(KEY_SUGGESTION_LIST, json);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Suggestion suggestion = (Suggestion) data.getSerializableExtra("suggestion");
            if (suggestion != null) {
                if (requestCode == REQUEST_REGISTER) {
                    suggestionList.add(suggestion);
                } else if (requestCode == REQUEST_EDIT) {
                    int position = data.getIntExtra("position", -1);
                    if (position != -1) {
                        suggestionList.set(position, suggestion);
                    }
                }
                sortList();
                saveSuggestionList();
                suggestionAdapter.notifyDataSetChanged();
            }
        }
    }

    public void openAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_REGISTER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suggestion_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuItemAdicionar) {
            openRegister();
            return true;
        } else if (id == R.id.menuItemConfiguracoes) {
            openSettings();
            return true;
        } else if (id == R.id.menuItemSobre) {
            openAbout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
