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

import java.util.concurrent.Callable;

import com.icecream.bot.core.action.capture.exception.CaptureException;
import com.icecream.bot.core.action.capture.exception.CaptureExceptionFactory;
import com.icecream.bot.core.log.Logger;
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

    public final Observable.Transformer<? super CatchablePokemon, ? extends CatchResult> catchIt() {
        return observable -> observable
                .compose(attemptEncounter())
                .compose(attemptCatch());
    }

    private Observable.Transformer<? super CatchablePokemon, ? extends CatchablePokemon> attemptEncounter() {
        return observable -> observable
                .flatMap(pokemon -> Observable
                        .fromCallable(pokemon::encounterPokemon)
                        .filter(EncounterResult::wasSuccessful)
                        .map(result -> pokemon)
                );
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
                                .doOnError(error -> {
                                    if (error instanceof CaptureException) {
                                        Logger.captureErrorPokemon((CaptureException) error);
                                    }
                                })
                                .retryWhen(errors -> errors
                                        .takeWhile(this::cannotRetryException)
                                        .flatMap(error -> canRetryException(error) ? Observable.just(pokemon) : Observable.<CatchResult>error(error))
                                )
                );
    }

    private Callable<CatchResult> throwPokeball(final CatchablePokemon pokemon) {
        return () -> pokemon.catchPokemon(Pokeball.POKEBALL);
    }

    private boolean canRetryException(Throwable throwable) {
        if (throwable instanceof CaptureException) {
            CaptureException exception = (CaptureException) throwable;
            return exception.isRetry();
        }
        return false;
    }

    private boolean cannotRetryException(Throwable throwable) {
        if (throwable instanceof CaptureException) {
            CaptureException exception = (CaptureException) throwable;
            return exception.isRetry();
        }
        return true;
    }
}
