package com.estsoft.finalproject.content.model.dto;

import java.util.Collection;
import org.springframework.http.HttpStatusCode;

public class ResponseDto<T> {
    private final T item;
    private final HttpStatusCode responseCode;
    private final String message;

    public ResponseDto(T item, HttpStatusCode responseCode, String message) {
        this.item = item;
        this.responseCode = responseCode;
        this.message = message;
    }

    public T getItem() {
        return this.item;
    }

    public HttpStatusCode getResponseCode() {
        return this.responseCode;
    }

    public String getMessage() {
        return this.message;
    }

    public int getSize() {
        if (item == null) {
            return 0;
        } else if (item instanceof Collection itemlist) {
            return itemlist.size();
        }
        return 1;
    }

    public static <U> Builder<U> builder(U item) {
        return new ResponseDto.Builder<>(item);
    }

    public static class Builder<T> {
        private T item;
        private HttpStatusCode responseCode;
        private String message;

        protected Builder(T item) {
            this.item = item;
        }

        public Builder<T> item(T item) {
            this.item = item;
            return this;
        }

        public Builder<T> responseCode(HttpStatusCode responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ResponseDto<T> build() {
            return new ResponseDto<T>(item, responseCode, message);
        }
    }
}