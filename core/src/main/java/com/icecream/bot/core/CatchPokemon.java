package com.icecream.bot.core;

import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import rx.Observable;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class CatchPokemon {

    private CatchPokemon() {
    }

    public static Observable.Transformer<? super CatchablePokemon, ? extends CatchResult> catchIt() {
        return null;//observable -> observable
    }
}
