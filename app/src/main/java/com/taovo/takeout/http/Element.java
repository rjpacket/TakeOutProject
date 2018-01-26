package com.taovo.takeout.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Element<T> implements ParameterizedType {

    private Class<T> cl;

    public Element(Class<T> cl) {
        this.cl = cl;
    }

    public Type[] getActualTypeArguments() {
        return new Type[] {cl};
    }

    public Type getRawType() {
        return cl;
    }

    public Type getOwnerType() {
        return null;
    }
}