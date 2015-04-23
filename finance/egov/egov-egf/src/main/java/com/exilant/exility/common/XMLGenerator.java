package com.exilant.exility.common;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
/**
 * @author raghu.bhandi
 * 
 * Generates XML string for an object.
 * This is a singleton, as there  is no need to have any instance variables
 */
public class XMLGenerator {
	// this is a singleton.
	private static XMLGenerator xmlGenerator = null;
	private static final Logger LOGGER=Logger.getLogger(XMLGenerator.class);
	/**
	 * 
	 */
	private XMLGenerator() {
	}

	public static XMLGenerator getInstance(){
		if (xmlGenerator == null) xmlGenerator = new XMLGenerator();
		return xmlGenerator;
	}

/*
 * Generate XML for any object. 
 * Note: this method is plymorphed for specific types of class
 */	
	public String toXML(Object object, String tag, String tabs){

		Class objclass = object.getClass();

		/*
		 * If the class designer is smart enough to write her own toXML(String tag, String tabs)... 
		 */

		 Class[] clsarr = {String.class, String.class};
		 Object[] objarr = {tag, tabs};
		 try{
			Method method = objclass.getMethod("toXML",clsarr);
			method.setAccessible(true);
			return (String)method.invoke(object,objarr);
		 }catch (Exception e){
			if(LOGGER.isInfoEnabled())     LOGGER.info("Failed to invoke method" +e.getMessage());
		 } // no sweat if you failed. Carry on...
		
		StringBuffer sbf = new StringBuffer(tabs);

		// set tag if required
		if (tag == null || tag.length() == 0){
			try{
				tag = ObjectGetSetter.get(object, "type").toString();  //try type field for tag
			}catch (Exception e){
				LOGGER.error("Error in toXML"+e.getMessage());
			}
		}
		if (tag == null || tag.length() == 0){
			tag = object.getClass().getName();
			tag = tag.substring(tag.lastIndexOf('.')+1);
			tag = tag.substring(0,1).toLowerCase() + tag.substring(1);
		}
		
		//produce "<tag "
		sbf.append('<');
		sbf.append(tag);
		sbf.append(' ');

		if (objclass.isPrimitive() || (objclass.getName().indexOf("java.lang.") == 0)){
			sbf.append("value=\"");
			sbf.append(object.toString());
			sbf.append("\"/>\n");
		}else if(objclass.isArray()){
			sbf.append(arrayToXML(object, tag, tabs));
		}else if(objclass.equals(HashMap.class)){
			sbf.append(hashMapToXML((HashMap)object, tag, tabs));
		}else if(objclass.equals(ArrayList.class)){
			sbf.append(arrayListToXML((ArrayList)object, tag, tabs));
		} else{ 
			HashMap values = ObjectGetSetter.getAll(object, false); 
			sbf.append(hashMapToXML(values, tag, tabs));
		}
		return sbf.toString();
	}

/*****************************************
 * Helper classes declared as private
 ****************************************/
 	
/*
 * returns XML  String for an array object with name as tag 
 */

	private String arrayToXML(Object object, String tag, String tabs){

		StringBuffer sbf = new StringBuffer();

		sbf.append("class=\"Array\" length=\"");
		int len = Array.getLength(object);
		sbf.append(len);
		sbf.append("\" ");
		if (len == 0){ 
			sbf.append("/>\n"); // end of tag and line
			return sbf.toString();
		}

		//Look at the type of elements the array contains
		Class cls = object.getClass().getComponentType();
		String newtag;
		if (cls.isArray()) newtag = "Array_" + cls.getComponentType();
		else {
			newtag = cls.getName();
			newtag = newtag.substring(newtag.lastIndexOf(".")+1);
			newtag = newtag.substring(0,1).toLowerCase() + newtag.substring(1);
		} 
		
		// produce class="class" and close tag
		sbf.append(" type=\"");
		sbf.append(newtag);
		sbf.append("\">\n");
   
		String newtabs = tabs+'\t'; //additional indent for children
   
		if (cls.isPrimitive() || (cls.getName().indexOf("java.lang.") == 0)){
	   
			// dump elements <class index="i" value="value"/>
			for(int i=0; i<len; i++){
				sbf.append(newtabs);
				sbf.append('<');
				sbf.append(newtag);
				sbf.append(" index=\"");
				sbf.append(i);
				sbf.append("\" value=\"");
				sbf.append(Array.get(object,i));
				sbf.append("\" />\n");
			}
	   }else{
			// generate XML for each element	
			XMLGenerator xg = XMLGenerator.getInstance();
			for(int i=0; i<len; i++){
				sbf.append(xg.toXML(Array.get(object, i),newtag, newtabs) );	   
			}
		}
		sbf.append(tabs);
		sbf.append('<');
		sbf.append(tag);
		sbf.append("/>\n");
		return sbf.toString();
	}

/*
 * returns XML string for an Object that is a HashMap.
 * It treats all primitive (primitive + String, Integer etc..) as attributes
 *  and other elements as 'children'
 */
	
	private String hashMapToXML(HashMap object, String tag, String tabs){
		
		StringBuffer sbf = new StringBuffer();
		boolean childfound = false;
		Iterator iterator;
		Map.Entry entry;
		String key;
		Object obj;
		Class cls;
		
		iterator = object.entrySet().iterator();
		while(iterator.hasNext()){
			entry = (Map.Entry)iterator.next();
			obj = entry.getKey();
			
			if ( !obj.getClass().equals(String.class)) continue; //key has to be string
			
			key = (String)obj;
			obj = entry.getValue();
			if (obj == null) continue;
			cls = obj.getClass();
			if (cls.isPrimitive() || (cls.getName().indexOf("java.lang.") == 0) ){ //attribute 
				sbf.append(key);
				sbf.append("=\"");
				sbf.append(entry.getValue().toString());
				sbf.append("\" ");
			}else{				
				childfound = true;
			}
		}
		
		if(childfound){
			//close line but not tag. ie. produce > and not />
			sbf.append(">\n");
			
			//lat us add XML for children
			String newtabs = tabs + '\t';
			XMLGenerator xg = XMLGenerator.getInstance();

			Iterator newIter = object.entrySet().iterator();
			Map.Entry newEntry;
			while(newIter.hasNext()){
				newEntry = (Map.Entry)newIter.next();
				obj = newEntry.getKey();
			
				if ( !obj.getClass().equals(String.class)) continue; //key has to be string
				key = (String)obj;
				obj = newEntry.getValue();
				if (obj == null)continue;
				if (obj.getClass().isPrimitive() || (obj.getClass().getName().indexOf("java.lang.") == 0) )continue; //attribute 
				sbf.append(xg.toXML(newEntry.getValue(), key, newtabs));
			}
			sbf.append(tabs);
			sbf.append('<');
			sbf.append(tag);
			sbf.append("/>\n");
			
		}else{ // no children. Simply end the tag  with />
			sbf.append("/>\n");
		}
		return sbf.toString();
	}

	/*
	 * returns XML string for an Object that is a HashMap.
	 * It treats all primitive (primitive + String, Integer etc..) as attributes
	 *  and other elements as 'children'
	 */
	
	private String arrayListToXML(ArrayList object, String tag, String tabs){
		
		int size = object.size();
		StringBuffer sbf = new StringBuffer();
		
		//produce header tag as <tag class="ArrayList" size="size>;
		sbf.append(" class=\"ArrayList\" size=\""); //problem with java. array-length, arraylist-size
		sbf.append(size);
		sbf.append("\" ");
		
		if (size == 0){
			sbf.append("/>\n");
		}else{
			sbf.append(">\n");
			
			XMLGenerator xg =  XMLGenerator.getInstance();
			String newtabs = tabs + '\t';
			for (int i=0; i<size; i++){
				sbf.append(xg.toXML(object.get(i),null, newtabs));
			}
			sbf.append(tabs);
			sbf.append('<');
			sbf.append(tag);
			sbf.append("/>\n");
		}
		return sbf.toString();
	}
}
