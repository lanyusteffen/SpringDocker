package stu.lanyu.springdocker.contract;

public interface IMapRequestThird<T1, T2, T3> {
    /* 将用户请求传输对象映射为领域对象 */
    T1 mapToDomainFirst();
    /* 将用户请求传输对象映射为领域对象 */
    T2 mapToDomainSecond();
    /* 将用户请求传输对象映射为领域对象 */
    T3 mapToDomainThird();
}
