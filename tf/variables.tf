variable "astra_client_token" {
  description = "Admin client token for Astra. Needs to have permissions to create databases and keyspaces."
}

variable "astra_db_client_id" {
  description = "Database user client ID for Astra. Needs to have permissions to R/W on keyspaces."
}
variable "astra_db_client_secret" {
  description = "Database user client secret for Astra. Needs to have permissions to R/W on keyspaces."
}
variable "astra_db_client_token" {
  description = "Database user client token for Astra. Needs to have permissions to R/W on keyspaces."
}

variable "aws_profile" {
  description = "AWS profile to use for AWS provider. See https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-profiles.html."
}