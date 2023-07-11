package system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import system.Repository;


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
class RepositoryImpl<T, ID> implements Repository<T, ID> {

	/**
	 * Repository data, objects of type T stored in map indexed by ID.
	 */
	private final Map<ID, T> data;

	/**
	 * function that returns id of type ID for entity of type T.
	 */
	private final Function<T,ID> idProvider;


	/**
	 * Non-public constructor.
	 * 
	 * @param idProvider function that returns id of type ID for entity of type T.
	 */
	RepositoryImpl(Function<T,ID> idProvider) {
		if(idProvider==null)
			throw new IllegalArgumentException("idProvider is null");
		//
		this.data = new HashMap<ID, T>();
		this.idProvider = idProvider;
	}


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
	@Override
	public <S extends T> S save(S entity) {
		if(entity==null)
			throw new IllegalArgumentException("entity is null");
		//
		ID id = idProvider.apply(entity);
		if(id==null)
			throw new IllegalArgumentException("id is null");
		//
		data.put(id, entity);
		//
		return entity;
	}


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
	@Override
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
		if(entities==null)
			throw new IllegalArgumentException("entities is null");
		//
		List<S> saved = new ArrayList<S>();
		for(var e : entities) {
			saved.add(save(e));
		}
		return saved;
	}


	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none found.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	@Override
	public Optional<T> findById(ID id) {
		if(id==null)
			throw new IllegalArgumentException("id is null");
		//
		return Optional.ofNullable(data.get(id));
	}


	/**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param id must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	@Override
	public boolean existsById(ID id) {
		return findById(id).isPresent();
	}


	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	@Override
	public Iterable<T> findAll() {
		return data.values();
	}


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
	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		if(ids==null)
			throw new IllegalArgumentException("ids is null");
		//
		List<T> found = new ArrayList<T>();
		for(ID id : ids) {
			findById(id)
				.ifPresent(f -> found.add(f));
		}
		return found;
	}


	/**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities.
	 */
	@Override
	public long count() {
		return data.size();
	}


	/**
	 * Deletes the entity with the given id.
	 * <p>
	 * If the entity is not found in the persistence store it is silently ignored.
	 *
	 * @param id must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
	 */
	@Override
	public void deleteById(ID id) {
		if(id==null)
			throw new IllegalArgumentException("id is null");
		//
		data.remove(id);
	}


	/**
	 * Deletes a given entity.
	 *
	 * @param entity must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with
	 *           a different value from that found in the persistence store. Also thrown if the entity is assumed to be
	 *           present but does not exist in the database.
	 */
	@Override
	public void delete(T entity) {
		if(entity==null)
			throw new IllegalArgumentException("entity is null");
		//
		ID id = idProvider.apply(entity);
		if(id==null)
			throw new IllegalArgumentException("id is null");
		//
		deleteById(id);
	}


	/**
	 * Deletes all instances of the type {@code T} with the given IDs.
	 * <p>
	 * Entities that aren't found in the persistence store are silently ignored.
	 *
	 * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
	 * @throws IllegalArgumentException in case the given {@literal ids} or one of its elements is {@literal null}.
	 * @since 2.5
	 */
	@Override
	public void deleteAllById(Iterable<? extends ID> ids) {
		if(ids==null)
			throw new IllegalArgumentException("ids is null");
		//
		for(ID id : ids) {
			deleteById(id);
		}
	}


	/**
	 * Deletes the given entities.
	 *
	 * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
	 * @throws IllegalArgumentException in case the given {@literal entities} or one of its entities is {@literal null}.
	 * @throws OptimisticLockingFailureException when at least one entity uses optimistic locking and has a version
	 *           attribute with a different value from that found in the persistence store. Also thrown if at least one
	 *           entity is assumed to be present but does not exist in the database.
	 */
	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		if(entities==null)
			throw new IllegalArgumentException("entities is null");
		//
		for(T entity : entities) {
			delete(entity);
		}
	}


	/**
	 * Deletes all entities managed by the repository.
	 */
	@Override
	public void deleteAll() {
		data.clear();
	}

}
