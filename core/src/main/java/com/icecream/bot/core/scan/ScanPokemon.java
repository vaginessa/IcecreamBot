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

package com.icecream.bot.core.scan;

import com.pokegoapi.api.map.Map;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import rx.Observable;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class ScanPokemon {

    private ScanPokemon() {
    }

    public static Observable.Transformer<? super Map, ? extends CatchablePokemon> discoverThem() {
        return observable -> observable
                .flatMap(map -> Observable.fromCallable(map::getCatchablePokemon))
                .flatMapIterable(catchables -> catchables);
    }
}
