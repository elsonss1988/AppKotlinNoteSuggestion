package com.unixverso.anotai;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listViewSuggestion;
    private List<Suggestion> suggestionList;
    private SuggestionAdapter suggestionAdapter;
    private static final int REQUEST_REGISTER = 1;
    private static final int REQUEST_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle(getString(R.string.title_list_suggestion));

        listViewSuggestion = findViewById(R.id.listViewSuggestion);
        
        initializeSuggestionList();
        setupContextMenu();

        listViewSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Suggestion selectedSuggestion = suggestionList.get(i);
                String message = "Sua sugestão clicada é \""
                        + selectedSuggestion.getCategory() + ": "
                        + selectedSuggestion.getName() + "\"\n";

                Toast.makeText(ListActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupContextMenu() {
        // Altera para CHOICE_MODE_MULTIPLE_MODAL para ativar o MultiChoiceModeListener
        listViewSuggestion.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewSuggestion.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int selectedCount = listViewSuggestion.getCheckedItemCount();
                mode.setTitle(selectedCount + " " + getString(R.string.title_list_suggestion));
                
                // Opcional: Desabilitar editar se mais de um item estiver selecionado
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
                break; // Edita apenas o primeiro selecionado
            }
        }
        mode.finish();
    }

    private void deleteSelected(ActionMode mode) {
        SparseBooleanArray checked = listViewSuggestion.getCheckedItemPositions();
        // Remove de trás para frente para não alterar os índices durante a remoção
        for (int i = checked.size() - 1; i >= 0; i--) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                suggestionList.remove(position);
            }
        }
        suggestionAdapter.notifyDataSetChanged();
        mode.finish();
    }

    private void initializeSuggestionList() {
        suggestionList = new ArrayList<>();
        suggestionAdapter = new SuggestionAdapter(this, suggestionList);
        listViewSuggestion.setAdapter(suggestionAdapter);
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
                suggestionAdapter.notifyDataSetChanged();
            }
        }
    }

    public void openAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
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
        } else if (id == R.id.menuItemSobre) {
            openAbout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}