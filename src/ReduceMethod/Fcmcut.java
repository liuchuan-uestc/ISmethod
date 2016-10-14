package ReduceMethod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
class point{
	public String name;
	public  Map<Integer, Double> vector; 
	public int mark;
}
class Fcm{
	public Map<Integer, Double>[] c;
	public ArrayList<point> [] pn;
}
/**
 * 从txt读取文本向量
 * @param Map<String, Map<Integer, Double>>
 * 		   	文本集
 * @param dim
 *            向量的维数
 * @param cata
 *            分类数
 * @param m
 * 			加权指数
 * @param maxcycle
 * 			最大循环次数
 * @param limit
 * 			跳出循环的阀值
 * @return Dataset 
 * @throws Exception
 */
public class Fcmcut {
	public Map<String, Map<Integer, Double>> Usefc(Map<String, Map<Integer, Double>> map1,int cata,int maxcycle){
		
				 Map<String, Map<Integer, Double>> rtemp=Fcmcutmain(map1,cata,maxcycle);
		
		 
		 return rtemp;
		
	}
	public Map<String, Map<Integer, Double>> Fcmcutmain(Map<String, Map<Integer, Double>> map1,int cata,int maxcycle){

		Fcm f=getFcm(map1,cata,maxcycle);
	
		Map<String, Map<Integer, Double>> result=new TreeMap();

		for(int i=0;i<cata;i++){
			if(f.pn[i].size()>0){
			//System.out.println(f.pn[i].get(0).name);
			String tempstr=f.pn[i].get(0).name;
		//	System.out.println(tempstr);
			String name=tempstr.split("_")[0];
			
			for(int j=0;j<f.pn[i].size();j++){
				tempstr=f.pn[i].get(j).name;
				if(!name.equals(tempstr.split("_")[0])){
					for(int k=0;k<f.pn[i].size();k++){
						result.put(f.pn[i].get(k).name, f.pn[i].get(k).vector);
					}
					break;
				}
			}
			//if(mark)result.put(f.pn[i].get(0).name.substring(0,4)+"cen"+String.valueOf(i), f.c[i]);
			}
		}
		System.out.println("  "+result.size());
		return result;
		
	}
	public  Fcm  getFcm(Map<String, Map<Integer, Double>> map1,int cata,int maxcycle){		
		int numpattern=map1.size();
		//System.out.println("+++"+numpattern);
		Fcm fcm=new Fcm();
		ArrayList<point> [] result=new ArrayList [cata];
		for(int i=0; i<cata;i++){
			result[i]=new ArrayList<point>();
		}
		Tools tool=new Tools();
		if (cata >= numpattern ) System.err.println("getFcm err");
		
		double [][] u=new double[numpattern][cata]; 
	
		int[] assignMeans = new int[numpattern];
		point [] pn=new point[numpattern];

		int count=0;
		 Set<Map.Entry<String,Map<Integer, Double>>> map1Set = map1.entrySet();
			for (Iterator<Map.Entry<String,Map<Integer, Double>>> it = map1Set
					.iterator(); it.hasNext();) {
				Map.Entry<String,Map<Integer, Double>> me=it.next();
				//System.out.println("+++"+me.getKey());
				point p=new point();
				p.name=me.getKey();
				p.vector=me.getValue();
//				System.out.println("++++");

				pn[count]=p;
				count++;						
			}

		
		int countnum=0;
		Map<Integer, Double> [] c=new TreeMap[cata];	
		for(int i=0;i<cata;i++){
			c[i]=pn[(int)(1+Math.random()*(count-1))].vector;
		}
		
		while(countnum<maxcycle){
			 countnum++;
			 System.out.println("Computing matrix");
				for (int i = 0; i < cata; i++) {
					for (int j = 0; j < numpattern; j++) {
						//u[j][i] = tool.getDistance(pn[j].vector, c[i]);
						u[j][i] = 1-tool.computeSim(pn[j].vector, c[i]);
					}
				}
				int[] nearestMeans = new int[numpattern];
				for (int j = 0; j < numpattern; j++) {
					nearestMeans[j] = findNearestMeans(u, j);
				}
				int okCount = 0;
				for (int i = 0; i < numpattern; i++) {
					if (nearestMeans[i] == assignMeans[i])
						okCount++;
				}
				System.out.println("okCount = " + okCount);
				if (okCount == numpattern )
					break;
				for(int i=0;i<cata;i++)
				result[i].clear();
				for (int i = 0; i < numpattern; i++) {
					assignMeans[i] = nearestMeans[i];
					result[nearestMeans[i]].add(pn[i]);
				}

				for (int i = 0; i < cata; i++) {
					if (result[i].isEmpty()) {
						continue;
					}
				c[i]=tool.getCeter(result[i]);
				}
			
		

		}
		fcm.pn=result;
		return fcm;
	}
	/**
	 * 找出距离当前点最近的聚类中心
	 * 
	 * @param double[][] 点到所有聚类中心的距离
	 * @return i 最近的聚类中心的序 号
	 * @throws IOException
	 */
	private int findNearestMeans(double[][] distance, int m) {
		// TODO Auto-generated method stub
		double minDist = 10;
		int j = 0;
		for (int i = 0; i < distance[m].length; i++) {
			if (distance[m][i] < minDist) {
				minDist = distance[m][i];
				j = i;
			}
		}
		return j;
	}

}
