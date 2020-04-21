# 家計簿アプリ(kakeibooo)

## コマンド

### flyway

flywayフォルダへ移動
```sbtshell
cd ./flyway
sbt flywayMigrate
```

cleanする場合
```sbtshell
sbt flywayClean
```

=> [README](../kakeiboo-infra/flyway/README.md)

### docker

初回実行の際の注意

=> [README](../kakeiboo-infra/docker/README.md)

### backend

[Scalafmt](https://scalameta.org/scalafmt/)を実行
```sbtshell
cd ./backend
sbt scalafmt
```
