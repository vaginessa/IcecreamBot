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

package com.icecream.bot.core.capture;

import java.util.concurrent.Callable;

import com.icecream.bot.core.capture.exception.CaptureExceptionError;
import com.icecream.bot.core.capture.exception.CaptureExceptionEscape;
import com.icecream.bot.core.capture.exception.CaptureExceptionFactory;
import com.icecream.bot.core.capture.exception.CaptureExceptionFlee;
import com.icecream.bot.core.capture.exception.CaptureExceptionMiss;
import com.pokegoapi.api.inventory.Pokeball;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EncounterResult;

import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus;
import rx.Observable;

import static POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus.CATCH_SUCCESS;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class CapturePokemon {

    public CapturePokemon() {
    }

    private Callable<CatchResult> throwPokeball(final CatchablePokemon pokemon) {
        return () -> pokemon.catchPokemon(Pokeball.POKEBALL);
    }

    private Observable.Transformer<? super CatchablePokemon, ? extends CatchResult> attemptCatch() {
        return observable -> observable
                .flatMap(pokemon ->
                        Observable
                                .fromCallable(throwPokeball(pokemon))
                                .flatMap(result -> {
                                    final CatchStatus status = result.getStatus();
                                    return status == CATCH_SUCCESS ? Observable.just(result) : Observable.error(CaptureExceptionFactory.create(pokemon, status));
                                })
                                .retryWhen(errors -> errors
                                        .takeWhile(error -> !(error instanceof CaptureExceptionFlee) && !(error instanceof CaptureExceptionError))
                                        .flatMap(error -> (error instanceof CaptureExceptionEscape) || (error instanceof CaptureExceptionMiss) ? Observable.just(pokemon) : Observable.<CatchResult>error(error))
                                )
                );
    }

    private Observable.Transformer<? super CatchablePokemon, ? extends CatchablePokemon> attemptEncounter() {
        return observable -> observable
                .flatMap(pokemon -> Observable
                        .fromCallable(pokemon::encounterPokemon)
                        .filter(EncounterResult::wasSuccessful)
                        .map(result -> pokemon)
                );
    }

    public final Observable.Transformer<? super CatchablePokemon, ? extends CatchResult> catchIt() {
        return observable -> observable
                .compose(attemptEncounter())
                .compose(attemptCatch());
    }
}
