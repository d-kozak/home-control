package io.dkozak.home.control.utils;

import java.util.Arrays;
import java.util.List;

public class ListUtils {

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> List<E> listOf(E... elems) {
        return Arrays.asList(elems);
    }
}
