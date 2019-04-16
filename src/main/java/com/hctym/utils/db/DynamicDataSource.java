package com.hctym.utils.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lcg on 16/12/13.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
public @interface DynamicDataSource {
    String value();
}
