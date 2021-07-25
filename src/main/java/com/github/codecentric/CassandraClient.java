package com.github.codecentric;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.net.URIBuilder;

public class CassandraClient {

  private final URI astraUrl;
  private final String astraToken;
  private final String astraNamespace;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public CassandraClient(URI astraUrl, String astraToken, String astraNamespace) {
    this.astraUrl = astraUrl;
    this.astraToken = astraToken;
    this.astraNamespace = astraNamespace;
  }

  public Optional<Order> getOrder(UUID orderId) {
    try {
      URI getOrderUri =
          new URIBuilder(astraUrl)
              .appendPathSegments(
                  "v2", "namespaces", astraNamespace, "collections", "orders", orderId.toString())
              .build();
      System.out.println("Requesting order from Astra at " + getOrderUri);
      Response response =
          Request.get(getOrderUri).addHeader("X-Cassandra-Token", astraToken).execute();
      CassandraOrder cassandraOrder =
          objectMapper.readValue(response.returnContent().asBytes(), CassandraOrder.class);
      Order resultOrder = cassandraOrder.getData();
      resultOrder.setOrderId(cassandraOrder.getDocumentId());
      return Optional.of(cassandraOrder.getData());
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public Order saveOrder(Order order) throws IOException {
    Response response =
        Request.post(
                String.format("%s/v2/namespaces/%s/collections/orders", astraUrl, astraNamespace))
            .addHeader("X-Cassandra-Token", astraToken)
            .body(
                HttpEntities.create(
                    objectMapper.writeValueAsString(order), ContentType.APPLICATION_JSON))
            .execute();

    Map<String, String> saveResult =
        objectMapper.readValue(response.returnContent().asBytes(), new TypeReference<>() {});
    UUID orderId = UUID.fromString(saveResult.get("documentId"));
    order.setOrderId(orderId);
    return order;
  }

  public void ensureNamespaceExists() throws IOException {
    Response getNamespaceResponse =
        Request.get(String.format("%s/v2/schemas/namespaces/%s", astraUrl, astraNamespace))
            .addHeader("X-Cassandra-Token", astraToken)
            .execute();

    if (getNamespaceResponse.returnResponse().getCode() != 200) {
      System.out.printf("Namespace '%s' does not exist, creating...%n", astraNamespace);
      Response createResponse =
          Request.post(String.format("%s/v2/schemas/namespaces", astraUrl))
              .body(
                  HttpEntities.create(
                      String.format("{\"name\":\"%s\"}", astraNamespace),
                      ContentType.APPLICATION_JSON))
              .addHeader("X-Cassandra-Token", astraToken)
              .execute();
      int creationReturnCode = createResponse.returnResponse().getCode();
      if (creationReturnCode != 201) {
        System.out.printf(
            "Creation of namespace '%s' failed with error code %s.%n",
            astraNamespace, creationReturnCode);
      } else {
        System.out.printf("Namespace '%s' successfully created.%n", astraNamespace);
      }
    }
  }
}
