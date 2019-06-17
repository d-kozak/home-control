package io.dkozak.home.control.utils;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Pair<Fst, Snd> {
    public final Fst first;
    public final Snd second;


    public static <Fst, Snd> Pair<Fst, Snd> pairOf(Fst first, Snd second) {
        return new Pair<>(first, second);
    }
}
