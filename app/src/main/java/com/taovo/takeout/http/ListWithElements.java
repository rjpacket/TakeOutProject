package com.taovo.takeout.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ListWithElements<T> implements ParameterizedType {

    private Class<T> elementsClass;

    public ListWithElements(Class<T> elementsClass) {
        this.elementsClass = elementsClass;
    }

    public Type[] getActualTypeArguments() {
        return new Type[] {elementsClass};
    }

    public Type getRawType() {
        return List.class;
    }

    public Type getOwnerType() {
        return null;
    }
}