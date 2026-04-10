package com.nahook.types;

import java.util.List;

public class ListResult<T> {
    private final List<T> data;

    public ListResult(List<T> data) {
        this.data = data;
    }

    public List<T> getData() { return data; }
}
