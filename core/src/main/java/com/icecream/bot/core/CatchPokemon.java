package com.icecream.bot.core;

import com.pokegoapi.api.inventory.Pokeball;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EncounterResult;
import rx.Observable;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class CatchPokemon {

    private CatchPokemon() {
    }

    public static Observable.Transformer<? super CatchablePokemon, ? extends CatchResult> catchIt() {
        return observable -> observable
                .flatMap(pokemon -> Observable
                        .fromCallable(pokemon::encounterPokemon)
                        .filter(EncounterResult::wasSuccessful)
                        .map(data -> pokemon)
                )
                .flatMap(pokemon -> Observable.fromCallable(() -> pokemon.catchPokemon(Pokeball.POKEBALL)));
    }
}
