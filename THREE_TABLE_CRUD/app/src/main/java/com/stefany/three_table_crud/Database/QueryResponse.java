package com.stefany.three_table_crud.Database;

public interface QueryResponse<T> {
    void onSuccess(T data);
    void onFailure(String message);
}
