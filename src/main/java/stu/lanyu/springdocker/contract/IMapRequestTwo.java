package stu.lanyu.springdocker.contract;

public interface IMapRequestTwo<T1, T2> {
    /* 将用户请求传输对象映射为领域对象 */
    T1 mapToDomainFirst();
    /* 将用户请求传输对象映射为领域对象 */
    T2 mapToDomainSecond();
}
