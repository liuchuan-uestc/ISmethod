package ReduceMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import PreProcess.DataPreProcess;


public class SE{

	public static Map<String, Map<Integer, Double>> SEMain(Map<String, Map<Integer, Double>> TrainFileMapVsm, 
														   double Phi, double maxphi, double percent, double delt){
		System.out.println("SelectFileAlgorithmIncreasePhi    Phi : "+Phi+", delt: "+delt);
		int  Tsize = TrainFileMapVsm.size();
		Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();

		Set<Map.Entry<String, Map<Integer, Double>>> TrainFileMapVsmSet = TrainFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String Cate = me.getKey().split("_")[0];
			if(CateMap.containsKey(Cate)){
				if(CateMap.get(Cate).containsKey(me.getKey())){
					int count = CateMap.get(Cate).get(me.getKey());
					CateMap.get(Cate).put(me.getKey(), count+1);
				}
				else{
					CateMap.get(Cate).put(me.getKey(),1);
				}
			}
			else{
			    Map<String, Integer> tempMap = new TreeMap<String, Integer>();
				tempMap.put(me.getKey(),1);
				CateMap.put(Cate, tempMap);
			}
		}

		int Ksize = 0;
		int j = 0;
		int i = 0;
		int[] CateNum = new int[CateMap.size()];

		Set<Map.Entry<String, Map<String, Integer>>> CateMapSet = CateMap.entrySet();
		for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, Integer>> me = it.next();
			CateNum[i++] = me.getValue().size();
			Set<Map.Entry<String, Integer>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();
				if(CateCenterMap.containsKey(me.getKey())){
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						if(CateCenterMap.get(me.getKey()).containsKey(me3.getKey())){
							double p = CateCenterMap.get(me.getKey()).get(me3.getKey());
							CateCenterMap.get(me.getKey()).put(me3.getKey(),p+me3.getValue());
						}else{
							CateCenterMap.get(me.getKey()).put(me3.getKey(), me3.getValue());
						}
					}
				}
				else{
					Map<Integer, Double> tempMap = new TreeMap<Integer, Double>();
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						tempMap.put(me3.getKey(), me3.getValue());
					}
					if(Ksize<tempMap.size()){
						Ksize = tempMap.size();
					}
					CateCenterMap.put(me.getKey(), tempMap);
				}			
			}
		}
		
		i = 0;
		String[] CateName = new String[CateCenterMap.size()];
		Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet = CateCenterMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CateCenterMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			CateName[i] = me.getKey();
			Set<Map.Entry<Integer, Double>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				CateCenterMap.get(me.getKey()).put(me2.getKey(),me2.getValue()/CateNum[i]);
			}
			i++;
		}
		
		Map<String, Map<Integer, Double>> CateCenterMapNew  = new TreeMap<String, Map<Integer, Double>>();
		CateCenterMapNew = DataPreProcess.copyMaptoMap(CateCenterMap); 
		//System.out.println(" TrainFileMapVsm     : "+TrainFileMapVsm.size());
		SEtool tl = new SEtool(CateMap, CateCenterMapNew, CateCenterMap, TrainFileMapVsm);
		tl.SetDelt(delt);
		return tl.SelectFileAlgorithmIncreasePhi(Phi, maxphi, percent); 
	}
}

