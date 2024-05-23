package com.example.weatherwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentSigninBinding;
import com.example.weatherwise.databinding.FragmentTemplateBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {

    private final String DEBUG_TAG = "TemplateFragment";

    private FragmentSigninBinding binding;

    private View root;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSigninBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        setBackButton();
        setSignUpClickable();
        setLoginButton();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setBackButton() {
        binding.btnBack.setOnClickListener(v -> {
            Navigation.findNavController(root).popBackStack();
        });
    }

    private void setSignUpClickable() {
        binding.tvSignUp.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_signInFragment_to_signUpFragment);
        });
    }

    private void setLoginButton() {
        binding.btnLogin.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            if (!verifyInputs()) {
                binding.progressBar.setVisibility(View.GONE);
                return;
            }

            String email = binding.inptEmail.getText().toString();
            String password = binding.inptPassword.getText().toString();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if(task.isCanceled()) {
                            Toast.makeText(getContext(), "Login cancelled!", Toast.LENGTH_SHORT).show();
                        }

                        if(!task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                        }

                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(root).navigate(R.id.action_signInFragment_to_homeFragment);
                    });

        });
    }

    private boolean verifyInputs() {
        if(binding.inptEmail.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "You failed to input an email!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(binding.inptPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "You failed to input a password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.inptPassword.getText().toString().length() < 6) {
            Toast.makeText(getContext(), "Password should be at least 6 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}