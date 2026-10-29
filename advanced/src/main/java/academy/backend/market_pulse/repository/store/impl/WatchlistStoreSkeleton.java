package academy.backend.market_pulse.repository.store.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import academy.backend.market_pulse.repository.store.WatchlistStore;

public class WatchlistStoreSkeleton implements WatchlistStore {

    @Override
    public void save(Path path, Set<String> tickers) {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.write(path, tickers, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Не удалось сохранить watchlist", e);
        }
    }

    @Override
    public Set<String> load(Path path) {
        if (!Files.exists(path)) {
            return new LinkedHashSet<>();
        }

        try {
            return new LinkedHashSet<>(
                    Files.readAllLines(path, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Не удалось прочитать watchlist", e);
        }
    }
}
