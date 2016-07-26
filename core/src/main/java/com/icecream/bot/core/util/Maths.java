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

package com.icecream.bot.core.util;

import java.util.List;
import javax.annotation.Nonnull;

import rx.Observable;
import rx.observables.MathObservable;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Maths {

    private Maths() {
        throw new AssertionError("No instances.");
    }

    public static int sumElements(@Nonnull List<? extends Integer> list) {
        return MathObservable.sumInteger(Observable.from(list)).toBlocking().first();
    }
}