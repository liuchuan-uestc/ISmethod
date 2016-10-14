package ReduceMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class Array{
	public double a;
	public String b;
}
// para1>=0&&para1<=0.2,para2>=0&&para2<=10
public class Shadowcut {
	public 	Map<String, Map<Integer, Double>> Usesh(Map<String, Map<Integer, Double>> map1,double para1,double para2){
		Tools tool=new Tools();
		 ArrayList al=tool.split(map1);
		 Map<String, Map<Integer, Double>> result=new TreeMap();
		 for(int i=0;i<al.size();i++){
			 Map<String, Map<Integer, Double>> temp1=(Map<String, Map<Integer, Double>>) al.get(i);
			 Map<String, Map<Integer, Double>> temp2=new TreeMap();
			 temp2.putAll(map1);
			 Map<String, Map<Integer, Double>> temp3= tool.chaji(temp2, temp1);
			 Map<String, Map<Integer, Double>> rtemp=Shadowmain(temp1,temp3,para1,para2);
			 result=tool.merge(result, rtemp);
		 }
		 return result;
	}
	public  Map<String, Map<Integer, Double>> Shadowmain(Map<String, Map<Integer, Double>> map1,Map<String, Map<Integer, Double>> map2,double para1,double para2){
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
	//	 Map<String, Map<Integer, Double>> result2=new TreeMap<String, Map<Integer, Double>> ();
		 List<Array> ar1=new ArrayList<Array>();
//		 List<Array> ar2=new ArrayList<Array>();
		 double max1=0;
		 double max2=0;
		 int n1=0;
		 int n2=0;
		 Set<Map.Entry<String,Map<Integer, Double>>> temp1Set = temp1.entrySet();
			for (Iterator<Map.Entry<String,Map<Integer, Double>>> it = temp1Set
					.iterator(); it.hasNext();) {
				Map.Entry<String,Map<Integer, Double>> me=it.next();
				Map<Integer, Double> temx=me.getValue();
				Map<Integer, Double> tem=tool.submap(temx, cen1.vector);
				double temresult=tool.shadow(tem, centv1);
				if(temresult>max1)max1=temresult;
				Array artemp=new Array();
				artemp.a=temresult;
				artemp.b=me.getKey();
				ar1.add(artemp);
				n1++;
			}
		
				double d=tool.getDistance(cen1.vector, cen2.vector);
				double sum=max1+max2;
				double para=para1*d+1/((n1+n2)/para2);
				if(sum<d){
					for(Array line:ar1){
						if((line.a>=(max1-para))&&(line.a<=max1)){
							result1.put(line.b, temp1.get(line.b));
						}
						
					}
				}else{
					for(Array line:ar1){
						if((line.a>=(d-max2-para))&&(line.a<=max1+para)){
							result1.put(line.b, temp1.get(line.b));
						}
						
					}
				}
				
				return result1;
	}

}
