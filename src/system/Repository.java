package system;

import java.util.Optional;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 * The Repository<T, ID> stores objects of type T and provides CRUD
 * operations: Create, Read, Update, Delete.
 * 
 * Examples of T are classes of business objects: Customer, Order, Article.
 * Generic type ID refers to the type of identifier used in classes T.
 * 
 * @param T entity type, T: Customer, Order, Article
 * @param ID type of identifier (id), e.g. Customer.ID: Long
 * 
 * Authors of original Spring Code:
 * @author Oliver Gierke
 * @author Eberhard Wolff
 * @author Jens Schauder
 * 
 * see Spring CrudRepository javadoc:
 * https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html#save(S)
 * 
 * source code Spring CrudRepository:
 * https://github.com/spring-projects/spring-data-commons/blob/main/src/main/java/org/springframework/data/repository/CrudRepository.java
 */
public interface Repository<T, ID> {

    /**
     * Saves object (entity) to a repository. Use the returned instance for further operations as the save operation might have changed the
	 * entity instance completely.
	 * 
     * see Spring CrudRepository.save() javadoc:
     * https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html#save(S)
     * 
     * @param entity to save, entity must not be null.
     * @return the saved entity, will never be null.
     */
	<S extends T> S save(S entity);

	/**
	 * Saves all given entities.
	 *
	 * @param entities must not be {@literal null} nor must it contain {@literal null}.
	 * @return the saved entities; will never be {@literal null}. The returned {@literal Iterable} will have the same size
	 *         as the {@literal Iterable} passed as an argument.
	 * @throws IllegalArgumentException in case the given {@link Iterable entities} or one of its entities is
	 *           {@literal null}.
	 * @throws OptimisticLockingFailureException when at least one entity uses optimistic locking and has a version
	 *           attribute with a different value from that found in the persistence store. Also thrown if at least one
	 *           entity is assumed to be present but does not exist in the database.
	 */
	<S extends T> Iterable<S> saveAll(Iterable<S> entities);

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none found.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	Optional<T> findById(ID id);

	/**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param id must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	boolean existsById(ID id);

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	Iterable<T> findAll();

	/**
	 * Returns all instances of the type {@code T} with the given IDs.
	 * <p>
	 * If some or all ids are not found, no entities are returned for these IDs.
	 * <p>
	 * Note that the order of elements in the result is not guaranteed.
	 *
	 * @param ids must not be {@literal null} nor contain any {@literal null} values.
	 * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
	 *         {@literal ids}.
	 * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
	 */
	Iterable<T> findAllById(Iterable<ID> ids);

	/**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities.
	 */
	long count();

	/**
	 * Deletes the entity with the given id.
	 * <p>
	 * If the entity is not found in the persistence store it is silently ignored.
	 *
	 * @param id must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
	 */
	void deleteById(ID id);

	/**
	 * Deletes a given entity.
	 *
	 * @param entity must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with
	 *           a different value from that found in the persistence store. Also thrown if the entity is assumed to be
	 *           present but does not exist in the database.
	 */
	void delete(T entity);

	/**
	 * Deletes all instances of the type {@code T} with the given IDs.
	 * <p>
	 * Entities that aren't found in the persistence store are silently ignored.
	 *
	 * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
	 * @throws IllegalArgumentException in case the given {@literal ids} or one of its elements is {@literal null}.
	 * @since 2.5
	 */
	void deleteAllById(Iterable<? extends ID> ids);

	/**
	 * Deletes the given entities.
	 *
	 * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
	 * @throws IllegalArgumentException in case the given {@literal entities} or one of its entities is {@literal null}.
	 * @throws OptimisticLockingFailureException when at least one entity uses optimistic locking and has a version
	 *           attribute with a different value from that found in the persistence store. Also thrown if at least one
	 *           entity is assumed to be present but does not exist in the database.
	 */
	void deleteAll(Iterable<? extends T> entities);

	/**
	 * Deletes all entities managed by the repository.
	 */
	void deleteAll();

}
