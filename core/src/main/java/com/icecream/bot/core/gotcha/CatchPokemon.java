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

package com.icecream.bot.core.gotcha;

import java.util.concurrent.Callable;

import com.pokegoapi.api.inventory.Pokeball;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EncounterResult;

import rx.Observable;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class CatchPokemon {

    private CatchPokemon() {
    }

    private static Callable<CatchResult> throwPokeball(final CatchablePokemon pokemon) {
        return () -> pokemon.catchPokemon(Pokeball.POKEBALL);
    }

    private static Observable.Transformer<? super CatchablePokemon, ? extends CatchResult> attemptCatch() {
        return observable -> observable
                .flatMap(pokemon ->
                        Observable
                                .fromCallable(throwPokeball(pokemon))
                                .flatMap(result -> {
                                    switch (result.getStatus()) {
                                        case CATCH_SUCCESS:
                                            return Observable.just(result);
                                        case CATCH_FLEE:
                                            return Observable.error(new CatchFleeException());
                                        case CATCH_ESCAPE:
                                            return Observable.error(new CatchMissedException());
                                        case CATCH_MISSED:
                                            return Observable.error(new CatchEscapeException());
                                        default:
                                            return Observable.empty();
                                    }
                                })
                                .retryWhen(errors -> errors
                                        .takeWhile(error -> !(error instanceof CatchFleeException))
                                        .flatMap(error -> {
                                            if (error instanceof CatchEscapeException) {
                                                return Observable.just(pokemon);
                                            }
                                            if (error instanceof CatchMissedException) {
                                                return Observable.just(pokemon);
                                            }
                                            return Observable.<CatchResult>error(error);
                                        })
                                )
                );
    }

    private static Observable.Transformer<? super CatchablePokemon, ? extends CatchablePokemon> attemptEncounter() {
        return observable -> observable
                .flatMap(pokemon -> Observable
                        .fromCallable(pokemon::encounterPokemon)
                        .filter(EncounterResult::wasSuccessful)
                        .map(result -> pokemon)
                );
    }

    public static Observable.Transformer<? super CatchablePokemon, ? extends CatchResult> catchIt() {
        return observable -> observable
                .compose(attemptEncounter())
                .compose(attemptCatch());
    }
}
