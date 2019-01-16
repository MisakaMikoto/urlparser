package com.misakamikoto.urlparser.parser.dto;

public class Response {

    public Response() {
        this.setResult(false);
    }

    boolean isResult;
    String quotient;
    String remainder;

    public boolean isResult() {
        return isResult;
    }

    public void setResult(boolean result) {
        isResult = result;
    }

    public String getQuotient() {
        return quotient;
    }

    public void setQuotient(String quotient) {
        this.quotient = quotient;
    }

    public String getRemainder() {
        return remainder;
    }

    public void setRemainder(String remainder) {
        this.remainder = remainder;
    }
}
