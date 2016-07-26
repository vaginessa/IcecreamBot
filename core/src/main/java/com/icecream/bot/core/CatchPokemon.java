package com.icecream.bot.core;

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

    private static Observable.Transformer<? super CatchablePokemon, ? extends CatchResult> attempCatch() {
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
                                            return Observable.<CatchResult>error(error);
                                        }))
                );
    }

    private static Observable.Transformer<? super CatchablePokemon, ? extends CatchablePokemon> attempEncounter() {
        return observable -> observable
                .flatMap(pokemon -> Observable
                        .fromCallable(pokemon::encounterPokemon)
                        .filter(EncounterResult::wasSuccessful)
                        .map(result -> pokemon)
                );
    }

    public static Observable.Transformer<? super CatchablePokemon, ? extends CatchResult> catchIt() {
        return observable -> observable
                .compose(attempEncounter())
                .compose(attempCatch());
    }
}
