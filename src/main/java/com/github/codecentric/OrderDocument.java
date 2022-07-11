package com.github.codecentric;

import java.util.UUID;

public class OrderDocument {

  private UUID documentId;
  private Order data;

  public UUID getDocumentId() {
    return documentId;
  }

  public void setDocumentId(UUID documentId) {
    this.documentId = documentId;
  }

  public Order getData() {
    return data;
  }

  public void setData(Order data) {
    this.data = data;
  }
}
