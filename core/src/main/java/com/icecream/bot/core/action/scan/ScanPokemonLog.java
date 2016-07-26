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

package com.icecream.bot.core.action.scan;

import com.icecream.bot.core.log.Log;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
final class ScanPokemonLog {

    private static final String FOUND = "FOUND";

    private ScanPokemonLog() {
    }

    static void scanPokemon(CatchablePokemon pokemon) {
        Log.w(FOUND, "Pokemon %-15s expires in %d seconds",
                pokemon.getPokemonId().name(),
                Math.round((pokemon.getExpirationTimestampMs() - System.currentTimeMillis()) / 1000.0)
        );
    }
}
