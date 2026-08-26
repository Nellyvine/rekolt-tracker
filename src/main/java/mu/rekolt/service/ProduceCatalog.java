package mu.rekolt.service;

import mu.rekolt.model.*;

import java.util.HashMap;
import java.util.Map;

public class ProduceCatalog {
    private static final String[] CODES = {"MZE", "BNS", "POT", "TEA"};
    private static final String[] NAMES = {"Maize", "Beans", "Potatoes", "Green tea leaf"};
    private static final double[] BASE_PRICES = {30.0, 90.0, 45.0, 25.0};

    private final Map<String, Produce> catalog = new HashMap<>();

    public ProduceCatalog() {
        catalog.put(CODES[0], new CerealProduce(CODES[0], NAMES[0], BASE_PRICES[0]));
        catalog.put(CODES[1], new CerealProduce(CODES[1], NAMES[1], BASE_PRICES[1]));
        catalog.put(CODES[2], new PerishableProduce(CODES[2], NAMES[2], BASE_PRICES[2]));
        catalog.put(CODES[3], new CashCropProduce(CODES[3], NAMES[3], BASE_PRICES[3]));
    }

    public Produce get(String code) {
        Produce produce = catalog.get(code);
        if (produce == null) {
            throw new IllegalArgumentException("Unknown produce code: " + code);
        }
        return produce;
    }

    public static String[] getCodes() {
        return CODES;
    }
}
