package nl.marketingsciences.dataEditing;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import nl.marketingsciences.beans.database.Interaction;
import nl.marketingsciences.beans.database.Activity;


public class HibernateUtil {

	  private static final SessionFactory sessionFactory = buildSessionFactory();
	  private static Configuration cfg;
	  
	  
	  
	    @SuppressWarnings("deprecation")
		private static SessionFactory buildSessionFactory() {
	        try {
	            // Create the SessionFactory from hibernate.cfg.xml
	        	 cfg = new Configuration();
	        	cfg
	        		.addAnnotatedClass(Interaction.class)
	        		.addAnnotatedClass(Activity.class);
	        	return cfg.configure().buildSessionFactory();
	        }
	        catch (Throwable ex) {
	            // Make sure you log the exception, as it might be swallowed
	            System.err.println("Initial SessionFactory creation failed." + ex);
	            throw new ExceptionInInitializerError(ex);
	        }
	    }
	 
	    public static SessionFactory getSessionFactory() {
	        return sessionFactory;
	    }
	
	   public static Configuration getConfig(){
		   
		   return cfg;
	   }
	
}