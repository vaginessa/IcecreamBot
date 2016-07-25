package com.icecream.bot.core;

import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EncounterResult;
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
    }

}