# タスク管理API（Task Manager API）

Spring Boot 3系で構築したタスク管理REST APIです。JWTによるステートレス認証を実装し、ユーザーごとにタスクを管理できます。日本企業への就職活動用ポートフォリオとして開発しました。

## 概要

- ユーザー登録・ログイン後、JWTトークンを使って自分のタスクのみ操作できるTODO管理API
- レイヤードアーキテクチャ（Controller / Service / Repository）を採用
- Docker Composeで、MySQLを含めてワンコマンドで起動可能
- Swagger UIでAPIを可視化・実行可能

## 技術スタック

| カテゴリ | 技術 |
|---|---|
| 言語 / フレームワーク | Java 17, Spring Boot 4.1.0 |
| 認証 | Spring Security, JWT (jjwt 0.12.6), BCrypt |
| データベース | MySQL 8.0, Spring Data JPA (Hibernate) |
| テスト | JUnit 5, Mockito |
| API文書 | springdoc-openapi (Swagger UI) |
| インフラ | Docker, Docker Compose |
| ビルドツール | Maven |

## 機能一覧

### 認証
- ユーザー登録（パスワードはBCryptでハッシュ化して保存）
- ログイン（JWTトークンを発行）

### タスク管理（要認証）
- タスク一覧取得（ログイン中のユーザーが所有するタスクのみ）
- タスク作成
- タスク更新
- タスク削除
- タスクの属性：タイトル、説明、完了フラグ、優先度（LOW/MEDIUM/HIGH）、期限日

### セキュリティ設計
- JWTによるステートレス認証（セッションを保持しない設計）
- ユーザーは自分が作成したタスクにのみアクセス可能（他ユーザーのタスクへの操作は404を返却し、存在の有無を漏洩しない）
- 統一エラーハンドリング（`@RestControllerAdvice`によるJSON形式のエラーレスポンス）

## 起動方法

### 前提条件
- Docker / Docker Composeがインストールされていること

### 手順

```bash
git clone <このリポジトリのURL>
cd task-manager
cp .env.example .env   # 任意のDBパスワード・JWTシークレットを設定（省略時は開発用デフォルト値を使用）
docker-compose up --build
```

起動後、以下のURLでアクセス可能です。

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 動作確認例

```bash
# ユーザー登録
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# ログイン（JWTトークンを取得）
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

## APIエンドポイント一覧

詳細なリクエスト/レスポンス仕様はSwagger UI（`/swagger-ui.html`）を参照してください。

| メソッド | エンドポイント | 説明 | 認証 |
|---|---|---|---|
| POST | `/api/auth/register` | ユーザー登録 | 不要 |
| POST | `/api/auth/login` | ログイン（JWT発行） | 不要 |
| GET | `/api/tasks` | 自分のタスク一覧取得 | 必要 |
| POST | `/api/tasks` | タスク作成 | 必要 |
| PUT | `/api/tasks/{id}` | タスク更新 | 必要 |
| DELETE | `/api/tasks/{id}` | タスク削除 | 必要 |

認証が必要なエンドポイントには、リクエストヘッダーに以下を付与してください。

```
Authorization: Bearer <JWTトークン>
```

## テスト

Service層のビジネスロジックに対してJUnit 5 + Mockitoによる単体テストを実装しています。

```bash
./mvnw test
```

- タスク作成の正常系
- 存在しないタスクへのアクセス（404エラー）
- 他ユーザーのタスクへの操作拒否（404エラー、所有権の検証）

## 工夫した点・学んだこと

- JWTを使用したステートレス認証を実装し、セキュリティを考慮した設計を意識しました。
- レイヤードアーキテクチャ（Controller / Service / Repository）を採用し、保守性の高いコードを目指しました。
- EntityとDTOを分離することで、APIレスポンスに不要な情報（パスワードハッシュなど）が漏れない設計にしました。
- `findByIdAndOwner`のようなクエリレベルでの所有権チェックにより、TOCTOU（Time-of-check to time-of-use）のリスクを防ぐ設計を意識しました。
- Docker Composeでアプリケーションとデータベースをまとめて管理し、環境構築の手間を最小化しました。
- C++でのバックグラウンドを活かしつつ、Javaのオブジェクト指向設計とSpringのDIコンテナの仕組みを新たに習得しました。

## ディレクトリ構成

```
task-manager/
├── src/main/java/com/example/task_manager/
│   ├── controller/    # REST APIエンドポイント
│   ├── service/       # ビジネスロジック
│   ├── repository/    # データアクセス層（Spring Data JPA）
│   ├── entity/         # DBエンティティ
│   ├── dto/            # リクエスト/レスポンス用DTO
│   ├── security/      # JWT認証関連
│   ├── exception/      # 統一例外ハンドリング
│   └── config/         # Swagger等の設定
├── src/test/java/      # 単体テスト
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```
