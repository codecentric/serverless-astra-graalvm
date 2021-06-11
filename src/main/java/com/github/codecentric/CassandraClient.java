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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraClient {

  private final URI astraUrl;
  private final String astraToken;
  private final String astraNamespace;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Logger log = LoggerFactory.getLogger(CassandraClient.class);

  public CassandraClient(URI astraUrl, String astraToken, String astraNamespace) {
    this.astraUrl = astraUrl;
    this.astraToken = astraToken;
    this.astraNamespace = astraNamespace;
  }

  public Optional<Order> getOrder(UUID orderId) {
    try {
      URI getOrderUri =
          new URIBuilder()
              .setScheme(astraUrl.getScheme())
              .setHost(astraUrl.getHost())
              .setPort(astraUrl.getPort())
              .setPathSegments(
                  "v2", "namespaces", astraNamespace, "collections", "orders", orderId.toString())
              .build();
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

  public UUID saveOrder(Order order) throws IOException {
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
    return UUID.fromString(saveResult.get("documentId"));
  }

  public void ensureNamespaceExists() throws IOException {
    Response getNamespaceResponse =
        Request.get(String.format("%s/v2/schemas/namespaces/%s", astraUrl, astraNamespace))
            .addHeader("X-Cassandra-Token", astraToken)
            .execute();

    if (getNamespaceResponse.returnResponse().getCode() != 200) {
      log.info("Namespace {} did not exist, creating...", astraNamespace);
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
        log.error(
            "Creation of namespace {} failed with error code {}.",
            astraNamespace,
            creationReturnCode);
      } else {
        log.info("Namespace {} successfully created.", astraNamespace);
      }
    }
  }
}
