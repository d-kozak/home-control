package io.dkozak.home.control.utils;

import lombok.var;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Streams {

    public static <T> Stream<T> streamOf(Iterable<T> iterable) {
        // unfortunately, there is no easy way prior to Java 9
        var iterator = iterable.iterator();
        var list = new ArrayList<T>();
        iterator.forEachRemaining(list::add);

        return list.stream();
    }
}
