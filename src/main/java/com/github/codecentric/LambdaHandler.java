package com.github.codecentric;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

public class LambdaHandler implements RequestHandler<APIGatewayV2HTTPEvent, LambdaResponse> {

  private static final Gson mapper = new Gson();

  @Override
  public LambdaResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
    String astraUrl = System.getenv("ASTRA_URL");
    String astraToken = System.getenv("ASTRA_TOKEN");
    String astraNamespace = System.getenv("ASTRA_NAMESPACE");
    CassandraClient client = new CassandraClient(URI.create(astraUrl), astraToken, astraNamespace);

    if (astraUrl.isBlank()) System.out.println("Astra url is NOT set.");
    if (astraToken.isBlank()) System.out.println("Astra token is NOT set.");
    if (astraNamespace.isBlank()) System.out.println("Astra namespace is NOT set.");

    System.out.println("input = " + input + ", context = " + context);

    byte[] decodedRequest = base64DecodeApiGatewayEvent(input);

    if (input.getRouteKey().startsWith("GET")) {
      String orderIdRaw = input.getPathParameters().get("orderId");
      UUID orderId = UUID.fromString(orderIdRaw);
      Optional<Order> order = client.getOrder(orderId);
      if (order.isEmpty()) {
        return new LambdaResponse();
      }
      return new LambdaResponse(order.get());

    } else {
      Order requestOrder = null;
      try {
        requestOrder = mapper.fromJson(Arrays.toString(decodedRequest), Order.class);
        System.out.println("Received order: " + requestOrder);
        Order savedOrder = client.saveOrder(requestOrder);
        LambdaResponse lambdaResponse = new LambdaResponse(savedOrder);
        System.out.println("Lambda response: " + lambdaResponse);
        return lambdaResponse;
      } catch (IOException e) {
        throw new RuntimeException(
            "Could not save input '"
                + input
                + "' as order '"
                + requestOrder
                + "' at "
                + astraUrl
                + " with namespace "
                + astraNamespace,
            e);
      }
    }
  }

  private byte[] base64DecodeApiGatewayEvent(APIGatewayV2HTTPEvent input) {
    byte[] decodedRequest;
    if (input.getIsBase64Encoded()) {
      decodedRequest = Base64.decodeBase64(input.getBody());
    } else {
      String body = input.getBody();
      decodedRequest = body != null ? body.getBytes(StandardCharsets.UTF_8) : null;
    }
    return decodedRequest;
  }
}
