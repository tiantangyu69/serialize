package cc.lee.serialize.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by lizhitao on 16-3-15.
 */
public class PojoUtil {
    private static final ConcurrentMap<String, Method>  NAME_METHODS_CACHE = new ConcurrentHashMap<String, Method>();
    private static final ConcurrentMap<Class<?>, ConcurrentMap<String, Field>> CLASS_FIELD_CACHE =
            new ConcurrentHashMap<Class<?>, ConcurrentMap<String, Field>>();

    public static Object realize(Object pojo, Class<?> type, Type genericType) {
        return realize0(pojo, type, genericType, new IdentityHashMap<Object, Object>());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object realize0(Object pojo, Class<?> type, Type genericType, final Map<Object, Object> history) {
        if (pojo == null) {
            return null;
        }

        if (type != null && type.isEnum()
                && pojo.getClass() == String.class) {
            return Enum.valueOf((Class<Enum>) type, (String) pojo);
        }

        if (ReflectionUtil.isPrimitives(pojo.getClass())
                && !(type != null && type.isArray()
                && type.getComponentType().isEnum()
                && pojo.getClass() == String[].class)) {
            return CompatibleTypeUtils.compatibleTypeConvert(pojo, type);
        }

        Object o = history.get(pojo);

        if (o != null) {
            return o;
        }

        history.put(pojo, pojo);

        if (pojo.getClass().isArray()) {
            if (Collection.class.isAssignableFrom(type)) {
                Class<?> ctype = pojo.getClass().getComponentType();
                int len = Array.getLength(pojo);
                Collection dest = createCollection(type, len);
                for (int i = 0; i < len; i++) {
                    Object obj = Array.get(pojo, i);
                    Object value = realize0(obj, ctype, null, history);
                    dest.add(value);
                }
                return dest;
            } else {
                Class<?> ctype = (type != null && type.isArray() ? type.getComponentType() : pojo.getClass().getComponentType());
                int len = Array.getLength(pojo);
                Object dest = Array.newInstance(ctype, len);
                for (int i = 0; i < len; i++) {
                    Object obj = Array.get(pojo, i);
                    Object value = realize0(obj, ctype, null, history);
                    Array.set(dest, i, value);
                }
                return dest;
            }
        }

        if (pojo instanceof Collection<?>) {
            if (type.isArray()) {
                Class<?> ctype = type.getComponentType();
                Collection<Object> src = (Collection<Object>) pojo;
                int len = src.size();
                Object dest = Array.newInstance(ctype, len);
                int i = 0;
                for (Object obj : src) {
                    Object value = realize0(obj, ctype, null, history);
                    Array.set(dest, i, value);
                    i++;
                }
                return dest;
            } else {
                Collection<Object> src = (Collection<Object>) pojo;
                int len = src.size();
                Collection<Object> dest = createCollection(type, len);
                for (Object obj : src) {
                    Type keyType = getGenericClassByIndex(genericType, 0);
                    Class<?> keyClazz = obj.getClass();
                    if (keyType instanceof Class) {
                        keyClazz = (Class<?>) keyType;
                    }
                    Object value = realize0(obj, keyClazz, keyType, history);
                    dest.add(value);
                }
                return dest;
            }
        }

        if (pojo instanceof Map<?, ?> && type != null) {
            Object className = ((Map<Object, Object>) pojo).get("class");
            if (className instanceof String && !Map.class.isAssignableFrom(type)) {
                try {
                    type = ClassHelper.forName((String) className);
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
            Map<Object, Object> map;
            // 返回值类型不是方法签名类型的子集 并且 不是接口类型
            if (!type.isInterface()
                    && !type.isAssignableFrom(pojo.getClass())) {
                try {
                    map = (Map<Object, Object>) type.newInstance();
                } catch (Exception e) {
                    //ignore error
                    map = (Map<Object, Object>) pojo;
                }
            } else {
                map = (Map<Object, Object>) pojo;
            }

            if (Map.class.isAssignableFrom(type) || type == Object.class) {
                final Map<Object, Object> tmp = new HashMap<Object, Object>(map.size());
                tmp.putAll(map);
                for (Map.Entry<Object, Object> entry : tmp.entrySet()) {
                    Type keyType = getGenericClassByIndex(genericType, 0);
                    Type valueType = getGenericClassByIndex(genericType, 1);
                    Class<?> keyClazz;
                    if (keyType instanceof Class) {
                        keyClazz = (Class<?>) keyType;
                    } else {
                        keyClazz = entry.getKey() == null ? null : entry.getKey().getClass();
                    }
                    Class<?> valueClazz;
                    if (valueType instanceof Class) {
                        valueClazz = (Class<?>) valueType;
                    } else {
                        valueClazz = entry.getValue() == null ? null : entry.getValue().getClass();
                    }

                    Object key = keyClazz == null ? entry.getKey() : realize0(entry.getKey(), keyClazz, keyType, history);
                    Object value = valueClazz == null ? entry.getValue() : realize0(entry.getValue(), valueClazz, valueType, history);
                    map.put(key, value);
                }
                return map;
            } else if (type.isInterface()) {
                Object dest = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{type}, new PojoInvocationHandler(map));
                history.put(pojo, dest);
                return dest;
            } else {
                Object dest = newInstance(type);
                history.put(pojo, dest);
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    Object key = entry.getKey();
                    if (key instanceof String) {
                        String name = (String) key;
                        Object value = entry.getValue();
                        if (value != null) {
                            Method method = getSetterMethod(dest.getClass(), name, value.getClass());
                            Field field = getField(dest.getClass(), name);
                            if (method != null) {
                                if (!method.isAccessible())
                                    method.setAccessible(true);
                                Type ptype = method.getGenericParameterTypes()[0];
                                value = realize0(value, method.getParameterTypes()[0], ptype, history);
                                try {
                                    method.invoke(dest, value);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    throw new RuntimeException("Failed to set pojo " + dest.getClass().getSimpleName() + " property " + name
                                            + " value " + value + "(" + value.getClass() + "), cause: " + e.getMessage(), e);
                                }
                            } else if (field != null) {
                                value = realize0(value, field.getType(), field.getGenericType(), history);
                                try {
                                    field.set(dest, value);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(
                                            new StringBuilder(32)
                                                    .append("Failed to set filed ")
                                                    .append(name)
                                                    .append(" of pojo ")
                                                    .append(dest.getClass().getName())
                                                    .append(" : ")
                                                    .append(e.getMessage()).toString(),
                                            e);
                                }
                            }
                        }
                    }
                }
                if (dest instanceof Throwable) {
                    Object message = map.get("message");
                    if (message instanceof String) {
                        try {
                            Field filed = Throwable.class.getDeclaredField("detailMessage");
                            if (!filed.isAccessible()) {
                                filed.setAccessible(true);
                            }
                            filed.set(dest, (String) message);
                        } catch (Exception e) {
                        }
                    }
                }
                return dest;
            }
        }
        return pojo;
    }

    private static Object newInstance(Class<?> cls) {
        try {
            return cls.newInstance();
        } catch (Throwable t) {
            try {
                Constructor<?>[] constructors = cls.getConstructors();
                if (constructors != null && constructors.length == 0) {
                    throw new RuntimeException("Illegal constructor: " + cls.getName());
                }
                Constructor<?> constructor = constructors[0];
                if (constructor.getParameterTypes().length > 0) {
                    for (Constructor<?> c : constructors) {
                        if (c.getParameterTypes().length <
                                constructor.getParameterTypes().length) {
                            constructor = c;
                            if (constructor.getParameterTypes().length == 0) {
                                break;
                            }
                        }
                    }
                }
                return constructor.newInstance(new Object[constructor.getParameterTypes().length]);
            } catch (InstantiationException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }


    private static Method getSetterMethod(Class<?> cls, String property, Class<?> valueCls) {
        String name = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
        Method method = NAME_METHODS_CACHE.get(cls.getName() + "." + name + "(" + valueCls.getName() + ")");
        if (method == null) {
            try {
                method = cls.getMethod(name, valueCls);
            } catch (NoSuchMethodException e) {
                for (Method m : cls.getMethods()) {
                    if (ReflectionUtil.isBeanPropertyWriteMethod(m)
                            && m.getName().equals(name)) {
                        method = m;
                    }
                }
            }
            if (method != null) {
                NAME_METHODS_CACHE.put(cls.getName() + "." + name + "(" + valueCls.getName() + ")", method);
            }
        }
        return method;
    }

    private static Field getField(Class<?> cls, String fieldName) {
        Field result = null;
        if (CLASS_FIELD_CACHE.containsKey(cls)
                && CLASS_FIELD_CACHE.get(cls).containsKey(fieldName)) {
            return CLASS_FIELD_CACHE.get(cls).get(fieldName);
        }
        try {
            result = cls.getField(fieldName);
        } catch (NoSuchFieldException e) {
            for (Field field : cls.getFields()) {
                if (fieldName.equals(field.getName())
                        && ReflectionUtil.isPublicInstanceField(field)) {
                    result = field;
                    break;
                }
            }
        }
        if (result != null) {
            ConcurrentMap<String, Field> fields = CLASS_FIELD_CACHE.get(cls);
            if (fields == null) {
                fields = new ConcurrentHashMap<String, Field>();
                CLASS_FIELD_CACHE.putIfAbsent(cls, fields);
            }
            fields = CLASS_FIELD_CACHE.get(cls);
            fields.putIfAbsent(fieldName, result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> createCollection(Class<?> type, int len) {
        if (type.isAssignableFrom(ArrayList.class)) {
            return  new ArrayList<Object>(len);
        }
        if (type.isAssignableFrom(HashSet.class)) {
            return new HashSet<Object>(len);
        }
        if (! type.isInterface() && ! Modifier.isAbstract(type.getModifiers())) {
            try {
                return (Collection<Object>) type.newInstance();
            } catch (Exception e) {
                // ignore
            }
        }
        return new ArrayList<Object>();
    }

    private static class PojoInvocationHandler implements InvocationHandler {

        private Map<Object, Object> map;

        public PojoInvocationHandler(Map<Object, Object> map) {
            this.map = map;
        }

        @SuppressWarnings("unchecked")
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(map, args);
            }
            String methodName = method.getName();
            Object value = null;
            if (methodName.length() > 3 && methodName.startsWith("get")) {
                value = map.get(methodName.substring(3, 4).toLowerCase() + methodName.substring(4));
            } else if (methodName.length() > 2 && methodName.startsWith("is")) {
                value = map.get(methodName.substring(2, 3).toLowerCase() + methodName.substring(3));
            } else {
                value = map.get(methodName.substring(0, 1).toLowerCase() + methodName.substring(1));
            }
            if (value instanceof Map<?,?> && ! Map.class.isAssignableFrom(method.getReturnType())) {
                value = realize0((Map<String, Object>) value, method.getReturnType(), null, new IdentityHashMap<Object, Object>());
            }
            return value;
        }
    }

    /**
     * 获取范型的类型
     * @param genericType
     * @param index
     * @return List<Person>  返回Person.class ,Map<String,Person> index=0 返回String.class index=1 返回Person.class
     */
    private static Type getGenericClassByIndex(Type genericType, int index){
        Type clazz = null ;
        //范型参数转换
        if (genericType instanceof ParameterizedType){
            ParameterizedType t = (ParameterizedType)genericType;
            Type[] types = t.getActualTypeArguments();
            clazz = types[index];
        }
        return clazz;
    }

}
