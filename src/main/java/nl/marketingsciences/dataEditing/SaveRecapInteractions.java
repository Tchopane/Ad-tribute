package nl.marketingsciences.dataEditing;

import org.hibernate.Session;

import nl.marketingsciences.beans.database.RecapInteraction;

public class SaveRecapInteractions {
	public static void saveRecapInteractions(RecapInteraction recapInteraction){
		Session sess=HibernateUtil.getSessionFactory().openSession();
		sess.beginTransaction();
		sess.save(recapInteraction);
		sess.getTransaction().commit();
		sess.close();
	}
}
