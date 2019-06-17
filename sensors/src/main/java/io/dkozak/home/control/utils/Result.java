package io.dkozak.home.control.utils;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Result<Response, Error> {
    public final Response data;
    public final Error error;


    public static <Response> Result<Response, Void> success(Response response) {
        return new Result<>(response, null);
    }

    public static <Error> Result<Void, Error> error(Error error) {
        return new Result<>(null, error);
    }
}
