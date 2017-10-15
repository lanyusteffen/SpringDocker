package stu.lanyu.springdocker.contract;

public interface IMapRequest<T1> {
    /* 将用户请求传输对象映射为领域对象 */
    T1 mapToDomain();
}
