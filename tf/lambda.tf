module "lambda_function" {
  source = "terraform-aws-modules/lambda/aws"
  version = "2.1.0"

  function_name = var.project_name
  handler = "com.github.codecentric.LambdaHandler::handleRequest"
  runtime = "provided"

  create_package = false
  local_existing_package = "../target/serverless-astra-graalvm-${local.git-short-sha}.zip"

  tags = {
    Name = var.project_name
    Version = local.git-short-sha
  }
}