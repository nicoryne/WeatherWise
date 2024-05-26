package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
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

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentProfileBinding;
import com.example.weatherwise.model.User;
import com.example.weatherwise.viewmodels.ProfileViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private final String DEBUG_TAG = "ProfileFragment";

    private FragmentProfileBinding binding;

    private View root;

    private ProfileViewModel model;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        root = binding.getRoot();

        model = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        model.getUserLiveData().observe(getViewLifecycleOwner(), this::setUserDetails);
        setSignOutButton();
        setEditButton();
        setCancelButton();
        setSaveButton();
        return root;
    }

    @SuppressLint("SetTextI18n")
    private void setUserDetails(User user) {
        binding.tvName.setText(user.getName().split(" ")[0]);
        binding.tvJoinDate.setText("Joined on " + user.getCreatedAt());

        setUserInputFields(user);
    }

    private void setUserInputFields(User user) {
        binding.tvFullName.setText(user.getName());
        binding.tvEmail.setText(user.getEmail());
        binding.tvLocation.setText(user.getAddress());
        binding.tvGender.setText(user.getGender());
        binding.tvBirthdate.setText(user.getBirthdate());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setSignOutButton() {
        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signOut();
            model.setUserMutableLiveData(null);
            Navigation.findNavController(root).navigate(R.id.boardingFragment);
        });
    }

    private void setEditButton() {
        binding.btnEdit.setOnClickListener(v -> openEditableProfileView());
    }

    private void activateTextField(EditText editText) {
        editText.setClickable(true);
        editText.setFocusable(true);
        editText.setCursorVisible(true);
        editText.setFocusableInTouchMode(true);
        editText.setBackgroundResource(R.drawable.rounded_rectangle_text_input_edit);
    }

    private void deactivateTextField(EditText editText) {
        editText.setClickable(false);
        editText.setFocusable(false);
        editText.setCursorVisible(false);
        editText.setFocusableInTouchMode(false);
        editText.setBackgroundResource(R.drawable.rounded_rectangle_text_input_noedit);
    }

    private void setCancelButton() {
        binding.btnCancel.setOnClickListener(v -> closeEditableProfileView());
    }

    private void setSaveButton() {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.tvFullName.getText().toString();
            String address = binding.tvLocation.getText().toString();
            String gender = binding.tvGender.getText().toString();
            String birthdate = binding.tvBirthdate.getText().toString();


            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            DocumentReference userRef = firestore.collection("users").document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
            Map<String, Object> editedUser = new HashMap<>();
            editedUser.put("name", name);
            editedUser.put("address", address);
            editedUser.put("gender", gender);
            editedUser.put("birthdate", birthdate);
            userRef.update(editedUser).addOnSuccessListener(unused -> showToast("Information successfully updated!"));

            DocumentReference documentReference = firestore.collection("users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                ProfileViewModel profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
                profileViewModel.setUserMutableLiveData(new MutableLiveData<>(user));
                closeEditableProfileView();
            });
        });
    }

    private void closeEditableProfileView() {
        binding.btnEdit.setVisibility(View.VISIBLE);
        binding.btnLogout.setVisibility(View.VISIBLE);
        binding.btnCancel.setVisibility(View.GONE);
        binding.btnSave.setVisibility(ViewGroup.GONE);

        deactivateTextField(binding.tvFullName);
        deactivateTextField(binding.tvLocation);
        deactivateTextField(binding.tvBirthdate);
        deactivateTextField(binding.tvGender);

        model.getUserLiveData().observe(getViewLifecycleOwner(), this::setUserDetails);
    }

    private void openEditableProfileView() {
        binding.btnEdit.setVisibility(View.GONE);
        binding.btnLogout.setVisibility(View.GONE);
        binding.btnCancel.setVisibility(View.VISIBLE);
        binding.btnSave.setVisibility(View.VISIBLE);

        activateTextField(binding.tvFullName);
        activateTextField(binding.tvLocation);
        activateTextField(binding.tvBirthdate);
        activateTextField(binding.tvGender);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}