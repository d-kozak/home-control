package io.dkozak.home.control.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Result<Response, Error> {
    public final Response data;
    public final Error error;
}
