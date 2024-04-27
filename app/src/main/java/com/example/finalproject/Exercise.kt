package com.example.finalproject

class Exercise(
    private var name: String,
    private var numOfSets: String,
    private var numOfReps: String,
    private var weight: String
) {
    // Getter and setter methods for each property
    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getNumOfSets(): String {
        return numOfSets
    }

    fun setNumOfSets(numOfSets: String) {
        this.numOfSets = numOfSets
    }

    fun getNumOfReps(): String {
        return numOfReps
    }

    fun setNumOfReps(numOfReps: String) {
        this.numOfReps = numOfReps
    }

    fun getWeight(): String {
        return weight
    }

    fun setWeight(weight: String) {
        this.weight = weight
    }
}
