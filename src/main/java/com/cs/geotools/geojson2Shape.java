package com.cs.geotools;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import com.amazonaws.util.json.JSONObject;

public class geojson2Shape {
	private static CommonMethod cm = new CommonMethod();  
	
	/** 
     * geojson转换为shp文件 
     * @param jsonPath 
     * @param shpPath 
     * @return 
     */  
    public Map geojson2Shape(String jsonPath, String shpPath){  
        Map map = new HashMap();  
        GeometryJSON gjson = new GeometryJSON();  
        try{  
            String strJson = cm.getFileContent(jsonPath);  
            JSONObject json = new JSONObject(strJson);  
            JSONArray features = (JSONArray) json.get("features");  
            JSONObject feature0 = new JSONObject(features.get(0).toString());  
            System.out.println(feature0.toString());  
            String strType = ((JSONObject)feature0.get("geometry")).getString("type").toString();  
              
            Class<?> geoType = null;  
            switch(strType){  
                case "Point":  
                    geoType = Point.class;  
                case "MultiPoint":  
                    geoType = MultiPoint.class;  
                case "LineString":  
                    geoType = LineString.class;  
                case "MultiLineString":  
                    geoType = MultiLineString.class;  
                case "Polygon":  
                    geoType = Polygon.class;  
                case "MultiPolygon":  
                    geoType = MultiPolygon.class;  
            }  
            //创建shape文件对象  
            File file = new File(shpPath);  
            Map<String, Serializable> params = new HashMap<String, Serializable>();  
            params.put( ShapefileDataStoreFactory.URLP.key, file.toURI().toURL() );  
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);  
            //定义图形信息和属性信息  
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();  
            tb.setCRS(DefaultGeographicCRS.WGS84);  
            tb.setName("shapefile");  
            tb.add("the_geom", geoType);  
            tb.add("POIID", Long.class);  
            ds.createSchema(tb.buildFeatureType());  
            //设置编码  
            Charset charset = Charset.forName("GBK");  
            ds.setCharset(charset);  
            //设置Writer  
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);  
              
            for(int i=0,len=features.length();i<len;i++){  
                String strFeature = features.get(i).toString();  
                Reader reader = new StringReader(strFeature);  
                SimpleFeature feature = writer.next();  
                feature.setAttribute("the_geom",gjson.readMultiPolygon(reader));  
                feature.setAttribute("POIID",i);  
                writer.write();  
            }  
            writer.close();  
            ds.dispose();  
            map.put("status", "success");  
            map.put("message", shpPath);  
        }  
        catch(Exception e){  
            map.put("status", "failure");  
            map.put("message", e.getMessage());  
            e.printStackTrace();  
        }  
        return map;  
    }  
    
    /** 
     * shp转换为Geojson 
     * @param shpPath 
     * @return 
     */  
    public Map shape2Geojson(String shpPath, String jsonPath){  
        Map map = new HashMap();  
          
        FeatureJSON fjson = new FeatureJSON();  
          
        try{  
            StringBuffer sb = new StringBuffer();  
            sb.append("{\"type\": \"FeatureCollection\",\"features\": ");  
              
            File file = new File(shpPath);  
            ShapefileDataStore shpDataStore = null;  
              
            shpDataStore = new ShapefileDataStore(file.toURL());  
            //设置编码  
            Charset charset = Charset.forName("GBK");  
            shpDataStore.setCharset(charset);  
            String typeName = shpDataStore.getTypeNames()[0];  
            SimpleFeatureSource featureSource = null;  
            featureSource =  shpDataStore.getFeatureSource (typeName);  
            SimpleFeatureCollection result = featureSource.getFeatures();  
            SimpleFeatureIterator itertor = result.features();  
            JSONArray array = new JSONArray();  
            while (itertor.hasNext())  
            {  
                SimpleFeature feature = itertor.next();  
                StringWriter writer = new StringWriter();  
                fjson.writeFeature(feature, writer);  
                JSONObject json = new JSONObject(writer.toString());  
                array.put(json);  
            }  
            itertor.close();  
            sb.append(array.toString());  
            sb.append("}");  
              
            //写入文件  
            cm.append2File(jsonPath, sb.toString());  
              
            map.put("status", "success");  
            map.put("message", sb.toString());  
        }  
        catch(Exception e){  
            map.put("status", "failure");  
            map.put("message", e.getMessage());  
            e.printStackTrace();  
              
        }  
        return map;  
    }  
    
	public static void main(String[] args) {
		FileFormat fileFormat = new FileFormat();  
        long start = System.currentTimeMillis();  
        String shpPath = "/Users/lzugis/Documents/chinadata/cityboundry.shp";  
        String jsonPath = "/Users/lzugis/Documents/chinadata/cityboundry.geojson";  
        Map map = fileFormat.shape2Geojson(shpPath, jsonPath);  
          
//      String shpPath = "D:/data/beijing/China43262.shp";  
//      String jsonPath = "D:/data/beijing/China4326.geojson";  
//      Map map = fileFormat.geojson2Shape(jsonPath, shpPath);  
          
        System.out.println(jsonPath+",共耗时"+(System.currentTimeMillis() - start)+"ms");  
	}
}
