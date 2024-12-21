package com.traders.portfolio.service.specification;

import com.traders.portfolio.domain.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JPAFilterWithJoinSpecification {

    private static final List<String> PAGINATION_KEYS = List.of("page", "size", "sort","userId");


    public static <T extends AbstractAuditingEntity<Long>>Specification<T> setFilter(Map<String,Object> filterMap,String column, String entity) {
        filterMap.keySet().removeIf(PAGINATION_KEYS::contains);
        return new Specification<T>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                filterMap.forEach((key, value) -> {
                    Predicate predicate;
                    if (key.contains(".")) {
                        String[] nestedKeys = key.split("\\.");
                        Path<Object> path = root.get(nestedKeys[0]);
                        for (int i = 1; i < nestedKeys.length; i++) {
                            path = path.get(nestedKeys[i]);
                        }
                        predicate = criteriaBuilder.equal(path, value);
                    }else if (column.equals(key)) {
                        Join<Object, Object> watchListJoin = root.join(entity);
                        predicate = criteriaBuilder.equal(watchListJoin.get(column), value);
                    }  else {
                        predicate = criteriaBuilder.equal(root.get(key), value);
                    }


                    predicates.add(predicate);
                });
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

            }

        };
    }
    
}
