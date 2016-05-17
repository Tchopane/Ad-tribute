package nl.marketingsciences.dataEditing;

import org.hibernate.Session;

import nl.marketingsciences.beans.database.TMS;

public class SaveTMS {
	public static void saveTMS(TMS tMS){
		Session sess=HibernateUtil.getSessionFactory().openSession();
		sess.beginTransaction();
		
		sess.save(tMS);
		sess.getTransaction().commit();
		sess.close();
	}
}
