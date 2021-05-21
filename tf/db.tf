resource "astra_database" "main" {
  name           = "serverless-astra-graalvm"
  keyspace       = "main"
  cloud_provider = "gcp"
  region         = "europe-west1"
}