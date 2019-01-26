package com.bitirme.gitbusters.borkinroads;

/*
* Class for representing the filtering preferences
*/
public class FilterPreferences {
    private boolean favourite;
    private boolean isNearWater;
    private boolean nearPark;
    private Float maxDuration;
    private Float minDuration;
    private String sortingCondtion;
    private Boolean sortingDirection;

    public FilterPreferences(boolean favourite, boolean isNearWater, boolean nearPark, Float maxDuration, Float minDuration, String sortingCondtion, Boolean sortingDirection) {
        this.favourite = favourite;
        this.isNearWater = isNearWater;
        this.nearPark = nearPark;
        this.maxDuration = maxDuration;
        this.minDuration = minDuration;
        this.sortingCondtion = sortingCondtion;
        this.sortingDirection = sortingDirection;
    }

    /**
    * Creates FilterPreferences object from comma splited toString of an FilterPrefenence Object
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

    public String getSortingCondtion() {
        return sortingCondtion;
    }

    public Boolean getSortingDirection() {
        return sortingDirection;
    }

    /**
    * Creates comma seperated string from attributes
     */
    @Override
    public String toString() {
        return "" + favourite + "," + isNearWater + "," + isNearPark() + "," + maxDuration.toString() +
                "," + minDuration.toString() + "," + sortingCondtion + "," + sortingDirection.toString();
    }

}
