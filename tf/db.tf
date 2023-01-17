resource "astra_database" "main" {
  name           = "serverless-astra-graalvm-db"
  keyspace       = "main"
  cloud_provider = "gcp"
  region         = "europe-west1"
}
