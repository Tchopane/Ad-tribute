package nl.marketingsciences.dataEditing;

import org.hibernate.Session;
import nl.marketingsciences.beans.database.Interaction;

public class SaveInteractionsDCM {
	public static void saveInteraction(Interaction interaction){
		Session sess=HibernateUtil.getSessionFactory().openSession();
		sess.beginTransaction();
		
		sess.save(interaction);
		sess.getTransaction().commit();
		sess.close();
	}
}
