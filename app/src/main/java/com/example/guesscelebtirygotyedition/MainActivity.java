package com.example.guesscelebtirygotyedition;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private TextView textViewScore;
    private ImageView celebrityPhoto;
    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;

    private final String url = "https://www.imdb.com/list/ls052283250/";

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> photos = new ArrayList<>();
    private ArrayList<Button> buttons = new ArrayList<>();

    private int score = 0;
    private int answerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewScore = findViewById(R.id.textViewScore);
        celebrityPhoto = findViewById(R.id.celebrityPhoto);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);

        textViewScore.setText(String.format(getString(R.string.score_text), score));
        GetHTML getHTML = new GetHTML();
        try {
            String HTML = getHTML.execute(url).get();

            Pattern namesPattern = Pattern.compile("<img alt=\"(.*?)\"\n" + "height=\"209\"");
            Matcher namesMatcher = namesPattern.matcher(HTML);
            while (namesMatcher.find()) {
                names.add(namesMatcher.group(1));
            }

            Pattern photosPattern = Pattern.compile("src=\"(.*?)\"\n" + "width=\"140\" />");
            Matcher photosMatcher = photosPattern.matcher(HTML);
            while (photosMatcher.find()) {
                photos.add(photosMatcher.group(1));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GenerateQuestion();
    }

    public void GenerateQuestion() {
        int random = (int) (Math.random() * 99 + 1);
        answerButton = (int) (Math.random() * 4);

        SetPhoto setPhoto = new SetPhoto();
        try {
            celebrityPhoto.setImageBitmap(setPhoto.execute(photos.get(random)).get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 4; i++) {
            if (i == answerButton) {
                buttons.get(i).setText(names.get(random));
            } else {
                buttons.get(i).setText(names.get((int) (Math.random() * 99 + 1)));
            }
        }
    }

    public void chooseAnswer(View view) {
        if (view.getTag().equals(String.valueOf(answerButton))) {
            Toast.makeText(getApplicationContext(), R.string.right_answer, Toast.LENGTH_SHORT).show();
            score++;
            textViewScore.setText(String.format(getString(R.string.score_text), score));
        } else {
            Toast.makeText(getApplicationContext(), R.string.wrong_answer, Toast.LENGTH_SHORT).show();
        }

        GenerateQuestion();
    }

    private static class GetHTML extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder html = new StringBuilder();
            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line = bufferedReader.readLine();
                while (line != null) {
                    html.append(line).append("\n");
                    line = bufferedReader.readLine();
                }

                return html.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }

    private static class SetPhoto extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();

                return BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }
}