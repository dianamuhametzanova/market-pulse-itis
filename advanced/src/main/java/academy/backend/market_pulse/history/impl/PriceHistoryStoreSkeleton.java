
package academy.backend.market_pulse.history.impl;

import academy.backend.market_pulse.history.PriceHistoryStore;
import academy.backend.market_pulse.history.PricePoint;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriceHistoryStoreSkeleton implements PriceHistoryStore {

    @Override
    public Path append(Path directory, PricePoint point) {
        try {
            Files.createDirectories(directory);

            String baseName = point.ticker() + "_"
                    + point.timestamp().toEpochMilli();

            Path file = directory.resolve(baseName + ".txt");
            int counter = 1;

            while (Files.exists(file)) {
                file = directory.resolve(baseName + "_" + counter + ".txt");
                counter++;
            }

            List<String> lines = List.of(
                    point.ticker(),
                    point.timestamp().toString(),
                    point.price().toPlainString()
            );

            Files.write(
                    file,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            );

            return file;

        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Не удалось сохранить точку истории", e);
        }
    }

    @Override
    public List<PricePoint> load(Path directory, String ticker) {
        List<PricePoint> points = new ArrayList<>();

        if (!Files.isDirectory(directory)) {
            return points;
        }

        try (var files = Files.list(directory)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString()
                            .startsWith(ticker + "_"))
                    .forEach(path -> {
                        try {
                            List<String> lines = Files.readAllLines(
                                    path, StandardCharsets.UTF_8);

                            if (lines.size() != 3) {
                                throw new IllegalArgumentException(
                                        "Некорректный файл истории: " + path);
                            }

                            PricePoint point = new PricePoint(
                                    lines.get(0),
                                    Instant.parse(lines.get(1)),
                                    new BigDecimal(lines.get(2))
                            );

                            if (point.ticker().equals(ticker)) {
                                points.add(point);
                            }

                        } catch (IOException e) {
                            throw new UncheckedIOException(
                                    "Не удалось прочитать файл: " + path, e);
                        }
                    });

        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Не удалось прочитать историю цен", e);
        }

        points.sort(Comparator.comparing(PricePoint::timestamp));
        return points;
    }
}
