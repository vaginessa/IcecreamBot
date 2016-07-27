package com.icecream.bot.core;

import java.io.File;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class Defaults {

    public static final String ROOT_FOLDER      = System.getProperty("user.dir");

    public static final String CONFIG_FOLDER    = Defaults.ROOT_FOLDER + File.separator + "configuration";
    public static final String CONFIG_FILE      = "configuration.json";

}
