package com.icecream.bot.core;

import POGOProtos.Networking.Responses.EncounterResponseOuterClass.EncounterResponse.Status;
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
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CatchPokemonTest {

    @Mock
    private CatchablePokemon mPokemon;
    @Mock
    private EncounterResult mResult;

    private TestSubscriber<CatchResult> mSubscriber;

    @Before
    public void setUp() throws Exception {
        mSubscriber = new TestSubscriber<>();
    }

    @After
    public void tearDown() throws Exception {
        if (mSubscriber != null)
            mSubscriber.unsubscribe();
    }

    @Test
    public void testCatchItHappyCase() throws Exception {
        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(true).when(mResult).wasSuccessful();
        doReturn(mResult).when(mPokemon).encounterPokemon();

        //Then
        observable
                .compose(CatchPokemon.catchIt())
                .subscribe(mSubscriber);

        verify(mResult, times(1)).wasSuccessful();
        verify(mPokemon, times(1)).encounterPokemon();
        verify(mPokemon, times(1)).catchPokemon(any());

        verifyNoMoreInteractions(mResult);
        verifyNoMoreInteractions(mPokemon);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();
        mSubscriber.assertValueCount(1);
    }

    @Test
    public void testCatchItHandlesEncounterError() throws Exception {
        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(Status.POKEMON_INVENTORY_FULL).when(mResult).getStatus();
        doReturn(false).when(mResult).wasSuccessful();

        doReturn(mResult).when(mPokemon).encounterPokemon();

        //Then
        observable
                .compose(CatchPokemon.catchIt())
                .subscribe(mSubscriber);

        verify(mResult, times(1)).wasSuccessful();
        verify(mPokemon, times(1)).encounterPokemon();
        verify(mPokemon, never()).catchPokemon(any());

        verifyNoMoreInteractions(mResult);
        verifyNoMoreInteractions(mPokemon);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();
        mSubscriber.assertValueCount(0);
    }

    @Test
    public void testCatchItHandlesExceptions() throws Exception {
        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doThrow(LoginFailedException.class).when(mPokemon).encounterPokemon();

        //Then
        observable
                .compose(CatchPokemon.catchIt())
                .subscribe(mSubscriber);

        verify(mPokemon, only()).encounterPokemon();

        verifyNoMoreInteractions(mResult);
        verifyNoMoreInteractions(mPokemon);

        mSubscriber.assertNotCompleted();
        mSubscriber.assertError(LoginFailedException.class);
        mSubscriber.assertValueCount(0);
    }
}