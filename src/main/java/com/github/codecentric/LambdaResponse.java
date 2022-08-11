package com.github.codecentric;

import java.util.Objects;

public class LambdaResponse {

  private String body;
  private int statusCode;
  private final boolean isBase64Encoded = false;

  public LambdaResponse() {
  }

  public LambdaResponse(int statusCode) {
    this.statusCode = statusCode;
  }

  public LambdaResponse(String body, int statusCode) {
    this.body = body;
    this.statusCode = statusCode;
  }

  @Override
  public String toString() {
    return "LambdaResponse{" +
        "body='" + body + '\'' +
        ", statusCode=" + statusCode +
        ", isBase64Encoded=" + isBase64Encoded +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LambdaResponse that = (LambdaResponse) o;
    return statusCode == that.statusCode && isBase64Encoded == that.isBase64Encoded
        && Objects.equals(body, that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(body, statusCode, isBase64Encoded);
  }
}
