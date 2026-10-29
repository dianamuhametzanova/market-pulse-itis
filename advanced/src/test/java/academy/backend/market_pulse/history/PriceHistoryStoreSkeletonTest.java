package academy.backend.market_pulse.history;

import academy.backend.market_pulse.history.impl.PriceHistoryStoreSkeleton;

class PriceHistoryStoreSkeletonTest
        extends PriceHistoryStoreContractTest {

    @Override
    protected PriceHistoryStoreSkeleton createStore() {
        return new PriceHistoryStoreSkeleton();
    }
}
