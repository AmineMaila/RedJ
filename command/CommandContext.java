package command;

import store.DataStore;

public class CommandContext {
    private final DataStore store;

    public CommandContext(DataStore store) {
        this.store = store;
    }

    public DataStore store() {
        return store;
    }
}
