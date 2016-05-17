package nl.marketingsciences.dataEditing;

import javax.persistence.EntityExistsException;

import org.hibernate.Session;

import nl.marketingsciences.beans.database.InteractionGroup;

public class SaveInteractionGroup {
	public static void saveInteractionGroup(InteractionGroup interactionGroup){
		try{
			Session sess=HibernateUtil.getSessionFactory().openSession();
			sess.beginTransaction();
			
			sess.save(interactionGroup);
			sess.getTransaction().commit();
			sess.close();
		} catch (EntityExistsException e) {
			System.out.println("already exists");
		}
		
	}
}
