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

package com.icecream.bot.core.api;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import okhttp3.OkHttpClient;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Api {

    // Configurable in the future
    private static final String USER_NAME = "icecreamscanner";
    private static final String USER_PASS = "icecream123";

    private PokemonGo mPokemonGo;
    private OkHttpClient mClient;

    private static volatile Api sInstance = null;

    public static Api getInstance() {
        if (sInstance == null) {
            synchronized (Api.class) {
                if (sInstance == null) {
                    sInstance = new Api();
                }
            }
        }
        return sInstance;
    }

    private Api() {
        mClient = new OkHttpClient();
    }

    public synchronized PokemonGo getPokemonGo() throws LoginFailedException, RemoteServerException {
        if (mPokemonGo == null)
            mPokemonGo = new PokemonGo(new PtcCredentialProvider(mClient, USER_NAME, USER_PASS), mClient);
        return mPokemonGo;
    }
}