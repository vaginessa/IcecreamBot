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

package com.icecream.bot.core.capture.exception;

import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class CaptureExceptionFactory {

    private CaptureExceptionFactory() {
    }

    public static CaptureException create(CatchablePokemon pokemon, CatchStatus status) {
        switch (status) {
            case CATCH_SUCCESS:
                throw new IllegalArgumentException("CATCH_SUCCESS is not a CatchException");
            case CATCH_FLEE:
                return new CaptureExceptionFlee(pokemon);
            case CATCH_ESCAPE:
                return new CaptureExceptionEscape(pokemon);
            case CATCH_MISSED:
                return new CaptureExceptionMiss(pokemon);
            case CATCH_ERROR:
            default:
                return new CaptureExceptionError(pokemon);
        }
    }
}
