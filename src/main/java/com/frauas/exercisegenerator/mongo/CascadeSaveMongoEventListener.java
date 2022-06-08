package com.frauas.exercisegenerator.mongo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.util.ReflectionUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();

        ReflectionUtils.doWithFields(source.getClass(),
                new CascadeSaveFieldCallback(mongoOperations, source));
    }

    @AllArgsConstructor
    private static class CascadeSaveFieldCallback implements ReflectionUtils.FieldCallback {

        private MongoOperations mongoOperations;

        private Object source;

        @Override
        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            ReflectionUtils.makeAccessible(field);

            if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeSave.class)) {
                final Object fieldValue = field.get(source);

                DBRefFieldCallback callback = new DBRefFieldCallback();
                Class<?> fieldClass = fieldValue.getClass();

                // If we have a collection, we need to check if the generic type of the
                // collection includes a required id field and insert each value separately
                if (fieldValue instanceof Collection<?>) {
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    fieldClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                    ReflectionUtils.doWithFields(fieldClass, callback);

                    if (!callback.isIdFound()) {
                        throw new MappingException("Cannot perform cascade save on child object without id set");
                    }

                    ((Collection<?>) fieldValue).forEach((singleValue) -> mongoOperations.save(singleValue));
                } else {
                    ReflectionUtils.doWithFields(fieldClass, callback);

                    if (!callback.isIdFound()) {
                        throw new MappingException("Cannot perform cascade save on child object without id set");
                    }

                    mongoOperations.save(fieldValue);
                }

            }
        }
    }

    @NoArgsConstructor
    private static class DBRefFieldCallback implements ReflectionUtils.FieldCallback {
        @Getter
        private boolean idFound;

        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            ReflectionUtils.makeAccessible(field);

            if (field.isAnnotationPresent(Id.class)) {
                idFound = true;
            }
        }

    }

}