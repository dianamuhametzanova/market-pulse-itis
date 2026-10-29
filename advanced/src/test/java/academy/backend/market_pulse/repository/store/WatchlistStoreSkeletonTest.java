
package academy.backend.market_pulse.repository.store;

import academy.backend.market_pulse.repository.store.impl.WatchlistStoreSkeleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WatchlistStoreSkeletonTest
        extends WatchlistStoreContractTest {

    @Override
    protected WatchlistStore createStore() {
        return new WatchlistStoreSkeleton();
    }

    @Test
    void preservesInsertionOrder(@TempDir Path tempDir) {
        WatchlistStore store = createStore();
        Path path = tempDir.resolve("watchlist.txt");

        Set<String> tickers = new LinkedHashSet<>(
                List.of("SBER", "GAZP", "AAPL")
        );

        store.save(path, tickers);
        Set<String> loaded = store.load(path);

        assertEquals(
                new ArrayList<>(tickers),
                new ArrayList<>(loaded)
        );
    }

    @Test
    void missingFileReturnsEmptySet(@TempDir Path tempDir) {
        WatchlistStore store = createStore();
        Path path = tempDir.resolve("missing.txt");

        assertTrue(store.load(path).isEmpty());
    }
}
