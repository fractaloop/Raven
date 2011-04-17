/**
 * 
 */
package raven.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author chester
 *
 */
public class Cloner {

	@SuppressWarnings("unchecked")
	public static <T> T Clone(T toClone) {
		
		ObjectOutputStream oos = null;
	      ObjectInputStream ois = null;
	      try
	      {
	         ByteArrayOutputStream bos = 
	               new ByteArrayOutputStream(); // A
	         oos = new ObjectOutputStream(bos); // B
	         // serialize and pass the object
	         oos.writeObject(toClone);   // C
	         oos.flush();               // D
	         ByteArrayInputStream bin = 
	               new ByteArrayInputStream(bos.toByteArray()); // E
	         ois = new ObjectInputStream(bin);                  // F
	         
	         // return the new object
	         return (T) ois.readObject(); // G
	      }
	      catch(IOException e) {
	         System.out.println("Exception in ObjectCloner = " + e);
	      }
	      catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	      finally
	      {
	         try {
				oos.close();
		        ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	      }
	      
	      return toClone;
		
	}
	
}
