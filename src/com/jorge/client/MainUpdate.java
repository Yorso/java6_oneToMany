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
			
			// Both of students are associated with guide 1. Now we want student 2 to associate with guide 2
			logger.debug("associating student 2 to guide 2");
			//guide.getStudent().add(student); // Wrong way!!!! student 2 continues associated to guide 1
											   // It is because of the Guide is not the owner of the relationship (Student is).
											   // Guide is not responsible of the relationship
											   // Guide (inverse end) only care about itself. It does not care about the relationship
			
			//student.setGuide(guide); // This way works because Student is the owner of the relationship. 
									   // But we want update data through Guide object (Guide is not the owner of the relationship)
			
			guide.addStudent(student); // This way works too and it is the way we are looking for, update data from entity that is not the owner of the relationship => Guide
									   // addStudent() method is implemented in Guide.java class by us
			
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
