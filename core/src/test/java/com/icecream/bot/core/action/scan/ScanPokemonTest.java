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

package com.icecream.bot.core.action.scan;

import java.util.Arrays;
import java.util.List;

import com.pokegoapi.api.map.Map;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.exceptions.LoginFailedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScanPokemonTest {

    @Mock
    Map mMap;
    @Mock
    CatchablePokemon mPokemon1;
    @Mock
    CatchablePokemon mPokemon2;
    @Mock
    CatchablePokemon mPokemon3;
    @Mock
    CatchablePokemon mPokemon4;
    @Mock
    CatchablePokemon mPokemon5;

    private ScanPokemon mSut;
    private List<CatchablePokemon> mPokemons;
    private TestSubscriber<CatchablePokemon> mSubscriber;

    @Rule
    public ExpectedException mException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        mSut = new ScanPokemon();
        mSubscriber = new TestSubscriber<>();
        mPokemons = Arrays.asList(mPokemon1, mPokemon2, mPokemon3, mPokemon4, mPokemon5);
    }

    @After
    public void tearDown() throws Exception {
        if (mSubscriber != null)
            mSubscriber.unsubscribe();
    }

    @Test
    public void testDiscoverThemIsSorted() throws Exception {
        System.out.println("Test: testDiscoverThemIsSorted");

        //Given
        Observable<Map> observable = Observable.just(mMap);

        //When
        doReturn(PokemonId.BULBASAUR).when(mPokemon1).getPokemonId();
        doReturn(System.currentTimeMillis() + 10000).when(mPokemon1).getExpirationTimestampMs();

        doReturn(PokemonId.SQUIRTLE).when(mPokemon2).getPokemonId();
        doReturn(System.currentTimeMillis() + 13000).when(mPokemon2).getExpirationTimestampMs();

        doReturn(PokemonId.CHARMANDER).when(mPokemon3).getPokemonId();
        doReturn(System.currentTimeMillis() + 11000).when(mPokemon3).getExpirationTimestampMs();

        doReturn(PokemonId.PIKACHU).when(mPokemon4).getPokemonId();
        doReturn(System.currentTimeMillis() + 14000).when(mPokemon4).getExpirationTimestampMs();

        doReturn(PokemonId.CHANSEY).when(mPokemon5).getPokemonId();
        doReturn(System.currentTimeMillis() + 12000).when(mPokemon5).getExpirationTimestampMs();

        doReturn(mPokemons).when(mMap).getCatchablePokemon();

        //Then
        observable
                .compose(mSut.discoverThem())
                .subscribe(mSubscriber);

        verify(mMap, only()).getCatchablePokemon();

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(5));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), contains(mPokemon1, mPokemon3, mPokemon5, mPokemon2, mPokemon4));
    }

    @Test
    public void testDiscoverThemHandlesExceptions() throws Exception {
        System.out.println("Test: testDiscoverThemHandlesExceptions");

        //Given
        Observable<Map> observable = Observable.just(mMap);

        //When
        doThrow(LoginFailedException.class).when(mMap).getCatchablePokemon();

        //Then
        observable
                .compose(mSut.discoverThem())
                .subscribe(mSubscriber);

        verify(mMap, only()).getCatchablePokemon();

        mSubscriber.assertNotCompleted();
        mSubscriber.assertError(LoginFailedException.class);

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), empty());
    }
}