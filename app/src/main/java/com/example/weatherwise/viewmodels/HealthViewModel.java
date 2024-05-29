package com.example.weatherwise.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherwise.model.Health;
import com.example.weatherwise.model.HydrationSetting;

import java.util.Objects;

public class HealthViewModel extends ViewModel {

    private MutableLiveData<Health> healthMutableLiveData;
    private MutableLiveData<HydrationSetting> hydrationSettingMutableLiveData;

    public LiveData<HydrationSetting> getHydrationSettingLiveData() {
        return hydrationSettingMutableLiveData;
    }

    public void setHydrationSettingMutableLiveData(MutableLiveData<HydrationSetting> hydrationSettingMutableLiveData) {
        this.hydrationSettingMutableLiveData = hydrationSettingMutableLiveData;
    }

    public LiveData<Health> getHealthLiveData() {
        return healthMutableLiveData;
    }

    public void setHealthMutableLiveData(MutableLiveData<Health> healthMutableLiveData) {
        this.healthMutableLiveData = healthMutableLiveData;
    }

    public void updateWaterConsumption(double waterConsumption) {
        Health currentHealth = healthMutableLiveData.getValue();
        if (currentHealth != null) {
            currentHealth.setWaterConsumption(waterConsumption);
            healthMutableLiveData.setValue(currentHealth);
        }
    }
}
