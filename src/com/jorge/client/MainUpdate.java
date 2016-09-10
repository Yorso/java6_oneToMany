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
 * In this class, we want One side (not owner) to update data
 */
public class MainUpdate {

	public static void main(String[] args) {
		BasicConfigurator.configure(); // Necessary for configure log4j. It must be the first line in main method
								       // log4j.properties must be in /src directory
		
		Logger  logger = Logger.getLogger(MainUpdate.class.getName());
		logger.debug("log4j configured correctly and logger set");

		logger.debug("getting session factory from HibernateUtil.java");
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction txn = session.getTransaction();
		
		try {
			
			logger.debug("beginning persist transaction");
			txn.begin(); 

			logger.debug("getting Guide data");
			Guide guide = (Guide)session.get(Guide.class, 2L);
			
			logger.debug("getting Student data");
			Student student = (Student)session.get(Student.class, 2L);
			
			// Both of students are associated to guide 1. Now we want student 2 to associate to guide 2
			logger.debug("associating student 2 to guide 2");
			//guide.getStudent().add(student); // Wrong way!!!! student 2 continues associated to guide 1
											   // It is because of the Guide (one side) is not the owner (many side) of the relationship (Student is).
											   // Guide is not responsible of the relationship
											   // Guide (inverse end) only care about itself. It does not care about the relationship
			
			//student.setGuide(guide); // This way works fine because Student is the owner of the relationship. 
									   // But this way is not what we are looking for because we are updating data from Many side (Owner) to One side (not owner) and 
									   // we want to update data from One side (not owner - Guide object) to many side (Many side). Guide is not the owner of the relationship.
									   // The result with both methods (Many to One or One to Many) is the same.
			
			guide.addStudent(student); // This way works fine too and it is the way we are looking for, update data from entity that is not the owner of the relationship => Guide
									   // addStudent() method is implemented in Guide.java class by us
									   // The result with both methods (Many to One or One to Many) is the same.
			
			//session.persist(guide); // We don't need this instruction for updating data in DB
			
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
