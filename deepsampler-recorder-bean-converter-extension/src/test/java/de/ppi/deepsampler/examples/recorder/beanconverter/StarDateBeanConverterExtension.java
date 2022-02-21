/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.examples.recorder.beanconverter;

import de.ppi.deepsampler.persistence.bean.PersistentBeanConverter;
import de.ppi.deepsampler.persistence.bean.ext.StandardBeanConverterExtension;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This is an example of a {@link de.ppi.deepsampler.persistence.bean.ext.BeanConverterExtension}.
 * <p>
 * By default, {@link LocalDateTime} is serialized using a single numerical value. For the sake of demonstration, we change this
 * format to a stardate! To achieve this, we need to follow four steps:
 * <ul>
 *     <li>Bind this extension to a SamplerFixture, or a test using the annotation {@link UseBeanConverterExtension}</li>
 *     <li>Bind this extension to the type {@link LocalDateTime}</li>
 *     <li>Convert a {@link LocalDateTime} to a String, that contains the stardate. This will be used to serialize the {@link LocalDateTime}</li>
 *     <li>Convert the String back to a {@link LocalDateTime} during deserialization}</li>
 * </ul>
 */
public class StarDateBeanConverterExtension extends StandardBeanConverterExtension {
    
    private static final DateTimeFormatter STAR_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyDDD.AAAA");

    /**
     * Here we bind the extension to {@link LocalDateTime}.
     *
     * @param beanClass The type of the object, that is to be converted. This is the actual return type, or parameter
     *                  type as it is declared by the stubbed method.
     * @param beanType  If beanClass is declared as a generic type, the corresponding {@link ParameterizedType} is taken
     *                  from the method declaration and passed to this method.
     *                  Otherwise, null is passed.
     * @return true if this extension wants to convert all objects of the type beanClass.
     */
    @Override
    public boolean isProcessable(Class<?> beanClass, ParameterizedType beanType) {
        return beanClass.isAssignableFrom(LocalDateTime.class);
    }

    /**
     * Converts the original object during serialization to a persistable form.
     * <p>
     * In theory all sorts of objects can be returned here, as long as they are serializable by the underlying
     * persistence API (Jackson by default). However, we recommend, to use a
     * {@link de.ppi.deepsampler.persistence.bean.DefaultPersistentBean} for all non-primitive types (including wrapper).
     * DefaultPersistentBeans are a simple key-value-based abstraction of Beans that can be serialized without writing
     * concrete type names to the json-file. This simplifies refactorings greatly, since name-changes don't affect 
     * sample-files anymore.
     * <p>
     * However, for the sake of demonstration, we now simply convert a {@link LocalDateTime} to a String. So we don't need a
     * {@link de.ppi.deepsampler.persistence.bean.DefaultPersistentBean}.
     *
     * @param originalBean            The original object, that is to be converted for serialisation.
     * @param beanType                If the type of the Object was declared as a generic type, the corresponding
     *                                {@link ParameterizedType} is passed to convert(). This is especially useful,
     *                                because the {@link ParameterizedType} cannot be retrieved from originalBean, it
     *                                can only be retrieved from the method declaration.
     * @param persistentBeanConverter If originalBean has non-primitive properties, these properties don't necessarily
     *                                need to be converted manually. Instead, they can be passed to
     *                                {@link PersistentBeanConverter#convert(Object, Type)} for recursive bean
     *                                conversions.
     * @return An object in a form that can be serialized by the underlying persistence API.
     */
    @Override
    public Object convert(Object originalBean, ParameterizedType beanType, PersistentBeanConverter persistentBeanConverter) {
        return STAR_DATE_FORMATTER.format((LocalDateTime) originalBean);
    }

    /**
     * Converts persistentBean during deserialization back to the type that was originally used by the stubbed method.
     * <p>
     * Since we saved our {@link LocalDateTime} as a String during convert(), we now need to parse the date from the String
     * and return a {@link LocalDateTime} containing the date.
     *
     * @param persistentBean          An object, as it was deserialized by the underlying persistence API. In most cases, this will
     *                                a {@link de.ppi.deepsampler.persistence.model.PersistentBean} or a primitive type.
     * @param targetClass             The type that is used by the stubbed method. This is the type to which persistentBean needs to be
     *                                converted.
     * @param targetType              If targetClass was declared as a generic by the stubbed method, the corresponding
     *                                {@link ParameterizedType} is passed here.
     * @param persistentBeanConverter If persistentBean contains some properties that need to be reverted recursively,
     *                                this can be done by passing these properties to
     *                                {@link PersistentBeanConverter#revert(Object, Class, ParameterizedType)}
     * @param <T>
     * @return                        The deserialized persistentBean as a type, that can be used by the stubbed method.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T revert(Object persistentBean, Class<T> targetClass, ParameterizedType targetType, PersistentBeanConverter persistentBeanConverter) {
        return (T) LocalDateTime.parse((String) persistentBean, STAR_DATE_FORMATTER);
    }
}
