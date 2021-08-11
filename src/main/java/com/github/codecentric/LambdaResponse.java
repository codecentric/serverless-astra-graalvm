package com.github.codecentric;

import java.util.Objects;

public class LambdaResponse {

  private Order order;

  public LambdaResponse() {}

  public LambdaResponse(Order order) {
    this.order = order;
  }

  @Override
  public String toString() {
    return "LambdaResponse{" + "order=" + order + '}';
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
    return Objects.equals(order, that.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(order);
  }
}
