package ReduceMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class CNN{

	public Map<String, Map<Integer, Double>> CNNmain(Map<String, Map<Integer, Double>> map1){
		point [] X=new point[map1.size()];
		int [] XFlag=new int[map1.size()];
		//int count=map1.size();
		
		point [] S=new point[map1.size()];
		int [] SFlag=new int[map1.size()];
		int count=0;
		int finish=1;
		int Selected=0;
		
		Tools tool=new Tools();

		Set<Map.Entry<String,Map<Integer, Double>>> map1Set = map1.entrySet();
		for (Iterator<Map.Entry<String,Map<Integer, Double>>> it = map1Set.iterator(); it.hasNext();) {
			Map.Entry<String,Map<Integer, Double>> me=it.next();
			point p=new point();
			p.name=me.getKey();
			p.vector=me.getValue();
			X[count]=p;
			XFlag[count]=1;
			S[count]=p;
			SFlag[count]=0;
			count++;						
		}
		
		XFlag[0]=0;
		SFlag[0]=1;
		
		while(1==finish){
			finish=0;
			Selected=0;
			double Temp = 0.0;
			double dismin_value = 0.0;
			int dismin_index = -1;
			//System.err.println("dismin_index = "+dismin_index);
			for(int i=0;i<count;i++){
				dismin_value = 0.0;
				if(1==XFlag[i]){
					for(int j=0;j<count;j++){
						if((i!=j)&&(1==SFlag[j])){
							Temp=tool.computeSim(X[i].vector, S[j].vector);
							if(Temp>=dismin_value){
								dismin_value = Temp;
								dismin_index = j;
							}
						}
					}
					
					if(-1!=dismin_index){
						String[] temp1=X[i].name.split("_");	
						String[] temp2=S[dismin_index].name.split("_");
						if(temp1[0].equals(temp2[0])){
							//do noting;
						}else{
							XFlag[i]=0;
							SFlag[i]=1;
							finish=1;
						}
					}
					else{
						System.err.println("dismin_index = "+dismin_index);
					}
				}else{
					Selected++;
				}
			}
			System.out.println("Selected = "+Selected);
		}
		
		//筛选每个点
		Map<String, Map<Integer, Double>> result=new TreeMap();
		Map<String, Integer> CateName=new TreeMap();
		int selectedNum = 0;
		for(int j=0;j<count;j++){
			if(1==SFlag[j]){
				result.put(S[j].name, S[j].vector);
				selectedNum++;
				String[] temp1=S[j].name.split("_");
				if(CateName.containsKey(temp1[0])){
					CateName.put(temp1[0], CateName.get(temp1[0])+1);
				}else{
					CateName.put(temp1[0], 1);
				}
			}
		}
		System.out.println("CNNmain  selectedNum = "+selectedNum);
		return result;
	}
}

