package com.msibai.cloud.mappers;

/**
 * Generic Mapper interface for mapping objects between types A and B.
 *
 * @param <A> Type A
 * @param <B> Type B
 */
public interface Mapper<A, B> {

  /**
   * Maps an object of type A to an object of type B.
   *
   * @param a Object of type A to be mapped
   * @return Mapped object of type B
   */
  B mapTo(A a);

  /**
   * Maps an object of type B to an object of type A.
   *
   * @param b Object of type B to be mapped
   * @return Mapped object of type A
   */
  A mapFrom(B b);
}
