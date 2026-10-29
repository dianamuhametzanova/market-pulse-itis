
package academy.backend.market_pulse.repository.store.impl;

import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.repository.store.QuoteFileStore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class QuoteFileStoreSkeleton implements QuoteFileStore {

    @Override
    public void save(Path path, List<Quote> quotes) {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();

            for (Quote quote : quotes) {
                Instrument instrument = quote.getInstrument();

                String line = String.join(";",
                        instrument.getType(),
                        instrument.getTicker(),
                        instrument.getName(),
                        instrument.getCurrency().name(),
                        quote.getPrice().toPlainString(),
                        quote.getChangePercent().toPlainString()
                );

                lines.add(line);
            }

            Files.write(path, lines, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Не удалось сохранить котировки", e);
        }
    }

    @Override
    public List<Quote> load(Path path) {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            List<Quote> quotes = new ArrayList<>();

            for (String line :
                    Files.readAllLines(path, StandardCharsets.UTF_8)) {

                String[] fields = line.split(";", -1);

                if (fields.length != 6) {
                    throw new IllegalArgumentException(
                            "Некорректная запись котировки: " + line);
                }

                String type = fields[0];
                String ticker = fields[1];
                String name = fields[2];
                Currency currency = Currency.valueOf(fields[3]);

                BigDecimal price = new BigDecimal(fields[4]);
                BigDecimal changePercent = new BigDecimal(fields[5]);

                Instrument instrument = InstrumentFactories.create(
                        type, ticker, name, currency);

                quotes.add(new Quote(
                        instrument, price, changePercent));
            }

            return quotes;

        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Не удалось прочитать котировки", e);
        }
    }
}
