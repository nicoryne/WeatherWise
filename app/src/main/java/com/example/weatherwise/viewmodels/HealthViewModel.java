package com.example.weatherwise.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherwise.model.Health;
import com.example.weatherwise.model.HydrationSetting;

public class HealthViewModel extends ViewModel {

    private MutableLiveData<Health> healthMutableLiveData;

    private MutableLiveData<HydrationSetting> hydrationSettingMutableLiveData;

    public LiveData<Health> getHealthMutableLiveData() {
        return healthMutableLiveData;
    }

    public void setHealthMutableLiveData(MutableLiveData<Health> healthMutableLiveData) {
        this.healthMutableLiveData = healthMutableLiveData;
    }

    public LiveData<HydrationSetting> getHydrationSettingLiveData() {
        return hydrationSettingMutableLiveData;
    }

    public void setHydrationSettingMutableLiveData(MutableLiveData<HydrationSetting> hydrationSettingMutableLiveData) {
        this.hydrationSettingMutableLiveData = hydrationSettingMutableLiveData;
    }
}
