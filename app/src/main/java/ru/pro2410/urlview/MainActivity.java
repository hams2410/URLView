package ru.pro2410.urlview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    Button parseButton;
    EditText editText;
    TextView textHtml;
    LinearLayout linearLayout;
    ScrollView scrollView;
    String URL_REGEX = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL_REGEX = getString(R.string.URLRegex);
        parseButton = (Button) findViewById(R.id.parseButton);
        editText = (EditText) findViewById(R.id.editText);

        linearLayout = (LinearLayout) findViewById(R.id.idLinear);
        scrollView = (ScrollView) findViewById(R.id.idScroll);
        textHtml = (TextView) findViewById(R.id.idText);

        parseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString();
                if (!checkUrl(str)){

                    String message = "Введите url. Например https://yandex.ru/";
                    Toast.makeText(MainActivity.this, message,Toast.LENGTH_SHORT).show();
                    return;
                }
                linearLayout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                new GetHtmlTask().execute(str);

            }
        });
    }
    boolean checkUrl(String str){
        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    class GetHtmlTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            StringBuilder html = null;
            try {
                URLConnection connection = (new URL(params[0])).openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
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
           textHtml.setText(s);
        }
    }
}
