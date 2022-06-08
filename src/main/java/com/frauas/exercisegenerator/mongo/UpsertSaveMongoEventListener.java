package com.frauas.exercisegenerator.mongo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;

import lombok.AllArgsConstructor;

public class UpsertSaveMongoEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();

        ReflectionUtils.doWithFields(source.getClass(),
                new UpsertSaveFieldCallback(mongoOperations, source));
    }

    @AllArgsConstructor
    private static class UpsertSaveFieldCallback implements ReflectionUtils.FieldCallback {

        private MongoOperations mongoOperations;

        private Object source;

        @Override
        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            ReflectionUtils.makeAccessible(field);

            if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(UpsertSave.class)) {
                final Object fieldValue = field.get(source);

                UpsertSave annotation = field.getAnnotation(UpsertSave.class);

                String[] filters = annotation.filters();
                Class<?> fieldClass = fieldValue.getClass();

                // If we have a collection, we need to check if the generic type of the
                // collection includes the required filter fields
                if (fieldValue instanceof Collection<?>) {
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    fieldClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }

                // Check if specified filter fields exist on target class
                for (String filter : filters) {
                    Field filterField = ReflectionUtils.findField(fieldClass, filter);

                    if (filterField == null)
                        throw new MappingException("Filter field '" + filter
                                + "' could not be found on annotated parameter");
                }

                // Collections need to be handled seperately
                if (fieldValue instanceof Collection<?>) {
                    Collection<Object> resolved = Collections.emptyList();
                    Collection<?> fieldValueCollection = ((Collection<?>) fieldValue);

                    // Handle each value individually and fill resolved collection with resolved
                    // values
                    for (Object singleValue : fieldValueCollection) {
                        // Build query with filters
                        Query query = new Query();

                        for (String filter : filters) {
                            Field filterField = ReflectionUtils.findField(fieldClass, filter);
                            ReflectionUtils.makeAccessible(filterField);

                            query.addCriteria(Criteria.where(filter).is(filterField.get(singleValue)));
                        }

                        Object document = mongoOperations.findOne(query, singleValue.getClass());

                        // If document exists use existing one, otherwise persist new one
                        if (document == null) {
                            resolved.add(mongoOperations.save(singleValue));
                        } else {
                            resolved.add(document);
                        }
                    }

                    // override current value on source object
                    field.set(source, resolved);
                } else {
                    // Build query with filters
                    Query query = new Query();

                    for (String filter : filters) {
                        Field filterField = ReflectionUtils.findField(fieldClass, filter);
                        ReflectionUtils.makeAccessible(filterField);

                        query.addCriteria(Criteria.where(filter).is(filterField.get(fieldValue)));
                    }

                    Object resolved = mongoOperations.findOne(query, fieldClass.getClass());

                    // If document exists use existing one, otherwise persist new one
                    if (resolved == null) {
                        resolved = mongoOperations.save(fieldValue);
                    }

                    field.set(source, resolved);
                }
            }

        }
    }
}