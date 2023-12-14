package com.example.nombrepremier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText initialValue;
    private ArrayAdapter<Integer> primeNumbersAdapter;
    private TextView countPrimeNumber;

    private ListView primeNumbersListView;

    private Integer primeNumberFind = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        primeNumbersListView = findViewById(R.id.home_page_list_text_view);
        Button startCalculationButton = findViewById(R.id.home_page_btn_calc);
        countPrimeNumber = findViewById(R.id.home_page_text_view_count_result);
        initialValue = findViewById(R.id.home_page_edit_text);

        // Créer un adaptateur pour les nombres premiers
        primeNumbersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        primeNumbersListView.setAdapter(primeNumbersAdapter);

        Button btnchangeView = findViewById(R.id.home_page_btn_change_view);
        btnchangeView.setOnClickListener(v->{

            Intent child = new Intent(MainActivity.this, ChildActivity.class);

            startActivity(child);

        });

        startCalculationButton.setOnClickListener(v -> {
            // Exécutez la tâche AsyncTask
            primeNumberFind = 0;
            countPrimeNumber.setText(primeNumberFind.toString());
            new CalculatePrimesTask().execute(Long.parseLong(initialValue.getText().toString())); // Remplacez 100 par la valeur souhaitée
        });
    }


    private class CalculatePrimesTask extends AsyncTask<Long, Long, List<Long>> {
        private  int test=0;
        @Override
        protected void onPreExecute() {
            // Vous pouvez effectuer des initialisations ici avant le début de la tâche
            primeNumbersAdapter.clear();
        }

        @Override
        protected List<Long> doInBackground(Long... params) {
            long maxNumber = params[0];
            boolean[] isPrime = new boolean[(int) (maxNumber + 1)];
            Arrays.fill(isPrime, true);

            for (long number = 2; number <= maxNumber; number++) {
                if (isPrime[(int) number]) {
                    for (long multiple = number * number; multiple <= maxNumber; multiple += number) {
                        isPrime[(int) multiple] = false;
                    }
                    publishProgress(number); // Mettre à jour l'interface utilisateur pendant l'exécution
                }
            }

            List<Long> primeNumbers = new ArrayList<>();
            for (long i = 2; i <= maxNumber; i++) {
                if (isPrime[(int) i]) {
                    primeNumbers.add(i);
                    primeNumberFind = primeNumberFind + 1;
                }
            }

            return primeNumbers;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            // Mettez à jour l'interface utilisateur avec le nouveau nombre premier
            primeNumbersAdapter.add(Integer.parseInt(values[0].toString()));
            primeNumbersListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        }

        @Override
        protected void onPostExecute(List<Long> result) {
            Toast.makeText(MainActivity.this, "HOALLA", Toast.LENGTH_LONG).show();
            countPrimeNumber.setText(primeNumberFind.toString());
        }


    }
}