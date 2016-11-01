package ru.pro2410.urlview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Button btnParseHtml;
    private EditText edtEnterUrl;
    private TextView textHtml;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private String url_regex;
    private ProgressBar pbCircleLoading;
    private static final int TIMEOUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url_regex = getString(R.string.URLRegex);
        btnParseHtml = (Button) findViewById(R.id.parseButton);
        edtEnterUrl = (EditText) findViewById(R.id.editText);
        linearLayout = (LinearLayout) findViewById(R.id.idLinear);
        scrollView = (ScrollView) findViewById(R.id.idScroll);
        textHtml = (TextView) findViewById(R.id.idText);
        pbCircleLoading = (ProgressBar) findViewById(R.id.pbCircleLoading);

        btnParseHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = edtEnterUrl.getText().toString();
                if (!isValidUrl(str)){

                    String message = "Введите url. Например https://yandex.ru/";
                    Toast.makeText(MainActivity.this, message,Toast.LENGTH_SHORT).show();
                    return;
                }
                linearLayout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                new LoadHtmlTask().execute(str);
            }
        });
    }
    private boolean isValidUrl(String str){
        Pattern p = Pattern.compile(url_regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    private class LoadHtmlTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            pbCircleLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder html = null;
            try {
                URLConnection connection = (new URL(params[0])).openConnection();
                connection.setConnectTimeout(TIMEOUT);
                connection.setReadTimeout(TIMEOUT);
                connection.connect();

                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                html = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    html.append(line);
                }
                in.close();
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (html==null){
                return "URL не существует";
            }
            return html.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            pbCircleLoading.setVisibility(View.GONE);
            textHtml.setText(s);
        }
    }
}
