package com.hctym.utils.db;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by lcg on 16/12/13.
 */
public class MultipleDataSourceAspect {
    private static final Logger LOG = LoggerFactory.getLogger(MultipleDataSourceAspect.class);

    public MultipleDataSourceAspect() {
    }

    public Object doAround(ProceedingJoinPoint jp) throws Throwable {
        if(LOG.isDebugEnabled()) {
            LOG.debug("MultipleDataSourceAspectAdvice invoked!");
        }

        Signature signature = jp.getSignature();
        String dataSourceKey = this.getDataSourceKey(signature);
        if(StringUtils.hasText(dataSourceKey)) {
            MultipleDataSource.setDataSourceKey(dataSourceKey);
        }

        Object jpVal = jp.proceed();
        return jpVal;
    }

    private String getDataSourceKey(Signature signature) {
        if(signature == null) {
            return null;
        } else {
            if(signature instanceof MethodSignature) {
                MethodSignature methodSignature = (MethodSignature)signature;
                Method method = methodSignature.getMethod();
                if(method.isAnnotationPresent(DynamicDataSource.class)) {
                    return this.dsSettingInMethod(method);
                }

                Class declaringClazz = method.getDeclaringClass();
                if(declaringClazz.isAnnotationPresent(DynamicDataSource.class)) {
                    try {
                        return this.dsSettingInConstructor(declaringClazz);
                    } catch (Exception var6) {
                        LOG.error("获取构�?�方法的DataSource注解失败", var6);
                    }
                }
            }

            return null;
        }
    }

    private String dsSettingInMethod(Method method) {
        DynamicDataSource dataSource = (DynamicDataSource)method.getAnnotation(DynamicDataSource.class);
        return dataSource.value();
    }

    private String dsSettingInConstructor(Class clazz) {
        DynamicDataSource dataSource = (DynamicDataSource)clazz.getAnnotation(DynamicDataSource.class);
        return dataSource.value();
    }

}
