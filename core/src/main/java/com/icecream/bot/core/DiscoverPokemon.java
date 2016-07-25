package com.icecream.bot.core;

import com.pokegoapi.api.map.Map;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import rx.Observable;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class DiscoverPokemon {

    private DiscoverPokemon() {
    }

    public static Observable.Transformer<? super Map, ? extends CatchablePokemon> discoverThem() {
        return observable -> observable
                .flatMap(map -> Observable.fromCallable(map::getCatchablePokemon))
                .flatMapIterable(catchables -> catchables);
    }
}
