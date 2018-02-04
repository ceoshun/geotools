package com.cs.geotools;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

public class isPointInPoly {
	public static void main(String[] args) {
		try {
			String wktPoly = "POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"; //请自行搜素了解wkt格式  
			String wktPoint = "POINT (30 30)";    
			WKTReader reader = new WKTReader(JTSFactoryFinder.getGeometryFactory());  
			GeometryFactory geometryFactory =  JTSFactoryFinder.getGeometryFactory(null);  
			Geometry point =  reader.read(wktPoint);  
			Geometry poly = reader.read(wktPoly);  
			boolean rtn = poly.contains(point); //返回true或false  
			System.out.println(rtn);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
