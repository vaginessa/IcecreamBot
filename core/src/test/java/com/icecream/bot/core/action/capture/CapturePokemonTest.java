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

package com.icecream.bot.core.action.capture;

import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EncounterResult;
import com.pokegoapi.exceptions.LoginFailedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus;
import POGOProtos.Networking.Responses.EncounterResponseOuterClass.EncounterResponse.Status;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CapturePokemonTest {

    @Mock
    private CatchablePokemon mPokemon;
    @Mock
    private EncounterResult mEncounterResult;
    @Mock
    private CatchResult mCatchResult;

    private CapturePokemon mSut;
    private TestSubscriber<CatchResult> mSubscriber;

    @Before
    public void setUp() throws Exception {
        mSut = new CapturePokemon();
        mSubscriber = new TestSubscriber<>();

        doReturn(PokemonId.BULBASAUR).when(mPokemon).getPokemonId();
    }

    @After
    public void tearDown() throws Exception {
        if (mSubscriber != null)
            mSubscriber.unsubscribe();
    }

    @Test
    public void testCatchItHappyCase() throws Exception {
        System.out.println("Test: testCatchItHappyCase");

        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(true).when(mEncounterResult).wasSuccessful();
        doReturn(mEncounterResult).when(mPokemon).encounterPokemon();

        doReturn(CatchStatus.CATCH_SUCCESS).when(mCatchResult).getStatus();
        doReturn(mCatchResult).when(mPokemon).catchPokemon(any());

        //Then
        observable
                .compose(mSut.catchIt())
                .subscribe(mSubscriber);

        verify(mPokemon, times(1)).catchPokemon(any());
        verify(mPokemon, times(1)).encounterPokemon();
        verify(mCatchResult, times(1)).getStatus();
        verify(mEncounterResult, times(1)).wasSuccessful();

        verifyNoMoreInteractions(mEncounterResult);
        verifyNoMoreInteractions(mCatchResult);
        verifyNoMoreInteractions(mPokemon);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(1));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), containsInAnyOrder(mCatchResult));
    }

    @Test
    public void testCatchItRetryWhenEscapeOrMissed() throws Exception {
        System.out.println("Test: testCatchItRetryWhenEscapeOrMissed");

        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(true).when(mEncounterResult).wasSuccessful();
        doReturn(mEncounterResult).when(mPokemon).encounterPokemon();

        doReturn(CatchStatus.CATCH_ESCAPE).doReturn(CatchStatus.CATCH_MISSED).doReturn(CatchStatus.CATCH_SUCCESS).when(mCatchResult).getStatus();
        doReturn(mCatchResult).when(mPokemon).catchPokemon(any());

        //Then
        observable
                .compose(mSut.catchIt())
                .subscribe(mSubscriber);

        verify(mPokemon, times(3)).catchPokemon(any());
        verify(mPokemon, times(1)).encounterPokemon();
        verify(mCatchResult, times(3)).getStatus();
        verify(mEncounterResult, times(1)).wasSuccessful();

        verifyNoMoreInteractions(mEncounterResult);
        verifyNoMoreInteractions(mCatchResult);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(1));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), containsInAnyOrder(mCatchResult));
    }

    @Test
    public void testCatchItRetryWhenEscapeDontRetryWhenFlee() throws Exception {
        System.out.println("Test: testCatchItRetryWhenEscapeDontRetryWhenFlee");

        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(true).when(mEncounterResult).wasSuccessful();
        doReturn(mEncounterResult).when(mPokemon).encounterPokemon();

        doReturn(CatchStatus.CATCH_ESCAPE).doReturn(CatchStatus.CATCH_MISSED).doReturn(CatchStatus.CATCH_FLEE).when(mCatchResult).getStatus();
        doReturn(mCatchResult).when(mPokemon).catchPokemon(any());

        //Then
        observable
                .compose(mSut.catchIt())
                .subscribe(mSubscriber);

        verify(mPokemon, times(3)).catchPokemon(any());
        verify(mPokemon, times(1)).encounterPokemon();
        verify(mCatchResult, times(3)).getStatus();
        verify(mEncounterResult, times(1)).wasSuccessful();

        verifyNoMoreInteractions(mEncounterResult);
        verifyNoMoreInteractions(mCatchResult);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), is(empty()));
    }

    @Test
    public void testCatchItUnknownStatus() throws Exception {
        System.out.println("Test: testCatchItUnknownStatus");

        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(true).when(mEncounterResult).wasSuccessful();
        doReturn(mEncounterResult).when(mPokemon).encounterPokemon();

        doReturn(CatchStatus.CATCH_ERROR).when(mCatchResult).getStatus();
        doReturn(mCatchResult).when(mPokemon).catchPokemon(any());

        //Then
        observable
                .compose(mSut.catchIt())
                .subscribe(mSubscriber);

        verify(mPokemon).catchPokemon(any());
        verify(mPokemon).encounterPokemon();
        verify(mCatchResult).getStatus();
        verify(mEncounterResult).wasSuccessful();

        verifyNoMoreInteractions(mEncounterResult);
        verifyNoMoreInteractions(mCatchResult);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), is(empty()));
    }

    @Test
    public void testCatchItDontRetryWhenFlee() throws Exception {
        System.out.println("Test: testCatchItDontRetryWhenFlee");

        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(true).when(mEncounterResult).wasSuccessful();
        doReturn(mEncounterResult).when(mPokemon).encounterPokemon();

        doReturn(CatchStatus.CATCH_FLEE).when(mCatchResult).getStatus();
        doReturn(mCatchResult).when(mPokemon).catchPokemon(any());

        //Then
        observable
                .compose(mSut.catchIt())
                .subscribe(mSubscriber);

        verify(mPokemon).catchPokemon(any());
        verify(mPokemon).encounterPokemon();
        verify(mCatchResult).getStatus();
        verify(mEncounterResult).wasSuccessful();

        verifyNoMoreInteractions(mEncounterResult);
        verifyNoMoreInteractions(mCatchResult);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), is(empty()));
    }

    @Test
    public void testCatchItHandlesEncounterError() throws Exception {
        System.out.println("Test: testCatchItHandlesEncounterError");

        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(Status.POKEMON_INVENTORY_FULL).when(mEncounterResult).getStatus();
        doReturn(false).when(mEncounterResult).wasSuccessful();

        doReturn(mEncounterResult).when(mPokemon).encounterPokemon();

        //Then
        observable
                .compose(mSut.catchIt())
                .subscribe(mSubscriber);

        verify(mEncounterResult).wasSuccessful();
        verify(mPokemon).encounterPokemon();
        verify(mPokemon, never()).catchPokemon(any());

        verifyNoMoreInteractions(mEncounterResult);
        verifyNoMoreInteractions(mPokemon);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), empty());
    }

    @Test
    public void testCatchItHandlesEncounterExceptions() throws Exception {
        System.out.println("Test: testCatchItHandlesEncounterExceptions");

        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doThrow(LoginFailedException.class).when(mPokemon).encounterPokemon();

        //Then
        observable
                .compose(mSut.catchIt())
                .subscribe(mSubscriber);

        verify(mPokemon, only()).encounterPokemon();
        verifyZeroInteractions(mEncounterResult);

        mSubscriber.assertNotCompleted();
        mSubscriber.assertError(LoginFailedException.class);
        mSubscriber.assertValueCount(0);

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnErrorEvents(), not(empty()));
    }

    @Test
    public void testCatchItHandlesCatchExceptions() throws Exception {
        System.out.println("Test: testCatchItHandlesCatchExceptions");

        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(true).when(mEncounterResult).wasSuccessful();
        doReturn(mEncounterResult).when(mPokemon).encounterPokemon();

        doThrow(LoginFailedException.class).when(mPokemon).catchPokemon(any());

        //Then
        observable
                .compose(mSut.catchIt())
                .subscribe(mSubscriber);

        verify(mPokemon).catchPokemon(any());
        verify(mPokemon).encounterPokemon();
        verify(mEncounterResult).wasSuccessful();

        verifyZeroInteractions(mCatchResult);
        verifyNoMoreInteractions(mPokemon);
        verifyNoMoreInteractions(mEncounterResult);

        mSubscriber.assertNotCompleted();
        mSubscriber.assertError(LoginFailedException.class);
        mSubscriber.assertValueCount(0);

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnErrorEvents(), not(empty()));
    }
}