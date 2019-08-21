package com.square.common.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by chenxiaogang on 18/5/10 0010.
 * version: 0.1
 */
public class GsonBuilderUtil implements Serializable{

    private static GsonBuilder builder = new GsonBuilder();

    private GsonBuilderUtil(){}

    static {
        builder.serializeSpecialFloatingPointValues();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                return expose != null && !expose.serialize();
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                return expose != null && !expose.deserialize();
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        });

    }

    public static GsonBuilder getBuilder(){
        return builder;
    }

}
