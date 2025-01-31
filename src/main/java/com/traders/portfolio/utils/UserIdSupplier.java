package com.traders.portfolio.utils;

import com.traders.common.utils.CommonValidations;
import com.traders.portfolio.exception.BadRequestAlertException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Supplier;

public class UserIdSupplier implements Supplier<Long> {
    private UserIdSupplier() {
    }

    @Override
    public Long get() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        long id;
        if ((id = CommonValidations.getNumber(userId, Long.class)) == 0)
            throw new BadRequestAlertException("Invalid User details", "User Service", "Request hit by invalid user.");
        return id;

    }

    public static Long getUserId() {
        return new UserIdSupplier().get();
    }
}
