package com.example.weatherwise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentSigninBinding;
import com.example.weatherwise.databinding.FragmentTemplateBinding;
import com.example.weatherwise.model.Health;
import com.example.weatherwise.model.HydrationSetting;
import com.example.weatherwise.model.User;
import com.example.weatherwise.viewmodels.HealthViewModel;
import com.example.weatherwise.viewmodels.ProfileViewModel;
import com.example.weatherwise.worker.HydrationReminderWorker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(root).popBackStack());
    }

    private void setSignUpClickable() {
        binding.tvSignUp.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_signInFragment_to_signUpFragment));
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
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
               if(task.isSuccessful()) {
                   binding.progressBar.setVisibility(View.GONE);
                   showToast("Login successful!");

                   //  Handle data
                   FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                   DocumentReference userRef =  firestore.collection("users").document(Objects.requireNonNull(firebaseAuth.getUid()));
                   DocumentReference hydrationSetting =  firestore.collection("hydration_setting").document(Objects.requireNonNull(firebaseAuth.getUid()));
                   userRef.get().addOnSuccessListener(documentSnapshot -> {
                       if(task.isSuccessful()) {
                           User user = documentSnapshot.toObject(User.class);
                           ProfileViewModel profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
                           profileViewModel.setUserMutableLiveData(new MutableLiveData<>(user));
                       }

                       if(task.isCanceled() || !(task.isSuccessful())) {
                           showToast("Error fetching user credentials!");
                       }
                   });

                   hydrationSetting.get().addOnSuccessListener(documentSnapshot -> {
                       if(task.isSuccessful()) {
                           HydrationSetting userHydrationSetting = documentSnapshot.toObject(HydrationSetting.class);
                           HealthViewModel healthViewModel = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);
                           healthViewModel.setHydrationSettingMutableLiveData(new MutableLiveData<>(userHydrationSetting));
                       }

                       if(task.isCanceled() || !(task.isSuccessful())) {
                           showToast("Error fetching user hydration settings");
                       }
                   });

                   Navigation.findNavController(root).navigate(R.id.action_signInFragment_to_homeFragment);
               } else if (!task.isSuccessful()) {
                   binding.progressBar.setVisibility(View.GONE);
                   showToast("Incorrect login details!");
               }

                if(task.isCanceled()) {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Login failed!");
                }
            });
        });
    }

    private boolean verifyInputs() {
        if (isFieldEmpty(binding.inptEmail, "You failed to input a name!")) return false;
        if (isFieldEmpty(binding.inptPassword, "You failed to input an email!")) return false;

        if (binding.inptPassword.getText().toString().length() < 6) {
            showToast("Password should be at least 6 characters!");
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