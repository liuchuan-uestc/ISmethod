package ReduceMethod;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Lstsrs {
	public Map<String, Map<Integer, Double>> Usels(Map<String, Map<Integer, Double>> map1,int teznum,double m,double limit,double lim){
		Tools tool=new Tools();
		 ArrayList al=tool.split(map1);
		 Map<String, Map<Integer, Double>> result=new TreeMap();
		 for(int i=0;i<al.size();i++){
			 Map<String, Map<Integer, Double>> temp1=(Map<String, Map<Integer, Double>>) al.get(i);
			 for(int j=i+1;j<al.size();j++){		 
				 Map<String, Map<Integer, Double>> temp2=(Map<String, Map<Integer, Double>>) al.get(j);
				 Map<String, Map<Integer, Double>> temp3=tool.merge(temp1, temp2);
				 Map<String, Map<Integer, Double>> rtemp=Lsmain(temp3,teznum,m,limit,lim);
				 result=tool.merge(result, rtemp);
			 }
		 }
		 return result;
		
	}
	public Map<String, Map<Integer, Double>> Lsmain(Map<String, Map<Integer, Double>> map1,int teznum,double m,double limit,double lim){
		Tools tool=new Tools();
		
		ArrayList al=tool.split(map1);
		
		 Map<String, Map<Integer, Double>> m1=(Map<String, Map<Integer, Double>>) al.get(0);
	
		 Map<String, Map<Integer, Double>> m2=(Map<String, Map<Integer, Double>>) al.get(1);

		 Fcm f2=getFcm(m2,teznum,m,limit);
		 Fcm f1=getFcm(m1,teznum,m,limit);
		 Map<String, Map<Integer, Double>> result=getresult(f1,f2,map1,lim);
		return result;
	}
	
	
	public  Fcm  getFcm(Map<String, Map<Integer, Double>> map1,int teznum,double m,double limit){		
		int cata=25;
		int numpattern=map1.size();
		//System.out.println("+++"+numpattern);
		Fcm fcm=new Fcm();
		ArrayList<point> [] result=new ArrayList [cata];
		for(int i=0; i<cata;i++){
			result[i]=new ArrayList<point>();
		}
		Tools tool=new Tools();
		if (cata >= numpattern || m <= 1) System.out.println("参数出错");
		
		double [][] u=new double[cata][numpattern];
		for(int j=0;j<numpattern;j++){
			//u[0][j]=1;
			for(int i=0;i<cata;i++){
				
				 u[i][j]=0;
			}
		}
		point [] pn=new point[numpattern];
		double [] min=new double[teznum];
		double [] max=new double[teznum];
		for(int i=0;i<teznum;i++){
			min[i]=100000000;
			max[i]=0;
		}
		int count=0;
		 Set<Map.Entry<String,Map<Integer, Double>>> map1Set = map1.entrySet();
			for (Iterator<Map.Entry<String,Map<Integer, Double>>> it = map1Set
					.iterator(); it.hasNext();) {
				Map.Entry<String,Map<Integer, Double>> me=it.next();
				//System.out.println("+++"+me.getKey());
				point p=new point();
				p.name=me.getKey();
				p.vector=me.getValue();
				for(int i=0;i<teznum;i++){
					if(p.vector.containsKey(i)){
						if(p.vector.get(i)>max[i])max[i]=p.vector.get(i);
						if(p.vector.get(i)<min[i])min[i]=p.vector.get(i);
					}
				}
				pn[count]=p;
				count++;						
			}
			//归一化
			for(int i=0; i<count;i++){
				for(int j=0;j<teznum;j++){
					if(pn[i].vector.containsKey(j)){
						double tem1=pn[i].vector.get(j);
						if(tem1==min[j])
						{tem1=0;}
						else{
						tem1=(tem1-min[j])/(max[j]-min[j]);
						}
						pn[i].vector.put(j, tem1);
					}
				}
			}
		
		double r1=0,r2=0,r3=0;
		double temp4=2/(m-1);
		int countnum=0;
		Map<Integer, Double> [] c=new TreeMap[cata];
		
		for(int i=0;i<cata;i++){
			c[i]=pn[(int)(1+Math.random()*(count-1))].vector;
		}
		
		while(true){
			countnum++;
			 //更新隶属矩阵
			// System.out.println("+++计算u矩阵+");
			// long bs = Calendar.getInstance().getTimeInMillis();
			 
		
			
			
				 for(int j=0;j<numpattern;j++){
					double temp1=0,temp2=0,temp3=0;
					 for(int i=0;i<cata;i++){
					 for(int k=0;k<cata;k++ ){
						 temp1=tool.getDistance(c[i], pn[j].vector);
						 temp2=tool.getDistance(c[k], pn[j].vector);
						 if(temp1<0.000000001){
							 temp1=0.000000001;
						 }
						 if(temp2<0.000000001){
							 temp2=0.000000001;
						 }
						 temp3 +=Math.pow((temp1/temp2),temp4);
					 }
					 u[i][j]=1/temp3;
					 temp1=0;
					 temp2=0;
					 temp3=0;
					// System.out.println(pn[j].name+"++++"+u[i][j]);
				 }
				 
			 }
			//	 System.out.println((Calendar.getInstance().getTimeInMillis()-bs)/1000);
//				 String filename="./Debug/u"+countnum+".txt";
//					
//				 File file=new File(filename);
//				 try {
//						FileWriter Writer = new FileWriter(file);
//						 for(int i=0;i<cata;i++){
//							 for(int j=0;j<numpattern;j++){
//								 Writer.write(String.valueOf(u[i][j]));
//								 Writer.write(",");
//							 }
//							 Writer.write("\n");
//						 }
//				 }catch(Exception e){
//					 
//				 }
		 //更新聚类中心
		// System.out.println("+++计算C值+");
		//  bs = Calendar.getInstance().getTimeInMillis();
		 
		 for(int i=0;i<cata;i++){
			 Map<Integer, Double> tmap1=new TreeMap<Integer, Double>();
			 Map<Integer, Double> summap=new TreeMap<Integer, Double>();
			 double temp1=0,temp2=0,temp3=0;
			 for(int j=0;j<numpattern;j++){
				 temp1=Math.pow(u[i][j], m);
				 tmap1=tool.onemul(temp1, pn[j].vector);
				 summap=tool.addmap(summap, tmap1);
				 temp2 +=temp1;
				// System.out.println(temp2);
			 }
			 temp3=(1/temp2);
		//	 System.out.println("+++++++temp3+"+temp3);
			 c[i]=tool.onemul(temp3, summap);
		 }
		 Map<String, Map<Integer, Double>> mapcent=new TreeMap();
//		 for(int i=0;i<cata;i++){
//			 mapcent.put(String.valueOf(i), c[i]);
//			 
//		 }
//		 tool.writevocter("./Debug/c"+countnum+".txt", mapcent);
		// System.out.println((Calendar.getInstance().getTimeInMillis()-bs)/1000);
		 //计算目标函数
		// System.out.println("+++计算目标函数+");
		// bs = Calendar.getInstance().getTimeInMillis();
		
		 for(int i=0;i<cata;i++){
			 for(int j=0;j<numpattern;j++){
				double temp1=0,temp2=0;
				 temp1=Math.pow(u[i][j], m);
				 temp2=tool.getDistance(c[i], pn[j].vector);
			//	 System.out.println("+++temp1+"+temp1);
			//	 System.out.println("+++temp2+"+temp2);
				 r1+=(temp1*temp2*temp2);
		//		 System.out.println("+++r1+"+r1);
			 }
		 }
		// System.out.println((Calendar.getInstance().getTimeInMillis()-bs)/1000);
		 r3=Math.abs(r1-r2);
//		 System.out.println("+++r1+"+r1);
//		 System.out.println("+++差值+"+r3);
		 if(r3<limit)break;
		 r2=r1;
		 r1=0;
//		 if(r3<limit){
//			 fcm.c=c;
//			 break;}
		 
		// System.out.println("+++计算u矩阵+");
		// bs = Calendar.getInstance().getTimeInMillis();
		 
	
		
		// System.out.println((Calendar.getInstance().getTimeInMillis()-bs)/1000);
		}
	//	System.out.println("+++开始输出+");
		for(int i=0; i<c.length;i++){
			for(int j=0;j<teznum;j++){
				if(c[i].containsKey(j)){
					double tem1=c[i].get(j);
					tem1=tem1*(max[j]-min[j])+min[j];
					c[i].put(j, tem1);
				}
			}
		}
		for(int i=0; i<count;i++){
			for(int j=0;j<teznum;j++){
				if(pn[i].vector.containsKey(j)){
					double tem1=pn[i].vector.get(j);
					tem1=tem1*(max[j]-min[j])+min[j];
					pn[i].vector.put(j, tem1);
				}
			}
		}

		
		fcm.c=c;
	//	fcm.pn=result;
		return fcm;
	}
	public Map<String, Map<Integer, Double>> getresult(Fcm f1,Fcm f2,Map<String, Map<Integer, Double>> map1,double lim){
		point [] pn=new point[map1.size()];
		Map<String, Map<Integer, Double>> result=new TreeMap();
		Tools tool=new Tools();
		int count=0;
		 Set<Map.Entry<String,Map<Integer, Double>>> map1Set = map1.entrySet();
			for (Iterator<Map.Entry<String,Map<Integer, Double>>> it = map1Set
					.iterator(); it.hasNext();) {
				Map.Entry<String,Map<Integer, Double>> me=it.next();
				//System.out.println("+++"+me.getKey());
				point p=new point();
				p.name=me.getKey();
				p.vector=me.getValue();
				pn[count]=p;
				count++;						
			}
			int k=11;
			//System.out.println(count);
			 String filename="./Debug/ux"+".txt";
				
//			 File file=new File(filename);
//			 try {
//					FileWriter Writer = new FileWriter(file);
					
			
		for(int i=0;i<count;i++){
			int mcount=0;
			double [][] d=new double[k][2];
			for(int j=0;j<k;j++){
				d[j][0]=1000;
				d[j][1]=0;
				
			}
			//System.out.println(pn[i].name);
			for(int n=0;n<f1.c.length;n++){
				
				double temp1=tool.getDistance(pn[i].vector, f1.c[n]);
				double temp2=tool.getDistance(pn[i].vector, f2.c[n]);
//				Writer.write(String.valueOf(temp1)+",");
//				Writer.write(String.valueOf(temp2)+"\n");
				if(temp1>temp2){
					for(int m=0;m<k;m++){
						if(d[m][0]>temp2){
							d[m][0]=temp2;	
							d[m][1]=1;
							//System.out.println("+++temp1+"+temp);
							break;
						}
					}
				}else {
					for(int m=0;m<k;m++){
						if(d[m][0]>temp1){
							d[m][0]=temp1;	
							d[m][1]=0;
							//System.out.println("+++temp1+"+temp);
							break;
						}
					}
				}
				
			}
			//System.out.println(f1.c.length);
			
			double numc=0;
			for(int l=0;l<k;l++){
			//	System.out.println("++"+d[l][1]);
				if(d[l][1]==0)numc++;
			}
//			System.out.println("+++count+"+numc);
			double temp1=k;
			double temp=Math.abs((temp1-2*numc)/temp1);
//			System.out.println(temp);
			if(temp<lim){
				result.put(pn[i].name, pn[i].vector);
			}

		
	}
//			 }catch(Exception e){
//				 
//			 }
//		System.out.println(result.size());
		tool.writevocter("./Debug/result.txt", result);
		return result;
	}
}