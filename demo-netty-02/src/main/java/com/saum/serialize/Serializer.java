package com.saum.serialize;


/**
 * @Author saum
 * @Description: 序列化接口
 */
public interface Serializer {

    Serializer DEFAULT = SerializerAlgorithm.JAVA;

    /**
     * 获取序列化算法
     */
    byte getSerializerAlgorithm();

    /**
     * 序列化：将Java对象转为二进制
     */
    <T> byte[] serialize(T object);

    /**
    * 反序列化：将二进制转为Java对象
    */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
