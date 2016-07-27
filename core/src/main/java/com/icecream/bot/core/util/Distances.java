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

package com.icecream.bot.core.util;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.Point;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Distances {

    private Distances() {
    }

    static double get(double fromLat, double fromLon, double toLat, double toLon) {
        double r_earth = 6378137;
        double d_lat = (toLat - fromLat) * Math.PI / 180;
        double d_lon = (toLon - fromLon) * Math.PI / 180;
        double alpha = Math.sin(d_lat / 2) * Math.sin(d_lat / 2)
                + Math.cos(fromLat * Math.PI / 180) * Math.cos(toLat * Math.PI / 180)
                * Math.sin(d_lon / 2) * Math.sin(d_lon / 2);
        return 2 * r_earth * Math.atan2(Math.sqrt(alpha), Math.sqrt(1 - alpha));
    }

    public static double get(PokemonGo pokemonGo, Point point) {
        return get(pokemonGo.getLatitude(), pokemonGo.getLongitude(), point.getLatitude(), point.getLongitude());
    }
}
