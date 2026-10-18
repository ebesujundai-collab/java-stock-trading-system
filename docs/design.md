# 要件整理と実装設計

## 要件の確認

仕様全20項目を確認した。要件同士の矛盾はない。以下の範囲で実装し、株価・資産残高・保有株・実在銘柄の照会・祝日カレンダー・外部API・GUIは追加しない。

| 仕様 | 実装する内容 | 主な確認方法 |
| --- | --- | --- |
| 1 銘柄コード | 4文字、1・3文字目はASCII数字、2・4文字目は数字または指定19英大文字 | 指定例、全英字、文字数、空白、全角のテスト |
| 2 売買区分 | `TradeType` enum、正確な `BUY` / `SELL` のみ | 正常・不正入力テスト |
| 3 株数 | 正の整数、100株単位の制限なし | 1、100、0、負数、小数、文字列、非常に大きい整数 |
| 4 保有株 | 確認しない。SELLも同じ条件で記録 | 保有株情報なしでSELL成立 |
| 5 株価 | 扱わない。日時・コード・売買区分・株数の4項目だけ記録 | CSVの列を確認 |
| 6 曜日 | 月〜金を許可、土日を拒否。祝日は無視 | 曜日・平日の祝日テスト |
| 7 時間 | Asia/Tokyo、09:00:00〜11:30:00、12:30:00〜15:30:00、両端を含む | 全指定境界と終了直後のテスト |
| 8 成立条件 | 入力検証→曜日→時刻→CSV保存→成立表示 | 不成立時に保存されないことを確認 |
| 9 CSV | UTF-8、指定ヘッダー、成立順で追記 | 新規・追記・再読み込みテスト |
| 10 履歴 | `all` / `today` / `yesterday`、日本時間の日付 | 東京とUTCの日付が異なる時点で確認 |
| 11 並び順 | 表示するListだけを日時降順にする | ソート結果と元CSVの順序を確認 |
| 12 表 | コンソールだけ罫線付きの表 | 表示内容・行順・空履歴を確認 |
| 13 クラス | 入出力・モデル・検証・サービス・CSVの責務を分離 | 下記の設計、コードレビュー |
| 14 内部品質 | 小さなメソッド、明確な命名、必要最小限のクラス、標準API | レビューと学習用説明 |
| 15 Java/Maven | Java 21、Maven Wrapper、IntelliJから起動可能 | Mavenビルド、実行可能JAR |
| 16 エラー | 不正入力は説明して再入力、ファイルエラーも異常終了させない | コンソール入出力のテスト |
| 17 学習 | 通常のクラス・コンストラクタ・enum・Listなどを明示的に使う | 学習ガイド |
| 18 テスト | 主要な正常系・異常系・境界をJUnitで検証 | Maven Surefire結果 |
| 19 完了条件 | 実装後にビルド・テスト・コンソール起動を実行 | `./mvnw clean verify` とJAR実行 |
| 20 説明 | 構成・責務・処理順・CSV・抽出・ソート・設計理由を説明 | READMEと学習ガイド |

## 境界条件の決定

- 銘柄コードは入力文字列をそのまま検証する。空白を削除したり、小文字を大文字に直したりしない。
- BUY/SELLとall/today/yesterdayはそれぞれ指定された大小文字で受け付ける。
- 株数はJava標準の`BigInteger`を使う。仕様にない`int`の上限を設けず、値が正であることを確認する。
- 取引を判定する際、時計を一度だけ読む。`Asia/Tokyo`に変換した同じ日時で曜日と時刻を確認する。
- 時刻判定はナノ秒まで含む。11:30:00.000000001は11:30:00より後なので不可。判定後、保存日時だけを秒単位にそろえる。
- yesterdayは前営業日ではなく、東京の今日から1日引いた暦日。
- 同じ秒の取引はCSVに記録された順を保つ。日時の降順以外の並び替え条件は追加しない。
- CSVは作業ディレクトリの`../src/main/java/com/example/trading/csv/trading.csv`。存在しなければ履歴は空で、初回成立時にヘッダーと取引を保存する。既存の空ファイルにもヘッダーを一度だけ書く。
- CSVの不正なヘッダー・列・値・日時は、行番号付きの読み込みエラーとして知らせる。勝手に行を削除・無視・上書きしない。
- 保存に失敗したときは成立を通知しない。エラーメッセージを表示してメニューへ戻る。
- ローカルの単一プロセスで使用するコンソールアプリを対象とする。複数プロセス間の同時更新機能は追加しない。
- メニューは「1:取引、2:履歴、0:終了」。入力の終端（EOF）でも安全に終了する。

## クラスの責務

| パッケージ / クラス | 責務 |
| --- | --- |
| `Main` | 時計・保存先・各オブジェクトを組み立ててコンソールを開始 |
| `ui.ConsoleApplication` | メニュー、入力・再入力、メッセージ表示 |
| `ui.TradeHistoryTablePrinter` | 取引のListを罫線付きの表として表示 |
| `model.Trade` | 1件の取引データを変更不可のフィールドとして保持 |
| `model.TradeType` | BUY/SELLとその入力変換 |
| `model.HistoryPeriod` | ALL/TODAY/YESTERDAYとその入力変換 |
| `validator.StockCodeValidator` | 銘柄コードの形式検証 |
| `validator.QuantityValidator` | 文字列から正の株数への変換・検証 |
| `service.TradingHoursService` | 取引曜日・前場・後場の判定 |
| `service.TradingService` | 入力検証、現在日時の取得、条件判定、保存の順序を制御 |
| `service.TradeHistoryService` | 履歴を期間で絞り込み、日時降順のListを作る |
| `repository.TradeHistoryRepository` | CSVの作成・追記・読み込みと形式の検証 |

フレームワーク、DIコンテナ、不要なインターフェースや継承は導入しない。オブジェクトは`Main`で通常の`new`とコンストラクタを使って接続する。日時が必要なサービスは`Clock`を受け取り、本番はシステム時計、テストは固定時計を使う。

## データの流れ

取引: Main → ConsoleApplication（入力・再入力）→ TradingService（再検証・東京の現在日時）→ TradingHoursService（曜日・時間）→ Trade → TradeHistoryRepository（追記）→ 成立表示。

履歴: ConsoleApplication → HistoryPeriod → TradeHistoryService → TradeHistoryRepository（CSVからTradeのList）→ 日付による抽出 → `Comparator.comparing(Trade::getTradeDateTime).reversed()`による降順 → TradeHistoryTablePrinter。

コンソールでの検証は利用者にその場で修正してもらうため、サービスでの検証はコンソール以外から呼ばれても条件を守るために行う。両方が同じバリデーションクラスを呼ぶことで、条件の重複定義を避ける。

## 実装前に決める主要API

- `StockCodeValidator.validate(String)` → 検証済みString、不正時`IllegalArgumentException`
- `QuantityValidator.parse(String)` → `BigInteger`、不正時`IllegalArgumentException`
- `TradeType.fromInput(String)` / `HistoryPeriod.fromInput(String)` → enum、不正時`IllegalArgumentException`
- `Trade(LocalDateTime, String, TradeType, BigInteger)`と各getter
- `TradingHoursService.isTradingDay(LocalDate)` / `isTradingTime(LocalTime)` / `validate(LocalDateTime)`、`JAPAN_ZONE`定数
- `TradeHistoryRepository(Path)`、`append(Trade)` / `findAll()`、入出力エラーは`IOException`
- `TradingService(TradeHistoryRepository, TradingHoursService, Clock)`、`execute(String, String, String)` → 保存済みTrade
- `TradeHistoryService(TradeHistoryRepository, Clock)`、`findByPeriod(HistoryPeriod)` → `List<Trade>`
- `TradeHistoryTablePrinter(PrintWriter)`、`print(List<Trade>)`
- `ConsoleApplication(BufferedReader, PrintWriter, TradingService, TradeHistoryService, TradeHistoryTablePrinter)`、`run()`

エラーを通常の例外で表現し、UIで捕捉して日本語のメッセージにする。テストでは`Clock.fixed`とJUnitの一時ディレクトリを使い、実際のCSVや実際の時刻に依存しない。
