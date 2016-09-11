package com.jorge.client;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.jorge.entity.Guide;
import com.jorge.entity.Student;
import com.jorge.util.HibernateUtil;

/**
 * This is a ONE TO MANY relationship
 * 
 * This example is bidirectional.
 * In this kind of association (bidirectional), one of the sides (and only one) is the owner of the relationship.
 * The owner of the relationship is responsible for the association column(s) update
 * "MANY" side of the One To Many bidirectional relationship is (almost) always the owner side
 * Owner cares about the relationship
 * Owner is the entity that is persisted to the table that has the foreign key column
 *
 */
public class MainSet {

	public static void main(String[] args) {
		BasicConfigurator.configure(); // Necessary for configure log4j. It must be the first line in main method
								       // log4j.properties must be in /src directory
		
		Logger  logger = Logger.getLogger(MainSet.class.getName());
		logger.debug("log4j configured correctly and logger set");

		logger.debug("getting session factory from HibernateUtil.java");
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction txn = session.getTransaction();
		
		try {
			
			logger.debug("beginning persist transaction");
			txn.begin(); 

			logger.debug("setting data in Guide objects");
			Guide guide1 = new Guide("GD200331", "Homer Simpson", 1200);
			Guide guide2 = new Guide("GD200332", "Marge Simpson", 1600);
			
			logger.debug("setting data in Student objects");
			Student student1 = new Student("ST109883", "Bart Simpson", guide1); // Guide 1
			Student student2 = new Student("ST109884", "Lisa Simpson", guide1); // Guide 1 too
			
			logger.debug("adding Student objects to Guide objects");
			guide1.getStudents().add(student1);
			guide2.getStudents().add(student2);
			
			logger.debug("persisting data");
			session.persist(guide1);
			session.persist(guide2);
		
			logger.debug("making commit");
			txn.commit();
			
		} catch (Exception e) {
			if (txn != null) {
				logger.error("something was wrong, making rollback of transactions");
				txn.rollback(); // If something was wrong, we make rollback
			}
			logger.error("Exception: " + e.getMessage().toString());
		} finally {
			if (session != null) {
				logger.debug("close session");
				session.close();
			}
		}
	}

}
