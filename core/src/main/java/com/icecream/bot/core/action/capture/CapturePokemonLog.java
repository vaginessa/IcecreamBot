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

package com.icecream.bot.core.action.capture;

import com.icecream.bot.core.action.ActionLog;
import com.icecream.bot.core.action.capture.exception.CaptureException;
import com.icecream.bot.core.util.Logs;
import com.icecream.bot.core.util.Maths;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
final class CapturePokemonLog {

    private static final String CATCH = "CATCH_TRY";
    private static final String CATCH_SUCCESS = "CATCH_SUCCESS";

    private CapturePokemonLog() {
    }

    static void captureTryPokemon(CatchablePokemon pokemon, CatchResult result) {
        Logs.w(CATCH, "Pokemon %-15s with %3.2f%% miss rate",
                pokemon.getPokemonId().name(),
                result.getMissPercent() * 100
        );
    }

    static void capturePokemon(CatchablePokemon pokemon, CatchResult result) {
        Logs.i(CATCH_SUCCESS, "Pokemon %-15s captured!",
                pokemon.getPokemonId().name(),
                result.getXpList()
        );
        ActionLog.reward(
                Maths.sumElements(result.getXpList()),
                Maths.sumElements(result.getStardustList()),
                Maths.sumElements(result.getCandyList())
        );
    }

    static void captureErrorPokemon(CaptureException error) {
        Logs.e(error.getReason(), "%s. Cannot catch pokemon %-15s",
                error.isRetry() ? "Will retry" : "Awww snap!",
                error.getPokemon().getPokemonId().name()
        );
    }
}
