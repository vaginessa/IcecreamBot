/*
 * Copyright (c) 2016. Pedro Diaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icecream.bot.core.log;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Log {

    private static final String COLOR_WHITE         = "\033[30m";
    private static final String COLOR_RED           = "\033[31m";
    private static final String COLOR_GREEN         = "\033[32m";
    private static final String COLOR_YELLOW        = "\033[33m";
    private static final String COLOR_DARK_BLUE     = "\033[34m";
    private static final String COLOR_PURPLE        = "\033[35m";
    private static final String COLOR_LIGHT_BLUE    = "\033[36m";
    private static final String COLOR_GREY          = "\033[37m";
    private static final String COLOR_DEFAULT       = "\033[0m";

    private Log() {
    }

    private static void log(String tag, String color, String format, Object... objects) {
        System.out.println(color + String.format("[%-13s] ", tag) + String.format(format, objects) + COLOR_DEFAULT);
    }

    public static void a(String tag, String format, Object... objects) {
        log(tag, COLOR_LIGHT_BLUE, format, objects);
    }

    public static void d(String tag, String format, Object... objects) {
        log(tag, COLOR_DARK_BLUE, format, objects);
    }

    public static void e(String tag, String format, Object... objects) {
        log(tag, COLOR_RED, format, objects);
    }

    public static void i(String tag, String format, Object... objects) {
        log(tag, COLOR_GREEN, format, objects);
    }

    public static void v(String tag, String format, Object... objects) {
        log(tag, COLOR_PURPLE, format, objects);
    }

    public static void w(String tag, String format, Object... objects) {
        log(tag, COLOR_YELLOW, format, objects);
    }
}
