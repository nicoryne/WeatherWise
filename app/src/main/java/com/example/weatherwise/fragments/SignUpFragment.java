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
import com.example.weatherwise.databinding.FragmentSignupBinding;
import com.example.weatherwise.databinding.FragmentTemplateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpFragment extends Fragment {

    private final String DEBUG_TAG = "TemplateFragment";

    private FragmentSignupBinding binding;

    private View root;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        setBackButton();
        setSignUpButton();
        setSignInClickable();
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

    private void setSignInClickable() {
        binding.tvSignIn.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_signUpFragment_to_signInFragment);
        });
    }

    private void setSignUpButton() {
        binding.btnRegister.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            if (!verifyInputs()) {
                binding.progressBar.setVisibility(View.GONE);
                return;
            }

            String email = binding.inptEmail.getText().toString();
            String password = binding.inptPassword.getText().toString();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if(task.isCanceled()) {
                            Toast.makeText(getContext(), "Registration cancelled!", Toast.LENGTH_SHORT).show();
                        }

                        if(!task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registration failed!", Toast.LENGTH_SHORT).show();
                        }

                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(root).navigate(R.id.action_signUpFragment_to_signInFragment);
                    });

        });
    }

    private boolean verifyInputs() {
        if(binding.inptName.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "You failed to input a name!", Toast.LENGTH_SHORT).show();
            return false;
        }

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

        if(binding.inptVerifyPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "You failed to verify your password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(binding.inptVerifyPassword.getText().toString().equals(binding.inptPassword.getText().toString()))) {
            Toast.makeText(getContext(), "Your password verification does not match!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(binding.inptAddress.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "You failed to input an address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!binding.cbTerms.isChecked()) {
            Toast.makeText(getContext(), "You forgot to accept the Terms and Conditions!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}