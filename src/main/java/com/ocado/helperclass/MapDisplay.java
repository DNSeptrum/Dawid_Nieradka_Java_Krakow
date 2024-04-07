package com.ocado.helperclass;

import java.util.List;
import java.util.Map;

public class MapDisplay {
    public static void displayMap(Map<String, List<String>> deliveryMap) {
        if (deliveryMap == null || deliveryMap.isEmpty()) {
            System.out.println("No results.");
            return;
        }

        for (Map.Entry<String, List<String>> entry : deliveryMap.entrySet()) {
            System.out.println("Delivery method: " + entry.getKey());
            System.out.println("Products:");
            for (String item : entry.getValue()) {
                System.out.println(" - " + item);
            }
            System.out.println();
        }
    }
}
