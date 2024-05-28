package com.example.weatherwise.model;

public class Health {

    private int steps;

    private int dailyGoal;

    private double distance;

    private int streak;

    private double waterIntake;

    private double waterConsumption;

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(int dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(double waterIntake) {
        this.waterIntake = waterIntake;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public double getWaterConsumption() {
        return waterConsumption;
    }

    public void setWaterConsumption(double waterConsumption) {
        this.waterConsumption = waterConsumption;
    }
}
