# Serverless Astra GraalVM

## Build

### Prerequisites

- GraalVM (if you have [SdkMan!](https://sdkman.io/usage), execute `sdk env` or set `sdkman_auto_env=true` in `~/.sdkman/etc/config`)
- GraalVM Native Image (`gu install native-image`)

### Package

```bash
./mvnw package
```

## Deployment

### Prerequisites

- [gotf](https://github.com/craftypath/gotf)
- Packaged artifacts

### Initialization

```bash
gotf -m tf login
echo 'astra_client_token = "AstraCS:ZZZ' > tf/secrets.auto.tfvars
echo 'astra_db_client_id = "XXX"' >> tf/secrets.auto.tfvars
echo 'astra_db_client_secret = "YYY"' >> tf/secrets.auto.tfvars
echo 'astra_db_client_token = "AstraCS:ZZZ"' >> tf/secrets.auto.tfvars
gotf -m tf init
```

### Apply

```bash
gotf -m tf apply
```

### Destroy

```bash
gotf -m tf destroy
```