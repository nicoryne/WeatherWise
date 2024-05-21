package com.example.weatherwise.util;

import androidx.navigation.NavController;

public class MainController {

    private static MainController instance;

    private NavController openingController;
    private MainController() {};

    public static MainController getInstance() {
        if(instance == null) {
            instance = new MainController();
        }

        return instance;
    }




}
