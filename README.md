# Java Stock Trading System

Java 21 と Maven を使用する日本株の模擬取引コンソールアプリです。銘柄コード・売買区分・株数を検証し、日本時間の平日の取引時間内ならCSVへ記録します。履歴は期間を選択し、新しい日時から順に罫線付きの表で表示できます。

## 必要なもの

- JDK 21
- IntelliJ IDEA（IDE で開発する場合）

Maven Wrapper を同梱しているため、Maven の個別インストールは不要です。初回ビルド時には Maven とビルド用プラグインのダウンロードにインターネット接続が必要です。

## IntelliJ IDEA で開く

1. IntelliJ IDEA の **Open** から、このディレクトリの `pom.xml` を選び、プロジェクトとして開きます。
2. **File → Project Structure → Project SDK** で JDK 21 を選択します。未インストールの場合は **Download JDK** からバージョン 21 を追加できます。
3. Maven の設定で **Maven home path** を **Use Maven wrapper** に設定し、Maven の同期を実行します。Maven の実行に使う JDK も Project SDK（21）を選択してください。
4. `src/main/java/com/example/trading/Main.java` を開き、`main` メソッド横の実行ボタンで起動します。
5. 実行設定の **Working directory** はプロジェクトのルートにしてください。CSVはこの作業ディレクトリに作成されます。

コンソールに `日本株模擬取引システム` とメニューが表示されれば起動成功です。

## コマンドでビルド・実行する

JDK 21 をインストールし、`JAVA_HOME` をそのディレクトリに設定してください。以下はプロジェクトのルートで実行します。

```sh
./mvnw clean verify
./mvnw compile exec:java
```

Windows の PowerShell またはコマンドプロンプトでは、`./mvnw` を `./mvnw.cmd` に置き換えてください。

ビルドした JAR ファイルも直接実行できます。

```sh
java -jar target/java-stock-trading-system-1.0-SNAPSHOT.jar
```

## 操作と保存先

- `1`: 取引。銘柄コード、`BUY` または `SELL`、1以上の整数の株数を順に入力します。不正な値は同じ項目を再入力できます。
- `2`: 履歴。`all`、`today`、`yesterday`のいずれかを入力します。
- `0`: 終了。入力の終端（EOF）でも終了できます。

取引時間はAsia/Tokyoの月〜金、09:00:00〜11:30:00と12:30:00〜15:30:00です。開始・終了ちょうどを含み、土日と時間外は記録しません。祝日は考慮しません。昨日は前営業日ではなく前暦日です。

取引が成立すると、作業ディレクトリの`src/main/java/com/example/trading/csv/trade_history.csv`にUTF-8で追記します。初回に次のヘッダーを作成します。履歴の表示でCSVを並べ替えることはありません。

```csv
tradeDateTime,stockCode,side,quantity
2026-10-07 09:15:32,7203,BUY,100
```

株価・資産・保有株・実在銘柄の確認は扱いません。SELLも保有株にかかわらず記録できます。アプリの実行時には外部APIやネットワークを使用しません。

## テストと設計

`./mvnw clean verify`で、コンパイル、JUnitの全テスト、実行可能JARの生成を行います。固定時計と一時ファイルで、正常系・異常系、曜日・時刻境界、CSVの往復、履歴の絞り込み・並び順、入力の再試行、再起動後の履歴復元を確認します。

- [要件整理・クラス設計](docs/design.md)
- [クラスの役割と処理の学習ガイド](docs/learning-guide.md)
- [全20要件の最終確認](docs/verification.md)

このクラウド環境では、コマンド実行前に以下で用意済みのJDK・Maven・通信設定を有効にしてください。手元のIntelliJでは上記のJDK 21とMaven Wrapper設定を使います。

```sh
source /workspace/.cloud-setup/java-stock-trading-system/activate.sh
cd /workspace/java-stock-trading-system
./mvnw clean verify
```
