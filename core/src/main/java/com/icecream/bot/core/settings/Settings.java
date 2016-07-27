package com.icecream.bot.core.settings;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public abstract class Settings {

    public enum Account {
        PTC,
        GOOGLE
    }

    public static TypeAdapter<Settings> typeAdapter(Gson gson) {
        return new AutoValue_Settings.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_Settings.Builder();
    }

    @SerializedName("account")
    public abstract Account getAccount();
    @SerializedName("latitude")
    public abstract double getLatitude();
    @SerializedName("longitude")
    public abstract double getLongitude();

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder setAccount(Account account);
        public abstract Builder setLatitude(double latitude);
        public abstract Builder setLongitude(double longitude);

        public abstract Settings build();
    }
}