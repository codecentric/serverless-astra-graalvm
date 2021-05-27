package com.github.codecentric;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaHandler implements RequestHandler<LambdaRequest, LambdaResponse> {

  @Override
  public LambdaResponse handleRequest(LambdaRequest input, Context context) {
    LambdaResponse response = new LambdaResponse();
    response.statusCode = 200;
    return response;
  }
}