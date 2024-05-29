package com.example.weatherwise.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentGameBinding;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class GameFragment extends Fragment {

    private FragmentGameBinding binding;
    private View root;

    private List<Question> questions;

    private int currentQuestionIndex = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        setup();
        loadQuestion(currentQuestionIndex);
        return root;
    }


    private void loadQuizQuestions() {
        try {
            String json = loadJSONFromAsset(requireContext());
            Gson gson = new Gson();
            Quiz quiz = gson.fromJson(json, Quiz.class);
            questions = quiz.getQuestions();
            Collections.shuffle(questions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setup() {
        loadQuizQuestions();

        binding.btnFirst.setOnClickListener(v -> checkAnswer((boolean) binding.btnFirst.getTag(), binding.btnFirst));
        binding.btnSecond.setOnClickListener(v -> checkAnswer((boolean) binding.btnSecond.getTag(), binding.btnSecond));
        binding.btnThird.setOnClickListener(v -> checkAnswer((boolean) binding.btnThird.getTag(), binding.btnThird));
        binding.btnFourth.setOnClickListener(v -> checkAnswer((boolean) binding.btnFourth.getTag(), binding.btnFourth));
    }

    private void checkAnswer(boolean isCorrect, Button button) {
        if (isCorrect) {
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.google.android.libraries.places.R.color.quantum_googgreen)));
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.google.android.libraries.places.R.color.quantum_googred)));
            highlightCorrectAnswer();
        }

        new Handler().postDelayed(() -> {
            resetButtonBackgrounds();

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                loadQuestion(currentQuestionIndex);
            } else {
                // Quiz is finished, handle accordingly
            }
        }, 1000);
    }

    private void highlightCorrectAnswer() {
        for (int i = 0; i < 4; i++) {
            boolean isCorrect = (boolean) binding.btnFirst.getTag();
            if (isCorrect) {
                switch (i) {
                    case 0:
                        binding.btnFirst.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.google.android.libraries.places.R.color.quantum_googgreen)));
                        break;
                    case 1:
                        binding.btnSecond.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.google.android.libraries.places.R.color.quantum_googgreen)));
                        break;
                    case 2:
                        binding.btnThird.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.google.android.libraries.places.R.color.quantum_googgreen)));
                        break;
                    case 3:
                        binding.btnFourth.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.google.android.libraries.places.R.color.quantum_googgreen)));
                        break;
                }
                break;
            }
        }
    }

    private void resetButtonBackgrounds() {
        binding.btnFirst.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.salmon)));
        binding.btnSecond.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.salmon)));
        binding.btnThird.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.salmon)));
        binding.btnFourth.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.salmon)));
    }

    private void loadQuestion(int index) {
        Question question = questions.get(index);
        binding.tvQuestion.setText(question.getQuestion());
        List<Option> options = question.getOptions();
        Collections.shuffle(options);

        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            switch (i) {
                case 0:
                    binding.btnFirst.setText(option.getOption());
                    binding.btnFirst.setTag(option.isCorrect());
                    break;
                case 1:
                    binding.btnSecond.setText(option.getOption());
                    binding.btnSecond.setTag(option.isCorrect());
                    break;
                case 2:
                    binding.btnThird.setText(option.getOption());
                    binding.btnThird.setTag(option.isCorrect());
                    break;
                case 3:
                    binding.btnFourth.setText(option.getOption());
                    binding.btnFourth.setTag(option.isCorrect());
                    break;
            }
        }
    }

    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("quiz_game.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int bytesRead = is.read(buffer);
            is.close();
            if (bytesRead != size) {
                throw new IOException("Failed to read the entire file");
            }
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LoadJSON", "Error loading JSON file: " + e.getMessage());
        }
        return json;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class Question {
        @SerializedName("question")
        private String question;
        @SerializedName("options")
        private List<Option> options;

        public String getQuestion() {
            return question;
        }

        public List<Option> getOptions() {
            return options;
        }
    }

    class Option {
        @SerializedName("option")
        private String option;
        @SerializedName("correct")
        private boolean correct;

        public String getOption() {
            return option;
        }

        public boolean isCorrect() {
            return correct;
        }
    }

    class Quiz {
        @SerializedName("questions")
        private List<Question> questions;

        public List<Question> getQuestions() {
            return questions;
        }
    }
}