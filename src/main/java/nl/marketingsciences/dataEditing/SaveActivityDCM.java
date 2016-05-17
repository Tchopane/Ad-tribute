package nl.marketingsciences.dataEditing;

import org.hibernate.Session;
import nl.marketingsciences.beans.database.Activity;

public class SaveActivityDCM {
	public static void saveActivity(Activity activity){
		Session sess=HibernateUtil.getSessionFactory().openSession();
		sess.beginTransaction();
		
		sess.save(activity);
		sess.getTransaction().commit();
		sess.close();
	}
}
