# Serverless Astra GraalVM

## Build

### Prerequisites

- GraalVM (if you have [SdkMan!](https://sdkman.io/usage), execute `sdk env` or set `sdkman_auto_env=true` in `~/.sdkman/etc/config`)
- GraalVM Native Image (`gu install native-image`)

### Package

```bash
./mvnw package
```

Note that for the Lambda deployment to work, you need to build a linux-amd64 native image. At this point in time, cross compilation doesn't seem to work so if you are on Mac or Windows, you might need a VM if you're planning to build this locally.

## Deployment

### Prerequisites

- [gotf](https://github.com/craftypath/gotf)
- Packaged artifacts

### AWS IAM permissions

Add these AWS managed policies to the permissions of your AWS user:

- AWSLambda_FullAccess
- CloudWatchFullAccess
- AmazonAPIGatewayAdministrator
- AmazonSSMFullAccess

Add this inline policy to the permissinos of your AWS user to allow terraform the manipulation of IAM roles and policies.
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "iam:AttachRolePolicy",
                "iam:CreatePolicy",
                "iam:CreateRole",
                "iam:DeletePolicy",
                "iam:DeleteRole",
                "iam:DetachRolePolicy",
                "iam:ListEntitiesForPolicy",
                "iam:ListInstanceProfilesForRole",
                "iam:ListPolicyVersions",
                "iam:TagRole"
            ],
            "Resource": "*"
        }
    ]
}
```

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

## Usage

```
aws lambda invoke --function-name serverless-astra-graalvm --log-type Tail --payload "{}" response.json
```

