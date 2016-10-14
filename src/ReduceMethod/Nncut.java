package ReduceMethod;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Nncut {
	public Map<String, Map<Integer, Double>> UseNn(Map<String, Map<Integer, Double>> map1){		
		Tools tool=new Tools();
		 ArrayList al=tool.split(map1);
		 Map<String, Map<Integer, Double>> result=new TreeMap();
		 for(int i=0;i<al.size();i++){
			 Map<String, Map<Integer, Double>> temp1=(Map<String, Map<Integer, Double>>) al.get(i);
			 for(int j=i+1;j<al.size();j++){		 
				 Map<String, Map<Integer, Double>> temp2=(Map<String, Map<Integer, Double>>) al.get(j);
				 Map<String, Map<Integer, Double>> temp3=tool.merge(temp1, temp2);
				 Map<String, Map<Integer, Double>> rtemp= Nncutmain(temp3);
				 result=tool.merge(result, rtemp);
			 }
		 }
		 return result;
		
	}
	public Map<String, Map<Integer, Double>> Nncutmain(Map<String, Map<Integer, Double>> map1){
		point [] pn=new point[map1.size()];
		Tools tool=new Tools();
		int count=0;
		
		 Set<Map.Entry<String,Map<Integer, Double>>> map1Set = map1.entrySet();
			for (Iterator<Map.Entry<String,Map<Integer, Double>>> it = map1Set
					.iterator(); it.hasNext();) {
				Map.Entry<String,Map<Integer, Double>> me=it.next();
				
				point p=new point();
				p.name=me.getKey();
				p.vector=me.getValue();
				pn[count]=p;
				count++;						
			}
			System.out.println("+++"+count);
	
			double [] d_value=new double[count];
			int [] d_index=new int[count];
			for(int i=0;i<count;i++){
				d_value[i] = 0.0;
				d_index[i] = 0;
			}
			
			double Temp = 0.0;
			for(int i=0;i<count;i++){
				for(int j=0;j<count;j++){
					if(i!=j){
						Temp=tool.computeSim(pn[i].vector, pn[j].vector);
						if(Temp>d_value[i]){
							d_value[i] = Temp;
							d_index[i] = j;
						}
					}
				}
			}

			Map<String, Map<Integer, Double>> result=new TreeMap();

			for(int i=0;i<count;i++){
				String[] temp1=pn[i].name.split("_");		
				String[] temp2=pn[d_index[i]].name.split("_");
//				System.out.println(pn[i].name+"+"+pn[mark].name);
				if(temp1[0].equals(temp2[0]))result.put(pn[i].name, pn[i].vector);
				//System.out.println("+++"+i+"++++"+mark);
			}
		//	System.out.println("+++"+result.size());
			return result;
	}

}
