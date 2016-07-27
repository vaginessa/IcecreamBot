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

import com.icecream.bot.core.action.capture.CapturePokemon;
import com.icecream.bot.core.action.scan.ScanPokemon;
import com.icecream.bot.core.api.Api;
import com.icecream.bot.core.util.Logs;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import rx.Observable;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class Bot {

    private final ScanPokemon mScanPokemon;
    private final CapturePokemon mCapturePokemon;

    public Bot(@Nonnull ScanPokemon scanPokemon, @Nonnull CapturePokemon capturePokemon) {
        mScanPokemon = scanPokemon;
        mCapturePokemon = capturePokemon;
    }

    public Observable<?> farmNearbyPokemons(double latitude, double longitude) {
        return Observable
                .fromCallable(() -> Api.getInstance().getPokemonGo())
                .doOnNext(pokemonGo -> pokemonGo.setLocation(latitude, longitude, 0))
                .map(PokemonGo::getMap)
                .compose(mScanPokemon.discoverThem())
                .compose(mCapturePokemon.catchIt());
    }

    public static void main(String[] args) throws LoginFailedException, RemoteServerException {

        // Santa Monica Pier
        final double LOCATION_LAT = 34.010112;
        final double LOCATION_LON = -118.495739;

        // Farm pokemons
        new Bot(new ScanPokemon(), new CapturePokemon())
                .farmNearbyPokemons(LOCATION_LAT, LOCATION_LON)
                .subscribe(
                        o -> {},
                        Throwable::printStackTrace,
                        () -> Logs.d("DEBUG", "Completed")
                );
    }
}
