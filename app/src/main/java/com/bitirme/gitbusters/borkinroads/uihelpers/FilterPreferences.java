package com.bitirme.gitbusters.borkinroads.uihelpers;

import android.support.annotation.NonNull;

/*
* Class for representing the filtering preferences
*/
public class FilterPreferences {
    private final boolean favourite;
    private final boolean isNearWater;
    private final boolean nearPark;
    private final Float maxDuration;
    private final Float minDuration;
    private final String sortingCondition;
    private final Boolean sortingDirection;

    public FilterPreferences(boolean favourite, boolean isNearWater, boolean nearPark, Float maxDuration, Float minDuration, String sortingCondition, Boolean sortingDirection) {
        this.favourite = favourite;
        this.isNearWater = isNearWater;
        this.nearPark = nearPark;
        this.maxDuration = maxDuration;
        this.minDuration = minDuration;
        this.sortingCondition = sortingCondition;
        this.sortingDirection = sortingDirection;
    }

    /**
     * Creates FilterPreferences object from comma split toString of an FilterPreference Object
     */
    public FilterPreferences(String[] split) {
         this(Boolean.parseBoolean(split[0]), Boolean.parseBoolean(split[1]), Boolean.parseBoolean(split[2]),
                Float.parseFloat(split[3]), Float.parseFloat(split[4]), split[5], Boolean.parseBoolean(split[6]));
    }

    public boolean isFavourite() {
        return favourite;
    }

    public boolean isNearWater() {
        return isNearWater;
    }

    public boolean isNearPark() {
        return nearPark;
    }

    public Float getMaxDuration() {
        return maxDuration;
    }

    public Float getMinDuration() {
        return minDuration;
    }

    public String getSortingCondition() {
        return sortingCondition;
    }

    public Boolean getSortingDirection() {
        return sortingDirection;
    }

    // Creates comma separated string from attributes

    @NonNull
    @Override
    public String toString() {
        return "" + favourite + "," + isNearWater + "," + isNearPark() + "," + maxDuration.toString() +
                "," + minDuration.toString() + "," + sortingCondition + "," + sortingDirection.toString();
    }

}
