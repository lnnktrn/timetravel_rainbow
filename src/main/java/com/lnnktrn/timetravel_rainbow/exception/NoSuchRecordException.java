package com.lnnktrn.timetravel_rainbow.exception;

public class NoSuchRecordException extends RuntimeException {

    public NoSuchRecordException(Long id) {
        super("Record with id=" + id + " does not exist");
    }

    public NoSuchRecordException(String message) {
        super(message);
    }
}