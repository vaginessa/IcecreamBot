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

import com.icecream.bot.core.action.capture.exception.CaptureException;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Logger {

    private static final String FOUND = "FOUND";
    private static final String CATCH = "CATCH";

    private Logger() {
    }

    public static void scanPokemon(final CatchablePokemon pokemon) {
        Log.i(FOUND, "Pokemon #%3d %11s expires in %d seconds",
                pokemon.getPokemonId().getNumber(),
                pokemon.getPokemonId().name(),
                Math.round((pokemon.getExpirationTimestampMs() - System.currentTimeMillis()) / 1000.0)
        );
    }

    public static void capturePokemon(final CatchablePokemon pokemon) {
        Log.i(FOUND, "Pokemon #%3d %11s expires in %d seconds",
                pokemon.getPokemonId().getNumber(),
                pokemon.getPokemonId().name(),
                Math.round((pokemon.getExpirationTimestampMs() - System.currentTimeMillis()) / 1000.0)
        );
    }

    public static void captureErrorPokemon(CaptureException error) {
        Log.e(error.getReason(), "Cannot catch pokemon #%3d %11s%s",
                error.getPokemon().getPokemonId().getNumber(),
                error.getPokemon().getPokemonId().name(),
                error.isRetry() ? ". Will retry" : ""
        );
    }
}
