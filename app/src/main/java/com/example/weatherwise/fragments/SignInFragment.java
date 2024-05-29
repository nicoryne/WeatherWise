package com.example.weatherwise.fragments;

import android.os.Bundle;
import android.util.Log;
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

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentSigninBinding;
import com.example.weatherwise.model.Health;
import com.example.weatherwise.model.HydrationSetting;
import com.example.weatherwise.model.User;
import com.example.weatherwise.viewmodels.HealthViewModel;
import com.example.weatherwise.viewmodels.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignInFragment extends Fragment {

    private final String DEBUG_TAG = "SignInFragment";

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
                   DocumentReference health = firestore.collection("health").document(Objects.requireNonNull(firebaseAuth.getUid()));
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

                   health.get().addOnSuccessListener(documentSnapshot -> {
                       if(task.isSuccessful()) {
                           Health healthSettings = documentSnapshot.toObject(Health.class);
                           HealthViewModel healthViewModel = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);
                           healthViewModel.setHealthMutableLiveData(new MutableLiveData<>(healthSettings));
                           updateHealthSettings(healthViewModel);
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

    private void updateHealthSettings(HealthViewModel healthViewModel) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference userRef = firestore.collection("health").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Health health = document.toObject(Health.class);

                    if (health == null || health.getDay() == null) {
                        Log.d(DEBUG_TAG, "Health or DateTime is null");
                    } else {
                        ZonedDateTime currentDate = ZonedDateTime.now(ZoneId.of("GMT+8"));
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                        LocalDate storedLocalDate = LocalDate.parse(health.getDay(), dateTimeFormatter);
                        ZonedDateTime storedDate = storedLocalDate.atStartOfDay(ZoneId.of("GMT+8"));

                        if (storedDate.toLocalDate().equals(currentDate.toLocalDate())) {
                            Log.d(DEBUG_TAG, "DateTime is up-to-date");
                            healthViewModel.setHealthMutableLiveData(new MutableLiveData<>(health));
                        } else {
                            Log.d(DEBUG_TAG, "DateTime is new");
                            health.setDay(currentDate.format(dateTimeFormatter));
                            health.setSteps(0);
                            health.setDistance(0);
                            health.setWaterConsumption(0);

                            Map<String, Object> healthMap = new HashMap<>();
                            healthMap.put("steps", health.getSteps());
                            healthMap.put("day", health.getDay());
                            healthMap.put("distance", health.getDistance());
                            healthMap.put("waterConsumption", health.getWaterConsumption());

                            userRef.set(healthMap)
                                    .addOnSuccessListener(aVoid -> Log.d(DEBUG_TAG, "Health data updated successfully"))
                                    .addOnFailureListener(e -> Log.e(DEBUG_TAG, "Error updating health data", e));
                        }
                    }
                } else {
                    Log.d(DEBUG_TAG, "No such document, creating new document");
                    // Create new document with default values
                    Health newHealth = new Health();
                    ZonedDateTime currentDate = ZonedDateTime.now(ZoneId.of("GMT+8"));
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

                    newHealth.setDay(currentDate.format(dateTimeFormatter));
                    newHealth.setSteps(0);
                    newHealth.setDistance(0);
                    newHealth.setWaterConsumption(0);

                    Map<String, Object> healthMap = new HashMap<>();
                    healthMap.put("steps", newHealth.getSteps());
                    healthMap.put("day", newHealth.getDay());
                    healthMap.put("distance", newHealth.getDistance());
                    healthMap.put("waterConsumption", newHealth.getWaterConsumption());

                    userRef.set(healthMap)
                            .addOnSuccessListener(aVoid -> Log.d(DEBUG_TAG, "Health document created successfully"))
                            .addOnFailureListener(e -> Log.e(DEBUG_TAG, "Error creating health document", e));

                    healthViewModel.setHealthMutableLiveData(new MutableLiveData<>(newHealth));
                }
            } else {
                Log.e(DEBUG_TAG, "Get failed with ", task.getException());
            }
        });
    }
}