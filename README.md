# タスク管理API（Task Manager API）

**Spring Boot × JWT認証 × MySQL × Docker で構築した、ユーザーごとのタスク管理REST APIです。**
ユーザー登録・ログインを行い、発行されたトークンを使って「自分のタスクだけ」を作成・閲覧・更新・削除できます。

> 日本でのバックエンドエンジニア（Java）就職に向けたポートフォリオとして、個人で設計・実装しました。

---

## 3行でわかるこのプロジェクト

1. **認証付きのWeb API** — パスワードは暗号化（BCrypt）して保存し、ログイン後はJWTトークンで本人確認します。
2. **データの分離** — 他のユーザーのタスクは見ることも操作することもできません（データベース検索の段階で所有者を絞り込み）。
3. **すぐに動かせる** — Dockerがあれば、コマンド1つでアプリとデータベースが起動し、ブラウザ（Swagger UI）から試せます。

---

## 技術スタック

| カテゴリ | 使用技術 |
|---|---|
| 言語 | Java 17 |
| フレームワーク | Spring Boot 4.1.0（Spring Web MVC / Spring Data JPA / Spring Security） |
| 認証 | JWT（jjwt 0.12.6）、BCryptによるパスワードハッシュ化 |
| データベース | MySQL 8.0、Hibernate（ORM） |
| テスト | JUnit 5、Mockito、AssertJ |
| APIドキュメント | springdoc-openapi 2.8.6（Swagger UI） |
| インフラ | Docker（マルチステージビルド）、Docker Compose |
| ビルド | Maven |

---

## 機能一覧

### 認証
| 機能 | 内容 |
|---|---|
| ユーザー登録 | ユーザー名とパスワードで登録。パスワードはBCryptでハッシュ化して保存し、平文では保持しません。同じユーザー名は登録不可（409）。 |
| ログイン | 認証に成功するとJWTトークンを発行（有効期限：7日間）。失敗時は401を返却。 |
| 入力チェック | ユーザー名・パスワード未入力の場合は400を返却 |

### タスク管理（ログイン必須）
| 機能 | 内容 |
|---|---|
| 一覧取得 | ログイン中のユーザーが所有するタスクのみを返却 |
| 作成 | 作成者が自動的にタスクの所有者になる。`title` は必須、`completed` は省略時 `false`、`priority` は省略時 `MEDIUM` |
| 更新 | リクエストに含めた項目のみ更新（部分更新） |
| 削除 | 自分のタスクのみ削除可能 |

**タスクの項目**

| 項目 | 型 | 説明 |
|---|---|---|
| `id` | Long | 自動採番 |
| `title` | String | タイトル（作成時必須、255文字以内） |
| `description` | String | 説明（任意、255文字以内） |
| `completed` | Boolean | 完了フラグ（初期値 `false`） |
| `priority` | `LOW` / `MEDIUM` / `HIGH` | 優先度（初期値 `MEDIUM`） |
| `dueDate` | LocalDate | 期限日（任意、例：`2026-12-31`） |
| `createdAt` | LocalDateTime | 作成日時（サーバー側で自動設定） |
| `ownerUsername` | String | 所有者のユーザー名（レスポンスのみ） |

### その他
- **Swagger UI**：ブラウザ上でAPI仕様の確認と実行が可能（「Authorize」ボタンからJWTを設定）
- **入力値チェック**：Bean Validationにより、必須項目の未入力・文字数超過・不正なJSON（存在しない優先度、日付形式の誤りなど）は `400 Bad Request` を返却
- **統一エラーレスポンス**：エラー時は `timestamp` / `status` / `error`（入力エラー時は項目ごとの `details`）を含むJSONを返却
- **ヘルスチェック用エンドポイント**：`GET /hello`

---

## APIエンドポイント一覧

詳細なリクエスト／レスポンス仕様は Swagger UI（`http://localhost:8080/swagger-ui.html`）で確認できます。

| メソッド | パス | 説明 | 認証 | 主なレスポンス |
|---|---|---|---|---|
| POST | `/api/auth/register` | ユーザー登録 | 不要 | `201` 登録成功 / `400` 入力エラー / `409` ユーザー名重複 |
| POST | `/api/auth/login` | ログイン（JWT発行） | 不要 | `200` `{"token": "..."}` / `400` 入力エラー / `401` 認証失敗 |
| GET | `/api/tasks` | 自分のタスク一覧 | 必要 | `200` タスクの配列 |
| POST | `/api/tasks` | タスク作成 | 必要 | `201` 作成したタスク / `400` 入力エラー |
| PUT | `/api/tasks/{id}` | タスク更新（部分更新） | 必要 | `200` 更新後のタスク / `400` 入力エラー / `404` 存在しない・他人のタスク |
| DELETE | `/api/tasks/{id}` | タスク削除 | 必要 | `204` 削除成功 / `404` 存在しない・他人のタスク |
| GET | `/hello` | 動作確認 | 必要 | `200` `{"message": "Hello, World!"}` |

- 認証が必要なAPIには、ヘッダー `Authorization: Bearer <JWTトークン>` を付与してください。
- トークンがない・無効・期限切れの場合は `401 Unauthorized` が返ります。

---

## 起動方法

### 前提条件
- Docker Desktop（Docker Compose を含む）

### 手順

```bash
git clone https://github.com/bourbon-love/task-manager.git
cd task-manager
cp .env.example .env    # 任意：DBパスワードとJWTシークレットを設定（省略時は開発用の初期値で起動）
docker-compose up --build
```

ログに `Started TaskManagerApplication` と表示されたら起動完了です。

- API：`http://localhost:8080`
- Swagger UI：`http://localhost:8080/swagger-ui.html`

### 動作確認（curl）

```bash
# 1. ユーザー登録
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# 2. ログイン → レスポンスの token をコピー
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# 3. タスク作成（<TOKEN> を置き換え）
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"title":"面接の準備","priority":"HIGH","dueDate":"2026-12-31"}'

# 4. タスク一覧
curl http://localhost:8080/api/tasks -H "Authorization: Bearer <TOKEN>"
```

### Swagger UIで試す場合
1. `POST /api/auth/register` → `POST /api/auth/login` を実行し、`token` をコピー
2. 画面右上の **Authorize** をクリックし、トークンのみを貼り付け（`Bearer ` は自動付与されるため不要）
3. タスク系APIを実行

### 停止

```bash
docker-compose down       # データを残して停止
docker-compose down -v    # データベースのデータも削除
```

---

## テスト

Service層に対する JUnit 5 + Mockito の単体テストと、アプリケーション起動テストを実装しています。
テスト時はインメモリDB（H2）を使用するため、**MySQLやDockerを起動せずに**実行できます。

```bash
./mvnw test
```

| テスト | 検証内容 |
|---|---|
| `createTask_success` | タスク作成時、タイトル・優先度・所有者が正しくレスポンスに反映される |
| `updateTask_notFound_throws404` | 存在しないIDのタスクを更新しようとすると404エラーになる |
| `deleteTask_crossUser_throws404` | 他ユーザーのタスクを削除しようとすると404エラーになる（所有権チェック） |
| `contextLoads` | アプリケーション全体（DI・Security・JPAの設定）が正しく起動できる |

---

## 設計で工夫した点

- **レイヤードアーキテクチャ**
  Controller（リクエスト受付）／ Service（業務ロジック）／ Repository（DBアクセス）に責務を分け、変更やテストがしやすい構成にしました。
- **ステートレスなJWT認証**
  サーバー側でセッションを持たないため、サーバーを複数台に増やしても認証情報を共有する必要がありません。
- **他人のデータにアクセスさせない設計**
  `findByIdAndOwner` のように「IDと所有者」を同時に条件にして検索するため、確認と操作の間にすき間がありません（TOCTOU対策）。
  また、他人のタスクへのアクセスは「403」ではなく「404」を返し、そのタスクが存在するかどうか自体を推測させません。
- **EntityとDTOの分離**
  レスポンス専用のクラス（`TaskResponse`）を用意し、パスワードハッシュなどの内部情報がAPIから漏れない構造にしました。
- **原因に応じたHTTPステータス**
  入力エラーは `400`、未認証は `401`、存在しない・他人のデータは `404` を返します。
  作成と更新で同じDTOを使いつつ、Validation Groupで「タイトル必須」を作成時のみに適用しました。
- **環境に依存しないテスト**
  テスト時はH2（インメモリDB）に切り替え、`git clone` 直後でも `./mvnw test` がそのまま通る構成にしました。
- **秘密情報を環境変数で管理**
  DBパスワードとJWTシークレットは `.env`（Git管理外）から注入し、ソースコードに本番用の値を書かない運用にしています。
- **Dockerのマルチステージビルドとヘルスチェック**
  実行用イメージにはJREとjarのみを含めて軽量化し、MySQLの準備完了を待ってからアプリを起動するようにしました。

---

## 今後の改善予定

現時点で認識している課題と、次に取り組みたい内容です。

- **タスク1件取得API**：`GET /api/tasks/{id}` の追加
- **テストの拡充**：`AuthService` の単体テスト、Testcontainers を使ったRepository／API結合テスト
- **DBマイグレーション**：`ddl-auto=update` から Flyway によるスキーマ管理へ移行
- **CI**：GitHub Actions によるテスト自動実行

---

## 学んだこと

C++での開発経験をベースに、Spring Bootをゼロから学びながら開発しました。
DIコンテナによる依存関係の管理、JPAによるオブジェクトとテーブルの対応付け、Spring Securityのフィルターチェーンの仕組みなど、
Javaのエコシステムならではの設計思想を、実際に動くものを作りながら理解しました。
特に、Swagger UIでの認証設定やDocker環境でのDB接続など、「動かない原因を一つずつ切り分ける」デバッグの経験が大きな学びになりました。

---

## ディレクトリ構成

```
task-manager/
├── src/main/java/com/example/task_manager/
│   ├── controller/    # REST APIエンドポイント（Auth / Task / Hello）
│   ├── service/       # ビジネスロジック（認証・タスク管理）
│   ├── repository/    # データアクセス層（Spring Data JPA）
│   ├── entity/        # DBエンティティ（User / Task）
│   ├── dto/           # リクエスト／レスポンス用DTO
│   ├── security/      # JWT生成・検証、Spring Security設定
│   ├── exception/     # 統一例外ハンドリング
│   └── config/        # Swagger（OpenAPI）設定
├── src/main/resources/application.properties
├── src/test/java/     # 単体テスト・起動テスト
├── src/test/resources/application.properties  # テスト用設定（H2）
├── Dockerfile
├── docker-compose.yml
├── .env.example       # 環境変数のサンプル
└── pom.xml
```
