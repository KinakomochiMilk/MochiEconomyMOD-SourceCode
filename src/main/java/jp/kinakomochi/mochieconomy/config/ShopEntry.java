package jp.kinakomochi.mochieconomy.config;

public class ShopEntry {

    private final String id;

    private final String itemRegistryName;

    private final int amount;

    private final long price;

    private final boolean enabled;

    public ShopEntry(
            String id,
            String itemRegistryName,
            int amount,
            long price,
            boolean enabled
    ) {
        this.id = id;
        this.itemRegistryName =
                itemRegistryName;
        this.amount = amount;
        this.price = price;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public String getItemRegistryName() {
        return itemRegistryName;
    }

    public int getAmount() {
        return amount;
    }

    public long getPrice() {
        return price;
    }

    public boolean isEnabled() {
        return enabled;
    }
}