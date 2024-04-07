package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BasketSplitter {
    private final Map<String, List<String>> deliveryOptions;
    private Map<String, List<String>> optimalGroups;
    private int minimalNumberOfDeliveries = Integer.MAX_VALUE;

    /**
     * Constructor loads the delivery options from the specified JSON configuration file.
     *
     * @param absolutePathToConfigFile The absolute path to the JSON configuration file.
     * @throws IOException If there is an issue reading the file.
     */
    public BasketSplitter(String absolutePathToConfigFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.deliveryOptions = mapper.readValue(new File(absolutePathToConfigFile), Map.class);
    }

    /**
     * Splits the list of items into optimal delivery groups.
     *
     * @param items List of items to be split.
     * @return A map of delivery methods to the list of items for that delivery method.
     */
    public Map<String, List<String>> split(List<String> items) {
        backtrack(new HashMap<>(), new HashSet<>(items));
        return optimalGroups;
    }

    /**
     * Helper method to perform the backtrack search for optimal delivery groups.
     *
     * @param selectedGroups      The currently selected groups of deliveries.
     * @param remainingProducts   Products that need to be assigned to a delivery group.
     */
    private void backtrack(Map<String, List<String>> selectedGroups, Set<String> remainingProducts) {
        if (remainingProducts.isEmpty()) {
            if (selectedGroups.size() < minimalNumberOfDeliveries ||
                    (selectedGroups.size() == minimalNumberOfDeliveries && isLargerMaxGroup(selectedGroups))) {
                minimalNumberOfDeliveries = selectedGroups.size();
                optimalGroups = new HashMap<>(selectedGroups);
            }
            return;
        }

        if (selectedGroups.size() >= minimalNumberOfDeliveries) {
            return; // No need to continue if we cannot improve on the current solution.
        }

        Map<String, Set<String>> productCoverage = calculateProductCoverage(remainingProducts);

        productCoverage.forEach((method, productsForMethod) -> {
            if (!productsForMethod.isEmpty()) {
                List<String> products = new ArrayList<>(productsForMethod);
                selectedGroups.put(method, products);
                remainingProducts.removeAll(productsForMethod);

                backtrack(selectedGroups, remainingProducts);

                // Backtrack
                remainingProducts.addAll(productsForMethod);
                selectedGroups.remove(method);
            }
        });
    }

    /**
     * Calculates the coverage of delivery methods for the remaining products.
     *
     * @param remainingProducts Products that are not yet assigned to any delivery group.
     * @return A map from delivery methods to the set of products that can be delivered using them.
     */
    private Map<String, Set<String>> calculateProductCoverage(Set<String> remainingProducts) {
        Map<String, Set<String>> coverage = new HashMap<>();
        remainingProducts.forEach(product ->
                deliveryOptions.getOrDefault(product, Collections.emptyList()).forEach(method ->
                        coverage.computeIfAbsent(method, k -> new HashSet<>()).add(product)
                )
        );
        return coverage;
    }

    /**
     * Checks if the new group of deliveries has a larger max group size than the current optimal groups.
     *
     * @param newGroups The new groups of deliveries being considered.
     * @return true if the new groups have a larger max group size, false otherwise.
     */
    private boolean isLargerMaxGroup(Map<String, List<String>> newGroups) {
        int newMaxSize = newGroups.values().stream().mapToInt(List::size).max().orElse(0);
        int currentMaxSize = optimalGroups != null ? optimalGroups.values().stream().mapToInt(List::size).max().orElse(0) : 0;
        return newMaxSize > currentMaxSize;
    }
}