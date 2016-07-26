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

import com.pokegoapi.api.map.pokemon.CatchablePokemon;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Logger {

    private static final String FOUND = "FOUND";

    private Logger() {
    }

    public static void scanPokemon(final CatchablePokemon pokemon) {
        Log.i(FOUND, "Pokemon #%3d %15s expires in %d seconds",
                pokemon.getPokemonId().getNumber(),
                pokemon.getPokemonId().name(),
                Math.round((pokemon.getExpirationTimestampMs() - System.currentTimeMillis()) / 1000.0)
        );
    }
}
