package com.example.weatherwise.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentProfileBinding;
import com.example.weatherwise.databinding.FragmentTemplateBinding;
import com.example.weatherwise.model.User;
import com.example.weatherwise.viewmodels.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;

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
        return root;
    }

    @SuppressLint("SetTextI18n")
    private void setUserDetails(User user) {
        binding.tvName.setText(user.getName().split(" ")[0]);
        binding.tvFullName.setText(user.getName());
        binding.tvEmail.setText(user.getEmail());
        binding.tvLocation.setText(user.getAddress());
        binding.tvGender.setText(user.getGender());
        binding.tvBirthdate.setText(user.getBirthdate());
        binding.tvJoinDate.setText("Joined on " + user.getCreatedAt());
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
}