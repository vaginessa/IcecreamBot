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
import com.icecream.bot.core.util.Maths;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Logger {

    private static final String FOUND = "FOUND";
    private static final String CATCH = "CATCH";
    private static final String REWARD = "REWARD";
    private static final String CATCH_SUCCESS = "CATCH_SUCCESS";

    private Logger() {
    }

    public static void reward(int exp, int stardust, int candy) {
        Log.d(REWARD, "Exp:%4d Stardust:%4d Candy:%2d",
                exp,
                stardust,
                candy
        );
    }

    public static void scanPokemon(CatchablePokemon pokemon) {
        Log.w(FOUND, "Pokemon %-15s expires in %d seconds",
                pokemon.getPokemonId().name(),
                Math.round((pokemon.getExpirationTimestampMs() - System.currentTimeMillis()) / 1000.0)
        );
    }

    public static void captureTryPokemon(CatchablePokemon pokemon, CatchResult result) {
        Log.w(CATCH, "Trying catch pokemon %15s with %3.2f%% miss rate",
                pokemon.getPokemonId().name(),
                result.getMissPercent() * 100
        );
    }

    public static void capturePokemon(CatchablePokemon pokemon, CatchResult result) {
        Log.i(CATCH_SUCCESS, "%-15s captured!",
                pokemon.getPokemonId().name(),
                result.getXpList()
        );
        reward(
                Maths.sumElements(result.getXpList()),
                Maths.sumElements(result.getStardustList()),
                Maths.sumElements(result.getCandyList())
        );
    }

    public static void captureErrorPokemon(CaptureException error) {
        Log.e(error.getReason(), "Cannot catch pokemon %15s%s",
                error.getPokemon().getPokemonId().name(),
                error.isRetry() ? ". Will retry" : ""
        );
    }
}
