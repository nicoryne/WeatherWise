package com.example.weatherwise.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherwise.model.Health;
import com.example.weatherwise.model.HydrationSetting;

public class HealthViewModel extends ViewModel {

    private MutableLiveData<Integer> stepsMutableLiveData;

    private MutableLiveData<Health> healthMutableLiveData;
    private MutableLiveData<HydrationSetting> hydrationSettingMutableLiveData;

    public LiveData<HydrationSetting> getHydrationSettingLiveData() {
        return hydrationSettingMutableLiveData;
    }

    public void setHydrationSettingMutableLiveData(MutableLiveData<HydrationSetting> hydrationSettingMutableLiveData) {
        this.hydrationSettingMutableLiveData = hydrationSettingMutableLiveData;
    }

    public LiveData<Integer> getStepsLiveData() {
        return stepsMutableLiveData;
    }

    public void setStepsMutableLiveData(MutableLiveData<Integer> stepsMutableLiveData) {
        this.stepsMutableLiveData = stepsMutableLiveData;
    }

    public LiveData<Health> getHealthLiveData() {
        return healthMutableLiveData;
    }

    public void setHealthMutableLiveData(MutableLiveData<Health> healthMutableLiveData) {
        this.healthMutableLiveData = healthMutableLiveData;
    }
}
