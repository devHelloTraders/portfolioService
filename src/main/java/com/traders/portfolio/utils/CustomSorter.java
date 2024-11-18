package com.traders.portfolio.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CustomSorter {

    public static <T> void sortById(List<T> objects,
                                    List<Long> sortedIds,
                                    Function<T, Long> idExtractor,
                                    Optional<BiConsumer<T, Integer>> orderAssigner) {
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < sortedIds.size(); i++) {
            orderMap.put(sortedIds.get(i), i);
        }
        objects.sort(Comparator.comparingInt(o -> orderMap.getOrDefault(idExtractor.apply(o), Integer.MAX_VALUE)));

        orderAssigner.ifPresent(assigner->{
            objects.forEach(object -> {
                Long id = idExtractor.apply(object);
                Integer order = orderMap.getOrDefault(id, Integer.MAX_VALUE);
                assigner.accept(object, order);
            });
        });

    }
}
