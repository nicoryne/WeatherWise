package com.example.weatherwise.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherwise.model.CurrentWeatherData;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<CurrentWeatherData> currentWeatherDataMutableLiveData;

    public LiveData<CurrentWeatherData> getCurrentWeatherLiveData() {
        return currentWeatherDataMutableLiveData;
    }

    public void setCurrentWeatherDataMutableLiveData(MutableLiveData<CurrentWeatherData> currentWeatherDataMutableLiveData) {
        this.currentWeatherDataMutableLiveData = currentWeatherDataMutableLiveData;
    }
}
