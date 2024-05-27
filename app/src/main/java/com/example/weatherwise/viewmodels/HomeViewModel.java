package com.example.weatherwise.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherwise.model.CurrentWeatherData;
import com.example.weatherwise.model.Fitness;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<CurrentWeatherData> currentWeatherDataMutableLiveData;

    private MutableLiveData<Fitness> currentHealthMutableLiveData;

    public LiveData<CurrentWeatherData> getCurrentWeatherLiveData() {
        return currentWeatherDataMutableLiveData;
    }

    public void setCurrentWeatherDataMutableLiveData(MutableLiveData<CurrentWeatherData> currentWeatherDataMutableLiveData) {
        this.currentWeatherDataMutableLiveData = currentWeatherDataMutableLiveData;
    }

    public LiveData<Fitness> getFitnessLiveData() {
        return currentHealthMutableLiveData;
    }

    public void setCurrentHealthMutableLiveData(MutableLiveData<Fitness> currentHealthMutableLiveData) {
        this.currentHealthMutableLiveData = currentHealthMutableLiveData;
    }
}
