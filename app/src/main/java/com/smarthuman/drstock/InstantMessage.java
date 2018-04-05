package com.smarthuman.drstock;

/**
 * Created by shiyuzhou on 5/4/2018.
 */


class InstantMessage {

    private String message;
    private String author;

    InstantMessage(String message, String author) {
        this.message = message;
        this.author = author;
    }

    public InstantMessage() {



    }

    String getMessage() {
        return message;
    }

    String getAuthor() {
        return author;
    }
}