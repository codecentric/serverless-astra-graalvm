package com.github.codecentric;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.net.URIBuilder;

public class AstraClient {

  private final URI astraUrl;
  private final String astraToken;
  private final String astraNamespace;
  private final Gson mapper = new Gson();

  public AstraClient(URI astraUrl, String astraToken, String astraNamespace) {
    this.astraUrl = astraUrl;
    this.astraToken = astraToken;
    this.astraNamespace = astraNamespace;
  }

  public Optional<Order> getOrder(UUID orderId) {
    try {
      URI uri = new URIBuilder(astraUrl)
          .appendPathSegments("v2", "namespaces", astraNamespace,
              "collections", "orders", orderId.toString())
          .build();
      Response response = Request.get(uri)
          .addHeader("X-Cassandra-Token", astraToken)
          .execute();
      OrderDocument orderDoc = mapper.fromJson(
          response.returnContent().asString(UTF_8), OrderDocument.class);
      Order resultOrder = orderDoc.getData();
      resultOrder.setOrderId(orderDoc.getDocumentId());
      return Optional.of(orderDoc.getData());
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public Order saveOrder(Order order) throws IOException {
    Response response = Request.post(String.format(
            "%s/v2/namespaces/%s/collections/orders", astraUrl, astraNamespace))
        .addHeader("X-Cassandra-Token", astraToken)
        .body(HttpEntities.create(mapper.toJson(order), ContentType.APPLICATION_JSON))
        .execute();

    DocumentId documentId =
        mapper.fromJson(response.returnContent().asString(UTF_8), DocumentId.class);
    order.setOrderId(documentId.documentId);
    return order;
  }
}
