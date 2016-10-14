package ReduceMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Centrcut {
	public Map<String, Map<Integer, Double>> Usecen(Map<String, Map<Integer, Double>> map1){
		Tools tool=new Tools();
		 ArrayList al=tool.split(map1);
		 
		 Map<String, Map<Integer, Double>> result=new TreeMap();
		 for(int i=0;i<al.size();i++){
			 Map<String, Map<Integer, Double>> temp1=(Map<String, Map<Integer, Double>>) al.get(i);
			 Map<String, Map<Integer, Double>> temp2=new TreeMap();
			 temp2.putAll(map1);
			 Map<String, Map<Integer, Double>> temp3= tool.chaji(temp2, temp1);
			 Map<String, Map<Integer, Double>> rtemp=Centrmain(temp1,temp3);
			 result=tool.merge(result, rtemp);
		 }
		 return result;
		
	}
	
	public  Map<String, Map<Integer, Double>> Centrmain(Map<String, Map<Integer, Double>> map1,Map<String, Map<Integer, Double>> map2){
		Tools tool=new Tools();

		 Map<String, Map<Integer, Double>> temp1=map1;
		 Map<String, Map<Integer, Double>> temp2=map2;
		// System.out.println("++++++"+temp1.size());
//		 tool.writevocter("./Debug/temp1.txt", temp1);
//		 tool.writevocter("./Debug/temp2.txt", temp2);
		 Centrev cen1=tool.getCentre(temp1);
		 Centrev cen2=tool.getCentre(temp2);
		 Map<Integer, Double> centv1=tool.submap(cen2.vector, cen1.vector);
		 Map<Integer, Double> centv2=tool.submap(cen1.vector, cen2.vector);
		 Map<String, Map<Integer, Double>> result1=new TreeMap<String, Map<Integer, Double>> ();
//		 Map<String, Map<Integer, Double>> result2=new TreeMap<String, Map<Integer, Double>> ();
		 Set<Map.Entry<String,Map<Integer, Double>>> temp1Set = temp1.entrySet();
			for (Iterator<Map.Entry<String,Map<Integer, Double>>> it = temp1Set
					.iterator(); it.hasNext();) {
				Map.Entry<String,Map<Integer, Double>> me=it.next();
				Map<Integer, Double> temx=me.getValue();
				Map<Integer, Double> tem=tool.submap(temx, cen1.vector);
				double temresult=tool.pointmul(centv1, tem);
				if(temresult>=0){
					result1.put(me.getKey(), temx);
				}
			}
		return result1;
	}

}
