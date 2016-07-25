package com.icecream.bot.core;

import com.pokegoapi.api.map.Map;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;

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

    @Rule
    public ExpectedException mException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        mPokemons = Arrays.asList(mPokemon1, mPokemon2, mPokemon3, mPokemon4, mPokemon5);
    }

    @Test
    public void testDiscover() throws Exception {
        //Given
        Observable<Map> observable = Observable.just(mMap);
        TestSubscriber<CatchablePokemon> subscriber = new TestSubscriber<>();

        //When
        doReturn(mPokemons).when(mMap).getCatchablePokemon();

        //Then
        observable
                .compose(DiscoverPokemon.discover())
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertUnsubscribed();

        assertThat("Source emitted unexpected number of items", subscriber.getValueCount(), is(5));
        assertThat("Source emitted unexpected items", subscriber.getOnNextEvents(), containsInAnyOrder(mPokemon1, mPokemon2, mPokemon3, mPokemon4, mPokemon5));
    }
}