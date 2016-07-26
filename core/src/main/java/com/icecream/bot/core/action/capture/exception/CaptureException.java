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

package com.icecream.bot.core.action.capture.exception;

import com.icecream.bot.core.action.ActionException;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
abstract class CaptureException extends ActionException {

    private final CatchablePokemon mPokemon;

    CaptureException(CatchablePokemon pokemon, CatchStatus status) {
        super(status.name());
        mPokemon = pokemon;
    }

    public final CatchablePokemon getPokemon() {
        return mPokemon;
    }
}
