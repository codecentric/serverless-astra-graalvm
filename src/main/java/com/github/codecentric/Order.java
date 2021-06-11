package com.github.codecentric;

import java.util.Objects;
import java.util.UUID;

public class Order {

  private UUID orderId;
  private String productName;
  private Integer productQuantity;
  private Integer productPrice;

  public Order() {}

  public UUID getOrderId() {
    return orderId;
  }

  public void setOrderId(UUID orderId) {
    this.orderId = orderId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Integer getProductQuantity() {
    return productQuantity;
  }

  public void setProductQuantity(Integer productQuantity) {
    this.productQuantity = productQuantity;
  }

  public Integer getProductPrice() {
    return productPrice;
  }

  public void setProductPrice(Integer productPrice) {
    this.productPrice = productPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
    return Objects.equals(orderId, order.orderId)
        && Objects.equals(productName, order.productName)
        && Objects.equals(productQuantity, order.productQuantity)
        && Objects.equals(productPrice, order.productPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderId, productName, productQuantity, productPrice);
  }

  @Override
  public String toString() {
    return "Order{"
        + "orderId="
        + orderId
        + ", productName='"
        + productName
        + '\''
        + ", productQuantity="
        + productQuantity
        + ", productPrice="
        + productPrice
        + '}';
  }
}
