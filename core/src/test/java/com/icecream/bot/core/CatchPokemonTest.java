package com.icecream.bot.core;

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
public class CatchPokemonTest {

    @Mock
    private CatchablePokemon mPokemon;
    @Mock
    private EncounterResult mEncounterResult;
    @Mock
    private CatchResult mCatchResult;

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
        doReturn(true).when(mEncounterResult).wasSuccessful();
        doReturn(mEncounterResult).when(mPokemon).encounterPokemon();

        doReturn(CatchStatus.CATCH_SUCCESS).when(mCatchResult).getStatus();
        doReturn(mCatchResult).when(mPokemon).catchPokemon(any());

        //Then
        observable
                .compose(CatchPokemon.catchIt())
                .subscribe(mSubscriber);

        verify(mEncounterResult, times(1)).wasSuccessful();
        verify(mPokemon, times(1)).encounterPokemon();
        verify(mPokemon, times(1)).catchPokemon(any());

        verifyNoMoreInteractions(mEncounterResult);
        verifyNoMoreInteractions(mPokemon);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(1));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), containsInAnyOrder(mCatchResult));
    }

    @Test
    public void testCatchItHandlesEncounterError() throws Exception {
        //Given
        Observable<CatchablePokemon> observable = Observable.just(mPokemon);

        //When
        doReturn(Status.POKEMON_INVENTORY_FULL).when(mEncounterResult).getStatus();
        doReturn(false).when(mEncounterResult).wasSuccessful();

        doReturn(mEncounterResult).when(mPokemon).encounterPokemon();

        //Then
        observable
                .compose(CatchPokemon.catchIt())
                .subscribe(mSubscriber);

        verify(mEncounterResult, times(1)).wasSuccessful();
        verify(mPokemon, times(1)).encounterPokemon();
        verify(mPokemon, never()).catchPokemon(any());

        verifyNoMoreInteractions(mEncounterResult);
        verifyNoMoreInteractions(mPokemon);

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), empty());
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
        verifyZeroInteractions(mEncounterResult);

        mSubscriber.assertNotCompleted();
        mSubscriber.assertError(LoginFailedException.class);
        mSubscriber.assertValueCount(0);

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnErrorEvents(), not(empty()));
    }
}