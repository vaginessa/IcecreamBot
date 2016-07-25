package com.icecream.bot.core;

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
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DiscoverPokemonTest {

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

    private List<CatchablePokemon> mPokemons;
    private TestSubscriber<CatchablePokemon> mSubscriber;

    @Rule
    public ExpectedException mException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        mSubscriber = new TestSubscriber<>();
        mPokemons = Arrays.asList(mPokemon1, mPokemon2, mPokemon3, mPokemon4, mPokemon5);
    }

    @After
    public void tearDown() throws Exception {
        if (mSubscriber != null)
            mSubscriber.unsubscribe();
    }

    @Test
    public void testDiscoverThemHappyCase() throws Exception {
        //Given
        Observable<Map> observable = Observable.just(mMap);

        //When
        doReturn(mPokemons).when(mMap).getCatchablePokemon();

        //Then
        observable
                .compose(DiscoverPokemon.discoverThem())
                .subscribe(mSubscriber);

        verify(mMap, only()).getCatchablePokemon();

        mSubscriber.assertCompleted();
        mSubscriber.assertNoErrors();

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(5));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), containsInAnyOrder(mPokemon1, mPokemon2, mPokemon3, mPokemon4, mPokemon5));
    }

    @Test
    public void testDiscoverThemHandlesExceptions() throws Exception {
        //Given
        Observable<Map> observable = Observable.just(mMap);

        //When
        doThrow(LoginFailedException.class).when(mMap).getCatchablePokemon();

        //Then
        observable
                .compose(DiscoverPokemon.discoverThem())
                .subscribe(mSubscriber);

        verify(mMap, only()).getCatchablePokemon();

        mSubscriber.assertNotCompleted();
        mSubscriber.assertError(LoginFailedException.class);

        assertThat("Source emitted unexpected number of items", mSubscriber.getValueCount(), is(0));
        assertThat("Source emitted unexpected items", mSubscriber.getOnNextEvents(), empty());
    }
}