package com.example.weatherwise.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherwise.model.GameScore;

public class GameViewModel extends ViewModel {

    private MutableLiveData<GameScore> gameScoreMutableLiveData;

    public void setGameScoreMutableLiveData(MutableLiveData<GameScore> gameScoreMutableLiveData) {
        this.gameScoreMutableLiveData = gameScoreMutableLiveData;
    }

    public LiveData<GameScore> getGameScoreLiveData() {
        return gameScoreMutableLiveData;
    }
}
