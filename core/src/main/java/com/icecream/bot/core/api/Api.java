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
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.net.Proxy;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Api extends PokemonGo {

    // Configurable in the future
    private static final String USER_NAME = "icecreamscanner";
    private static final String USER_PASS = "icecream123";

    private static volatile Api sInstance = null;

    public static Api getInstance() throws LoginFailedException, RemoteServerException {
        if (sInstance == null) {
            synchronized (Api.class) {
                if (sInstance == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("200.2.15.207", 3128)))
                            .build();

                    sInstance = new Api(new PtcCredentialProvider(client, USER_NAME, USER_PASS), client);
                }
            }
        }
        return sInstance;
    }

    public Api(@Nonnull CredentialProvider provider, @Nonnull OkHttpClient client) throws LoginFailedException, RemoteServerException {
        super(provider, client);
    }
}