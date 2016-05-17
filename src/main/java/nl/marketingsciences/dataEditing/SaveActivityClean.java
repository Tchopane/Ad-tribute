package nl.marketingsciences.dataEditing;

import org.hibernate.Session;

import nl.marketingsciences.beans.database.ActivityClean;

public class SaveActivityClean {
	public static void saveActivityClean(ActivityClean activityClean){
		Session sess=HibernateUtil.getSessionFactory().openSession();
		sess.beginTransaction();
		
		sess.save(activityClean);
		sess.getTransaction().commit();
		sess.close();
	}
}
