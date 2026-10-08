# コードを読むための学習ガイド

## 1. フォルダーとクラスの構成

```text
src/main/java/com/example/trading/
├── Main.java
├── model/       Trade.java, TradeType.java, HistoryPeriod.java
├── validator/   StockCodeValidator.java, QuantityValidator.java
├── service/     TradingService.java, TradingHoursService.java, TradeHistoryService.java
├── repository/  TradeHistoryRepository.java
└── ui/          ConsoleApplication.java, TradeHistoryTablePrinter.java
src/test/java/com/example/trading/  各パッケージに対応するJUnitテスト
pom.xml                           Javaのバージョン・テスト依存関係・ビルド設定
```

Mavenは`src/main/java`をアプリのコード、`src/test/java`をテストとして扱います。生成されたクラスと実行可能JARは`target`に入ります。

## 2. 各クラスの責務

| クラス | 担当すること |
| --- | --- |
| Main | オブジェクトを組み立てて起動する |
| ConsoleApplication | メニュー・入力・再入力・結果の案内 |
| TradeHistoryTablePrinter | Listを罫線付きの表にする |
| Trade | 1件の取引日時・銘柄コード・売買区分・株数を保持する |
| TradeType | BUYとSELLを定義し、入力を変換する |
| HistoryPeriod | ALL、TODAY、YESTERDAYを定義し、入力を変換する |
| StockCodeValidator | 4文字と各位置の許可文字を検証する |
| QuantityValidator | 株数の文字列を正の整数へ変換する |
| TradingHoursService | 土日と前場・後場の時間を判定する |
| TradingService | 検証・日時取得・条件判定・保存を順に実行する |
| TradeHistoryRepository | CSV形式の検証とファイルの読み書き |
| TradeHistoryService | 履歴の期間による抽出と降順ソート |

## 3. Mainから取引成立まで

`Main`の`new`は、クラスをもとに実際のオブジェクトを作ります。例えば`new TradingService(repository, tradingHoursService, clock)`は、コンストラクタを使って取引処理に必要な保存先・時間判定・時計を渡します。

`ConsoleApplication.run()`がメニューを表示します。取引を選ぶと銘柄コード、売買区分、株数を順に読み、不正な項目だけ再入力させます。その後`TradingService.execute()`が同じvalidatorを使って再検証します。これは将来コンソール以外から呼ぶ場合も条件を守るためです。

サービスは時計を一度読み、`LocalDateTime.ofInstant(clock.instant(), JAPAN_ZONE)`で日本時間にします。`TradingHoursService`が`LocalDate`と`DayOfWeek`で土日を確認し、`LocalTime`で取引時間を確認します。すべての条件を満たした場合だけ`Trade`を作って保存します。

## 4. CSVへの保存

`TradingService`は`repository.append(trade)`を呼びます。リポジトリは既存CSVが正常か確認し、新規または空ファイルならヘッダーを書き、その後に4項目の取引行を追記します。`Path`は保存先、`Files.writeString`はファイルへの書き込みを表します。

保存に成功してサービスから戻った後に、コンソールが成立を表示します。書き込みに失敗すると`IOException`がUIまで伝わり、成立とは表示せず理由を案内します。

## 5. CSVの読み込みと期間の抽出

`findAll()`はUTF-8のCSVを1行ずつ読みます。ヘッダー、列数、日時、コード、売買区分、株数を検証し、各行を`Trade`に変換して`ArrayList`へ追加します。不正な行は行番号付きのエラーになり、勝手に削除しません。

`TradeHistoryService.findByPeriod()`は日本時間の今日を`LocalDate.now(clock.withZone(JAPAN_ZONE))`で取得します。ALLは全件、TODAYは取引日が今日に等しいもの、YESTERDAYは`today.minusDays(1)`に等しいものを新しいListへ入れます。日付を扱うので月末・年末もJava標準APIが処理します。

## 6. 日時の降順

```java
result.sort(Comparator.comparing(Trade::getTradeDateTime).reversed());
```

`Comparator.comparing`は取引日時を比較する規則を作り、`reversed()`で新しいものからの順序にします。`Trade::getTradeDateTime`は「比較する値をこのメソッドで取得する」という指定です。同じ日時はCSVにあった順序を保ちます。表示用Listだけを並べ替えるため、CSVの成立順は変わりません。

## 7. enumを使う理由

`TradeType`と`HistoryPeriod`は、選択肢を決まった値に限定します。単なる文字列を処理のあちこちで比較するより、間違いに気付きやすくなります。文字列からenumへの変換は`fromInput()`に集め、許可されない値はそこで拒否します。`switch`で期間ごとの処理を読みやすく分けられます。

## 8. バリデーションを分ける理由

MainやUIへ検証条件を直接書くと、画面と業務処理で条件がずれることがあります。専用クラスを両方から呼ぶことで条件を1か所に保ち、個別のテストもできます。validatorは状態を持たないため、`static`メソッドをクラス名で呼びます。

## 9. serviceとrepositoryの違い

serviceは「いつ取引できるか」「どの履歴を表示するか」という処理の判断を担当します。repositoryは「CSVへどう書き、どう読むか」を担当します。サービスはカンマ区切りやヘッダーを扱わず、リポジトリは現在の取引時間を判定しません。

## 10. 初心者として押さえるコード

- `class`はデータや処理の定義、objectは`new`で作った実体です。constructorで必要な相手を渡し、methodを呼んで処理を進めます。
- `private`はクラス内部だけで使うもの、`public`は他クラスから使う入口です。`Trade`の`private final`フィールドとgetterは、生成後の取引データを変更させずに読み出すための構成です。
- `static`はオブジェクトを作らずに呼べる処理や共有定数を表します。取引情報を持つ`Trade`と、状態を持たないvalidatorの違いを確認してください。
- `LocalDate`は日付、`LocalTime`は時刻、`LocalDateTime`は日付と時刻です。それ自体にタイムゾーンはないため、時計の瞬間を`ZoneId.of("Asia/Tokyo")`で変換します。
- `Clock`をコンストラクタで渡すと、実行時は現在の時計、テストでは`Clock.fixed`を使えます。テストは実際の曜日・時刻に依存しません。
- 株数には`BigInteger`を使い、仕様にない`int`の上限を設けていません。`signum()`で正の値か確認します。
- `List<Trade>`は取引の一覧という型、`ArrayList`はその具体的な実装です。`for`で順に読み、条件を満たす要素を`add`します。
- `try` / `catch`は例外への対応です。不正入力は`IllegalArgumentException`、ファイルの問題は`IOException`としてUIへ伝え、案内と再入力・メニューへの復帰につなげます。
- `try (...)`は読み込み処理が終わった時にファイルを確実に閉じます。日時のCSV解析には厳密なformatterを使い、存在しない日付を自動補正しません。

読む順序は`Main`→`ConsoleApplication`→validatorとenum→`TradingService`→`TradingHoursService`→repository→履歴サービス→表表示です。対応するテストを一緒に読むと、正常・異常・境界の期待動作を確認できます。
