package com.github.codecentric;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.Gson;
import java.net.URI;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

@SuppressWarnings("rawtypes")
public class AstraTestExtension implements BeforeEachCallback {

  private final Gson mapper = new Gson();
  private final GenericContainer stargate;
  private final String namespace;
  private volatile AstraClient client;
  private volatile URI astraUri;
  private volatile String authToken;

  public AstraTestExtension(GenericContainer stargate, String namespace) {
    this.stargate = stargate;
    this.namespace = namespace;
  }

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    astraUri = new URIBuilder()
        .setScheme("http")
        .setHost(stargate.getContainerIpAddress())
        .setPort(stargate.getMappedPort(8082))
        .build();
    authToken = generateAuthToken();
    client = new AstraClient(astraUri, authToken, namespace);
    ensureNamespaceExists();
  }

  public void ensureNamespaceExists() throws Exception {
    URI uri = new URIBuilder(astraUri).setPathSegments("v2", "schemas", "namespaces").build();

    Request.post(uri)
        .body(
            HttpEntities.create(
                String.format("{\"name\":\"%s\"}", namespace), ContentType.APPLICATION_JSON))
        .addHeader("X-Cassandra-Token", authToken)
        .execute();
  }

  public AstraClient getClient() {
    return client;
  }

  public String generateAuthToken() throws Exception {
    URI uri = new URIBuilder()
        .setScheme("http")
        .setHost(stargate.getContainerIpAddress())
        .setPort(stargate.getMappedPort(8081))
        .setPathSegments("v1", "auth")
        .build();

    Response response = Request.post(uri)
        .body(
            HttpEntities.create(
                "{\"username\":\"cassandra\", \"password\":\"cassandra\"}",
                ContentType.APPLICATION_JSON))
        .execute();
    AuthToken authResult =
        mapper.fromJson(response.returnContent().asString(UTF_8), AuthToken.class);
    return authResult.authToken;
  }
}
