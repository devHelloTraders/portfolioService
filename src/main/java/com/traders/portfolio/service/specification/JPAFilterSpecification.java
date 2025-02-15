package com.traders.portfolio.service.specification;

import com.traders.portfolio.domain.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JPAFilterSpecification {

    private static final List<String> PAGINATION_KEYS = List.of("page", "size", "sort","userId","watchListExchangeSegment");


    public static <T extends AbstractAuditingEntity<Long>>Specification<T> setFilter(Map<String,Object> filterMap) {
        filterMap.keySet().removeIf(PAGINATION_KEYS::contains);
        return new Specification<T>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                filterMap.forEach((key, value) -> {
                    Predicate predicate;
                    if (key.contains(".")) {
                        // Handle nested property (e.g., "stock.name" or "watchList.userId")
                        String[] nestedKeys = key.split("\\.");
                        Path<Object> path = root.get(nestedKeys[0]);
                        for (int i = 1; i < nestedKeys.length; i++) {
                            path = path.get(nestedKeys[i]);
                        }
                        predicate = criteriaBuilder.equal(path, value);
                    } else {
                        // Handle direct property (e.g., "price" or "orderType")
                        predicate = criteriaBuilder.equal(root.get(key), value);
                    }


//                    filterMap.entrySet().forEach(filter ->
//                    {
//                        predicate = criteriaBuilder.equal(root.get(filter.getKey()), filter.getValue());
//                        predicates.add(predicate);
//                    });

                    predicates.add(predicate);
                });
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

            }

        };
    }

}
