package org.springframework.data.rest.repository;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.data.repository.Repository;

/**
 * @author Jon Brisbin <jon@jbrisbin.com>
 */
public interface RepositoryMetadata<R extends Repository<? extends Object, ? extends Serializable>, E extends EntityMetadata<? extends AttributeMetadata>> {

  String name();

  Class<? extends Object> domainType();

  R repository();

  E entityMetadata();

  Method queryMethod(String key);

  Map<String, Method> queryMethods();

}