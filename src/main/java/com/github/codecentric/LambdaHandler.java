package com.github.codecentric;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;

public class LambdaHandler implements RequestHandler<LambdaRequest, LambdaResponse> {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public LambdaResponse handleRequest(LambdaRequest input, Context context) {
    String astraUrl = System.getenv("ASTRA_URL");
    String astraToken = System.getenv("ASTRA_TOKEN");
    String astraNamespace = System.getenv("ASTRA_NAMESPACE");
    CassandraClient client = new CassandraClient(URI.create(astraUrl), astraToken, astraNamespace);
    try {
      client.saveOrder(input.getOrder());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new LambdaResponse();
  }
}
