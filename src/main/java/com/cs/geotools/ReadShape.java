package com.cs.geotools;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

public class ReadShape {
	/**
	 * 读Shape文件，并且打印
	 * @param args
	 */
	public static void main(String[] args) {
        ShapefileDataStore shpDataStore = null;  
        Calendar startTime = Calendar.getInstance();  
        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();  
        try{  
        	
        	File file = JFileDataStoreChooser.showOpenFile("shp", null);
            if (file == null) {
                return;
            }

            //File file = new File (shpPath);  
            shpDataStore = new ShapefileDataStore(file.toURL());  
            //设置字符编码  
            Charset charset = Charset.forName("GBK");  
            shpDataStore.setCharset(charset);  
            String typeName = shpDataStore.getTypeNames()[0];  
            SimpleFeatureSource featureSource = null;  
            featureSource =  shpDataStore.getFeatureSource (typeName);  
            SimpleFeatureCollection result = featureSource.getFeatures();  
            SimpleFeatureIterator itertor = result.features();  
            while (itertor.hasNext())  
            {  
                Map<String,Object> data  = new HashMap<String, Object>();  
                SimpleFeature feature = itertor.next();  
                Collection<Property> p = feature.getProperties();  
                Iterator<Property> it = p.iterator();  
                while(it.hasNext()) {  
                    Property pro = it.next();  
                    String field = pro.getName().toString();  
                    String value = pro.getValue().toString();  
                    field = field.equals("the_geom")?"wkt":field;  
                    //System.out.println("field" + field + "  value:" + value + "field" + field);
                    data.put(field, value);  
                }  
                list.add(data);  
            }  
            System.out.println(list); 
            Calendar endTime = Calendar.getInstance();  
            int day = endTime.get(Calendar.DAY_OF_MONTH) - startTime.get(Calendar.DAY_OF_MONTH);  
            int hour = endTime.get(Calendar.HOUR_OF_DAY) - startTime.get(Calendar.HOUR_OF_DAY);  
            int minute = endTime.get(Calendar.MINUTE) - startTime.get(Calendar.MINUTE);  
            int second = endTime.get(Calendar.SECOND) - startTime.get(Calendar.SECOND);
            itertor.close();  
        } catch (Exception e){
        	e.printStackTrace();
        }
	}
}
