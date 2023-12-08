package com.example.nombrepremier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChildActivity extends AppCompatActivity {
    private EditText initialValue;
    private ArrayAdapter<Integer> primeNumbersAdapter;
    private TextView countPrimeNumber;

    private Integer primeNumberFind = 0;

    private ExecutorService executorService = Executors.newSingleThreadExecutor(); // You can adjust the number of threads as needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        ListView primeNumbersListView = findViewById(R.id.home_page_list_text_view);
        Button startCalculationButton = findViewById(R.id.home_page_btn_calc);
        countPrimeNumber = findViewById(R.id.home_page_text_view_count_result);
        initialValue = findViewById(R.id.home_page_edit_text);

        // Create an adapter for prime numbers
        primeNumbersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        primeNumbersListView.setAdapter(primeNumbersAdapter);

        Button changeView = findViewById(R.id.child_page_change_view);

        changeView.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
        startCalculationButton.setOnClickListener(v -> {

            // Execute the task using ExecutorService
            primeNumberFind = 0;
            countPrimeNumber.setText(primeNumberFind.toString());

            Future<List<Long>> future = executorService.submit(new CalculatePrimesTask(Long.parseLong(initialValue.getText().toString())));
            try {
                List<Long> result = future.get();
                updateUI(result);
            } catch (Exception e) {
                e.printStackTrace();

            }
        });
    }

    private class CalculatePrimesTask implements Callable<List<Long>> {
        private long maxNumber;

        CalculatePrimesTask(long maxNumber) {
            this.maxNumber = maxNumber;
        }

        @Override
        public List<Long> call() throws Exception {
            boolean[] isPrime = new boolean[(int) (maxNumber + 1)];
            Arrays.fill(isPrime, true);

            for (long number = 2; number <= maxNumber; number++) {
                if (isPrime[(int) number]) {
                    for (long multiple = number * number; multiple <= maxNumber; multiple += number) {
                        isPrime[(int) multiple] = false;
                    }
                    updateProgress(number);
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
    }

    private void updateUI(List<Long> result) {
        Toast.makeText(ChildActivity.this, "HOALLA2", Toast.LENGTH_LONG).show();
        countPrimeNumber.setText(primeNumberFind.toString());
        primeNumbersAdapter.clear();

    }

    private void updateProgress(long value) {
        runOnUiThread(() -> primeNumbersAdapter.add((int) value));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}