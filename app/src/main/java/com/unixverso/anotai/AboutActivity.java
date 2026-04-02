package com.unixverso.anotai;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getString(R.string.sobre_anotai));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void openLinkedin(View view) {
        openLink("https://www.linkedin.com/in/eng-elson/");
    }

    public void openWebSite(View view) {
        openLink("https://pos-graduacao-ead.cp.utfpr.edu.br/");
    }

    private void openLink(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void sendEmail(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"suporte@anotai.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Dúvida sobre o app Anotaí");

        String mensagem = "Olá,\n\n" +
                "Estou com a seguinte dúvida:\n\n" +
                "[Descreva aqui sua dúvida]\n\n" +
                "Detalhes adicionais:\n" +
                "- Versão do app:\n" +
                "- Dispositivo:\n\n" +
                "Obrigado!";

        intent.putExtra(Intent.EXTRA_TEXT, mensagem);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
