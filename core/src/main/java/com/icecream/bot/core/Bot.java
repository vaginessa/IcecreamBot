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

package com.icecream.bot.core;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.icecream.bot.core.scan.ScanPokemon;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.PtcLogin;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import okhttp3.OkHttpClient;
import rx.Observable;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class Bot {

    private final PokemonGo mPokemonGo;

    @Inject
    public Bot(@Nonnull PokemonGo pokemonGo) {
        mPokemonGo = pokemonGo;
    }

    public void start(double latitude, double longitude) throws LoginFailedException, RemoteServerException {
        mPokemonGo.setLocation(latitude, longitude, 0);

        ScanPokemon scanPokemon = new ScanPokemon();

        Observable
                .just(mPokemonGo.getMap())
                .compose(scanPokemon.discoverThem())
                .subscribe(o -> {}, Throwable::printStackTrace);
    }

    public static void main(String[] args) throws LoginFailedException, RemoteServerException {
        final String USER_NAME = "icecreamscanner";
        final String USER_PASS = "icecream123";

        final double LOCATION_LAT = 34.010112;
        final double LOCATION_LON = -118.495739;

        OkHttpClient client = new OkHttpClient();

        Bot bot = new Bot(new PokemonGo(new PtcLogin(client).login(USER_NAME, USER_PASS), client));
        bot.start(LOCATION_LAT, LOCATION_LON);
    }
}
