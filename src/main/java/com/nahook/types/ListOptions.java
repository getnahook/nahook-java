package com.nahook.types;

public class ListOptions {
    private final Integer limit;
    private final Integer offset;

    public ListOptions(Integer limit, Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public Integer getLimit() { return limit; }
    public Integer getOffset() { return offset; }
}
