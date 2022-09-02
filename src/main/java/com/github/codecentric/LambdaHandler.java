package com.github.codecentric;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.hc.core5.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.hc.core5.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.google.gson.Gson;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

public class LambdaHandler implements RequestHandler<APIGatewayV2HTTPEvent, LambdaResponse> {

  private static final Gson mapper = new Gson();

  @Override
  public LambdaResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
    LambdaLogger logger = context.getLogger();

    String astraUrl = System.getenv("ASTRA_URL");
    String astraToken = System.getenv("ASTRA_TOKEN");
    String astraNamespace = System.getenv("ASTRA_NAMESPACE");
    AstraClient client = new AstraClient(URI.create(astraUrl), astraToken, astraNamespace);

    if (astraUrl.isBlank()) {
      logger.log("Astra url is NOT set.");
    }
    if (astraToken.isBlank()) {
      logger.log("Astra token is NOT set.");
    }
    if (astraNamespace.isBlank()) {
      logger.log("Astra namespace is NOT set.");
    }

    logger.log("input = " + input + ", context = " + context);

    if (input.getRouteKey().startsWith("GET")) {
      String orderIdRaw = input.getPathParameters().get("orderId");
      UUID orderId = UUID.fromString(orderIdRaw);
      Optional<Order> order = client.getOrder(orderId);
      if (order.isEmpty()) {
        return new LambdaResponse(SC_NOT_FOUND);
      }
      return new LambdaResponse(mapper.toJson(order.get()), SC_OK);

    } else {
      Order requestOrder = null;
      try {
        byte[] decodedRequest = base64DecodeApiGatewayEvent(input);
        requestOrder = mapper.fromJson(new String(decodedRequest), Order.class);
        logger.log("Saving received order: " + requestOrder);
        Order savedOrder = client.saveOrder(requestOrder);
        LambdaResponse lambdaResponse = new LambdaResponse(mapper.toJson(savedOrder), SC_OK);
        logger.log("Successfully saved order with id: " + savedOrder.getOrderId());
        return lambdaResponse;
      } catch (Exception e) {
        logger.log(
            "Could not save input '"
                + input
                + "' as order '"
                + requestOrder
                + "' at "
                + astraUrl
                + " with namespace "
                + astraNamespace);
        logger.log("Exception was: " + e.getLocalizedMessage());
        return new LambdaResponse(
            "{ \"message\": \"Order could not be saved.\" }",
            SC_BAD_REQUEST);
      }
    }
  }

  private byte[] base64DecodeApiGatewayEvent(APIGatewayV2HTTPEvent input) {
    byte[] decodedRequest;
    if (input.getIsBase64Encoded()) {
      decodedRequest = Base64.decodeBase64(input.getBody());
    } else {
      String body = input.getBody();
      decodedRequest = body != null ? body.getBytes(UTF_8) : null;
    }
    return decodedRequest;
  }
}
