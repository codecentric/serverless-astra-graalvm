resource "aws_apigatewayv2_api" "api-gateway" {
  name = "serverless-graal-http-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "dev-stage" {
  api_id = aws_apigatewayv2_api.api-gateway.id
  name = "$default"
  auto_deploy = true
}

resource "aws_apigatewayv2_integration" "api-gateway" {
  api_id = aws_apigatewayv2_api.api-gateway.id
  integration_uri = module.lambda_function.lambda_function_invoke_arn
  integration_type = "AWS_PROXY"
  integration_method = "POST"
}

resource "aws_apigatewayv2_route" "api-gateway-get" {
  api_id = aws_apigatewayv2_api.api-gateway.id
  route_key = "GET /order/{orderId}"
  target = "integrations/${aws_apigatewayv2_integration.api-gateway.id}"
}

resource "aws_apigatewayv2_route" "api-gateway-post" {
  api_id = aws_apigatewayv2_api.api-gateway.id
  route_key = "POST /order"
  target = "integrations/${aws_apigatewayv2_integration.api-gateway.id}"
}
