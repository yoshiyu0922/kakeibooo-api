# Kakeibooo（API）

## note

Kakeiboooは3つの環境で構成している

- [kakeibooo-api](https://github.com/yoshiyu0922/kakeibooo-api) : バックエンドアプリ
- [kakeibooo-web](https://github.com/yoshiyu0922/kakeibooo-web) : フロントアプリ
- [kakeibooo-infra](https://github.com/yoshiyu0922/kakeibooo-infra): dockerやDDLを管理

## Environments

* Scala 2.12.8
* PlayFramework 2.7.2
* sbt 1.2.8
* scalikejdbc 3.2
* jwt-play-json 2.1.0
* sangria 1.4.2 (GraphQL library)

## Commands

#### compile

```sbtshell
sbt compile
```

#### run server

```sbtshell
sbt run
```

### create jar

```sbtshell
sbt assembly
```

### create scala-doc

```sbtshell
sbt doc
``

### run [scalafmt](https://scalameta.org/scalafmt/)

```sbtshell
sbt scalafmt
```
