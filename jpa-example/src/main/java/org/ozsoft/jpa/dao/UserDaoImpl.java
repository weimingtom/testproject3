package org.ozsoft.jpa.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.ozsoft.jpa.domain.User;

/**
 * User DAO implementation with JPA and JTA (local transactions).
 * 
 * @author Oscar Stigter
 */
public class UserDaoImpl implements UserDao {

	private static final String PU_NAME = "jpa-example";

	private final EntityManager em;

	/**
	 * Constructor.
	 */
	public UserDaoImpl() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME);
		em = emf.createEntityManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ozsoft.jpa.dao.UserDao#create(org.ozsoft.jpa.domain.User)
	 */
	@Override
	public void create(User user) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			em.persist(user);
			tx.commit();
		} catch (PersistenceException e) {
			tx.rollback();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ozsoft.jpa.dao.UserDao#retrieve(long)
	 */
	@Override
	public User retrieve(long id) {
		return (User) em.find(User.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ozsoft.jpa.dao.UserDao#update(org.ozsoft.jpa.domain.User)
	 */
	@Override
	public void update(User user) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			em.merge(user);
			tx.commit();
		} catch (PersistenceException e) {
			tx.rollback();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ozsoft.jpa.dao.UserDao#delete(org.ozsoft.jpa.domain.User)
	 */
	@Override
	public void delete(User user) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			em.remove(user);
			tx.commit();
		} catch (PersistenceException e) {
			tx.rollback();
			throw e;
		}
	}

}
