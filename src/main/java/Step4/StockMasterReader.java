package Step4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class StockMasterReader {

    private final Path filePath;

    public StockMasterReader(Path filePath) {
        this.filePath = filePath;
    }

    public List<StockMaster> findAll() {

        List<StockMaster> stockMasters =
                new ArrayList<>();

        try {

            List<String> lines =
                    Files.readAllLines(
                            filePath,
                            StandardCharsets.UTF_8
                    );

            if (lines.isEmpty()) {
                return stockMasters;
            }

            List<String> dataLines =
                    lines.subList(1, lines.size());

            for (String line : dataLines) {

                String[] data =
                        line.split(",");

                String symbol =
                        data[0];

                String productName =
                        data[1];

                String exchangeMarket =
                        data[2];

                Long sharesIssued =
                        Long.parseLong(data[3]);

                StockMaster stockMaster =
                        new StockMaster(
                                symbol,
                                productName,
                                exchangeMarket,
                                sharesIssued
                        );

                stockMasters.add(stockMaster);
            }

        } catch (IOException e) {

            return stockMasters;
        }

        return stockMasters;
    }
}