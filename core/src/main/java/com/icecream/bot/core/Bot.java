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

package com.icecream.bot.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icecream.bot.core.action.capture.CapturePokemon;
import com.icecream.bot.core.action.scan.ScanPokemon;
import com.icecream.bot.core.api.Api;
import com.icecream.bot.core.io.FileWrite;
import com.icecream.bot.core.setting.Configuration;
import com.icecream.bot.core.util.Logs;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;
import rx.Observable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class Bot {

    private final ScanPokemon mScanPokemon;
    private final CapturePokemon mCapturePokemon;

    public Bot(@Nonnull ScanPokemon scanPokemon, @Nonnull CapturePokemon capturePokemon) {
        mScanPokemon = scanPokemon;
        mCapturePokemon = capturePokemon;
    }

    /*
    public Observable<CatchResult> farmNearbyPokemons(Poke) {
        return Observable
                .fromCallable(Api::getInstance)
                .doOnNext(api -> api.setLocation(latitude, longitude, 0))
                .map(PokemonGo::getMap)
                .compose(mScanPokemon.discoverThem())
                .compose(mCapturePokemon.catchIt());
    }


    public Observable.Transformer<? extends PokemonGo, Object> farmNearbyPokemons() {
        return observable -> observable
                .map(PokemonGo::getMap)
                .compose(mScanPokemon.discoverThem())
                .compose(mCapturePokemon.catchIt());
    }
    */

    public Observable.Transformer<Api, CatchResult> farmPokemons() {
        return observable -> observable
                .map(PokemonGo::getMap)
                .compose(mScanPokemon.discoverThem())
                .compose(mCapturePokemon.catchIt());
    }

    public static Observable.Transformer<Throwable, Long> retryWithDelay() {
        return observable -> observable.flatMap(error -> {
            if (error instanceof RemoteServerException) {
                Logs.e("SERVER", "Cannot reach the server, could be down?");
                return Observable.timer(1, TimeUnit.SECONDS);
            }
            return Observable.error(error);
        });
    }

    public static <T> Observable.Transformer<T, T> repeatWithDelay() {
        return observable -> observable.delay(10, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException, LoginFailedException, RemoteServerException, IOException {

        System.out.println(System.getProperty("user.dir"));

        Configuration configuration = Configuration.builder()
                .setAccount(Configuration.Account.PTC)
                .setLatitude(34.010112)
                .setLongitude(-118.495739)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();

        String json = gson.toJson(configuration);

        new FileWrite(Defaults.ROOT_FOLDER + File.separator + Defaults.CONFIG_FOLDER, Defaults.CONFIG_FILE)
                .write(json)
                .subscribe();


        System.out.println(json);
        System.out.println(gson.fromJson(json, Configuration.class).toString());

        /*
        // Santa Monica Pier
        final double LOCATION_LAT = 34.010112;
        final double LOCATION_LON = -118.495739;

        // Farm pokemons
        //PokemonGo pokemonGo = ;
        ScanPokemon scanPokemon = new ScanPokemon();
        CapturePokemon capturePokemon = new CapturePokemon();

        Bot bot = new Bot(scanPokemon, capturePokemon);

        Subscription subscription = Observable
                .fromCallable(Api::getInstance)
                .doOnNext(api -> api.setLocation(LOCATION_LAT, LOCATION_LON, 0))
                .compose(bot.farmPokemons())
                .retryWhen(errors -> errors.compose(retryWithDelay()))
                .repeatWhen(completed -> completed.compose(repeatWithDelay()))
                .subscribe();

        while (!subscription.isUnsubscribed()) {
            Thread.sleep(1000);
        }


        new GoogleCredentialProvider(new OkHttpClient.Builder().proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("200.2.15.207", 3128))).build(), new GoogleCredentialProvider.OnGoogleLoginOAuthCompleteListener() {
            @Override
            public void onInitialOAuthComplete(GoogleAuthJson googleAuthJson) {
                Logs.d("Initial");
            }

            @Override
            public void onTokenIdReceived(GoogleAuthTokenJson googleAuthTokenJson) {
                Logs.d("Received");
            }
        });
        */
        /*
        PokemonGo pokemonGo = Api.getInstance();

        Observable
                .zip(
                        Observable.range(1, 10),
                        Observable.interval(2, TimeUnit.SECONDS),
                        (integer, time) -> integer
                )
                .doOnNext(System.out::println)
                .toList()
                .toBlocking()
                .first();
        */



        /*
        Observable
                .fromCallable(() -> pokemonGo.getMap().getSpawnPoints())
                .flatMapIterable(points -> points)
                .toSortedList((point1, point2) -> Double.compare(Distances.get(pokemonGo, point1), Distances.get(pokemonGo, point2)))
                .flatMapIterable(points -> points)
                .take(15)
                .forEach(point -> Logs.d("%.2f m", Distances.get(pokemonGo, point)));

        System.out.println("OOOOOOOOOOOOOOOOOOKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");

        Observable
                .fromCallable(() -> pokemonGo.getMap().getDecimatedSpawnPoints())
                .flatMapIterable(points -> points)
                .toSortedList((point1, point2) -> Double.compare(Distances.get(pokemonGo, point1), Distances.get(pokemonGo, point2)))
                .flatMapIterable(points -> points)
                .take(5)
                .forEach(point -> Logs.d("%.2f m", Distances.get(pokemonGo, point)));
                */
    }

}
