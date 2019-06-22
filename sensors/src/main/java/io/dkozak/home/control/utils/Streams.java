package io.dkozak.home.control.utils;

import java.util.stream.Stream;

public class Streams {

    public static <T> Stream<T> streamOf(Iterable<T> iterable) {
        var iterator = iterable.iterator();
        return Stream.generate(() -> null)
                     .takeWhile(x -> iterator.hasNext())
                     .map(n -> iterator.next());
    }
}
