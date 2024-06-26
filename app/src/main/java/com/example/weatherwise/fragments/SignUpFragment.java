package com.example.weatherwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.weatherwise.R;
import com.example.weatherwise.api.DataCallback;
import com.example.weatherwise.databinding.FragmentSignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(root).popBackStack());
    }

    private void setSignInClickable() {
        binding.tvSignIn.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_signUpFragment_to_signInFragment));
    }

    private void setSignUpButton() {
        binding.btnRegister.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);

            if (!verifyInputs()) {
                binding.progressBar.setVisibility(View.GONE);
                return;
            }

            String email = binding.inptEmail.getText().toString().trim();
            String password = binding.inptPassword.getText().toString().trim();

            registerUser(email, password);
        });
    }

    private void addNewUserToCollection(String uuid, String email) {
        String address = binding.inptAddress.getText().toString().trim();
        String name = binding.inptName.getText().toString().trim();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference =  firestore.collection("users").document(uuid);
        ZonedDateTime currentDate = ZonedDateTime.now(ZoneId.of("GMT+8"));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", name);
        newUser.put("email", email);
        newUser.put("address", address);
        newUser.put("gender", null);
        newUser.put("birthdate", null);
        newUser.put("createdAt", currentDate.format(dateTimeFormatter));

        documentReference.set(newUser).addOnSuccessListener(v -> showToast("Registration successful!"));
    }

    private void registerUser(String email, String password) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean userExists = !task.getResult().isEmpty();

                if(userExists) {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("User already exists!");
                    return;
                }

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createTask -> {
                    if (createTask.isCanceled()) {
                        binding.progressBar.setVisibility(View.GONE);
                        showToast("Registration cancelled!");
                        return;
                    }

                    if (!createTask.isSuccessful()) {
                        binding.progressBar.setVisibility(View.GONE);
                        showToast("Registration failed!");
                        return;
                    }

                    String uuid = firebaseAuth.getUid();

                    addNewUserToCollection(uuid, email);
                    binding.progressBar.setVisibility(View.GONE);

                    Navigation.findNavController(root).navigate(R.id.action_signUpFragment_to_signInFragment);
                });
            }
        });
    }

    private boolean verifyInputs() {
        if (isFieldEmpty(binding.inptName, "You failed to input a name!")) return false;
        if (isFieldEmpty(binding.inptEmail, "You failed to input an email!")) return false;
        if (isFieldEmpty(binding.inptPassword, "You failed to input a password!")) return false;
        if (isFieldEmpty(binding.inptVerifyPassword, "You failed to verify your password!")) return false;
        if (isFieldEmpty(binding.inptAddress, "You failed to input an address!")) return false;
        
        if (binding.inptPassword.getText().toString().length() < 6) {
            showToast("Password should be at least 6 characters!");
            return false;
        }

        if (!binding.inptVerifyPassword.getText().toString().equals(binding.inptPassword.getText().toString())) {
            showToast("Your password verification does not match!");
            return false;
        }

        if (!binding.cbTerms.isChecked()) {
            showToast("You forgot to accept the Terms and Conditions!");
            return false;
        }

        return true;
    }
    

    private boolean isFieldEmpty(EditText field, String errorMessage) {
        if (field.getText().toString().isEmpty()) {
            showToast(errorMessage);
            return true;
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}