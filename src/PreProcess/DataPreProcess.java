package PreProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import SVM.libsvm.svm_node;
import SVM.libsvm.svm_parameter;
import SVM.libsvm.svm_print_interface;

/**
 * Newsgroups文档集预处理类
 */
public class DataPreProcess {

	public static void SetPrintOut(String Dir) throws FileNotFoundException
	{
		String DesFile = Dir+"/PrintFile.dat";
		PrintStream ps=new PrintStream(new FileOutputStream(DesFile));  
		System.setOut(ps); 
	}
	
	public static void SetPrintOutName(String Dir, String Name) throws FileNotFoundException
	{
		String DesFile = Dir+"/PrintFile_"+Name+".dat";
		PrintStream ps=new PrintStream(new FileOutputStream(DesFile));  
		System.setOut(ps); 
	}
	
   	public static String fixedWidthIntegertoString (String s, int w) {
      	//String s = Integer.toString(n);
      	while (s.length() < w) {
      	   s = "0" + s;
     	 }
     	return s;
  	}
	
   	public static String fixedWidthIntegertoSpace (String s, int w) {
      	//String s = Integer.toString(n);
      	while (s.length() < w) {
      	   s = " " + s;
     	 }
     	return s;
  	}	

	public static Map<String, Double> compute_accuracy_F_RetMap(Map<String,String> actual,
												 Map<String,String> pred,
												 Map<Integer,String> classes,
												 String DesFile, int flagP, int flagF) throws IOException {
		//int numclasses = 0.0;
		double numcorrect = 0.0;
		double accuracy   = 0.0;
		double ClusterEntropy = 0.0;
		if(0==classes.size())
		{
			Map<String,String> classtemp = new TreeMap<String, String>();
			Set<Map.Entry<String, String>> allText0 = actual.entrySet();
			for (Iterator<Map.Entry<String, String>> it = allText0.iterator(); it.hasNext();) {
				Map.Entry<String, String> me = it.next();
				if(classtemp.containsKey(me.getValue())){
					//do nothing
				}else{
					classtemp.put(me.getValue(),me.getValue());
				}
			}
			int z = 0;
			Set<Map.Entry<String, String>> classtempSet = classtemp.entrySet();
			for (Iterator<Map.Entry<String, String>> it = classtempSet.iterator(); it.hasNext();) {
				Map.Entry<String, String> me = it.next();
				z++;
				classes.put(z,me.getKey());
			}
			System.out.println("compute_accuracy_F: Class_size = "+classes.size());
		}
		double[] precision = new double[classes.size()+1];
		double[] recall= new double[classes.size()+1];
		double[] F= new double[classes.size()+1];
		double[][]confus = new double[classes.size()+1][classes.size()+1];
		double[] everyClusterEntropy = new double[classes.size()+1];
		for(int i = 0; i <= classes.size(); i++) {
			for(int j = 0; j <= classes.size(); j++) {
				confus[i][j] = 0.0;
			}
		}
		if(actual.size()!=pred.size())
		{
			if(1==flagP)
				System.out.println("Erro: actual.size = " + actual.size()+",  pred.size = "+pred.size());
			return null;
		}

		Set<Map.Entry<String, String>> allText = actual.entrySet();
		for (Iterator<Map.Entry<String, String>> it = allText.iterator(); it.hasNext();) {
			Map.Entry<String, String> me = it.next();
			if(me.getValue().equals(pred.get(me.getKey())))
			{
				numcorrect++;
			}
		}
		accuracy = numcorrect/actual.size();

		for(int i = 1; i <= classes.size(); i++) {
			String a = classes.get(i);
			for(int j = 1; j <= classes.size(); j++) {
				String b = classes.get(j);
				Set<Map.Entry<String, String>> allText2 = actual.entrySet();
				for (Iterator<Map.Entry<String, String>> it = allText2.iterator(); it.hasNext();) {
					Map.Entry<String, String> me = it.next();
					if(me.getValue().equals(a))
					{
						if(pred.get(me.getKey()).equals(b))
						{
							confus[i][0]++;
							confus[i][j]++;
						}
					}
				}
			}
		}

		for(int j = 1; j <= classes.size(); j++) {
			for(int i = 1; i <= classes.size(); i++) {
				confus[0][j] += confus[i][j];
			}
		}
		
	    F[0] = 0;
		recall[0] = 0;
		precision[0] = 0;
		for(int i = 1; i <= classes.size(); i++) {
			if(confus[i][0]>0)
			{
				recall[i] = confus[i][i] / confus[i][0];
			}
			else
			{
				recall[i] = 0.0;
			}
			
			if(confus[0][i]>0)
			{
				precision[i] = confus[i][i] / confus[0][i];
			}
			else
			{
				precision[i] = 0.0;
			}

			if((precision[i]+recall[i])>0)
			{
				F[i] = 2 * (precision[i]*recall[i]) / (precision[i]+recall[i]);
			}
			else
			{
				F[i] = 0.0;
			}
			
		    F[0] += F[i];
			recall[0] += recall[i];
			precision[0] += precision[i];
		}

		double macro_p  = precision[0]/classes.size();
		double macro_r  = recall[0]/classes.size();
		double macro_F1 = 2*macro_p*macro_r/(macro_p+macro_r);

		double T_TP = 0.0;
		double T_TP_FP = 0.0;
		double T_TP_FN = 0.0;
		double micro_p  = 0.0;
		double micro_r  = 0.0;
		double micro_F1 = 0.0;

		for(int i = 1; i <= classes.size(); i++) {
			T_TP += confus[i][i];
			T_TP_FP += confus[0][i];
			T_TP_FN += confus[i][0];
		}

		if(T_TP_FP>0)
		{
			micro_p = T_TP / T_TP_FP;
		}
		else
		{
			micro_p = 0.0;
		}
		if(T_TP_FN>0)
		{
			micro_r = T_TP / T_TP_FN;
		}
		else
		{
			micro_r = 0.0;
		}
		micro_F1 = 2*micro_p*micro_r/(micro_p+micro_r);


		for (int j = 1; j <= classes.size(); j++) {
			if (confus[0][j] != 0) {
				for (int i = 1; i <= classes.size(); i++) {
					double p = (double) confus[i][j] / confus[0][j];
					if (p != 0) {
						everyClusterEntropy[j] += -p * Math.log(p);
					}
				}
				ClusterEntropy += confus[0][j] / (double) pred.size()
								  * everyClusterEntropy[j];
			}
		}


		if(1==flagP){
			System.out.println();
			for(int i = 1; i <= classes.size(); i++) {
				System.out.print("  ");
				for(int j = 1; j <= classes.size(); j++) {
					System.out.print(fixedWidthIntegertoString(""+confus[i][j],5) + "    ");
				}
				System.out.println();
			}

			System.out.println();
			System.out.println("numcorrect:");
			System.out.println(numcorrect);
			System.out.println();
			System.out.println("accuracy:");
			System.out.println(accuracy);	
			
			System.out.println();
			System.out.println("precision:");
			for (int i = 1; i <= classes.size(); i++) {
				System.out.print(precision[i]+"    ");
			}
			System.out.println();
			
			System.out.println("recall:");
			for (int i = 1; i <= classes.size(); i++) {
				System.out.print(recall[i]+"    ");
			}
			System.out.println();
			
			System.out.println("F1:");
			for (int i = 1; i <= classes.size(); i++) {
				System.out.print(F[i]+"    ");
			}
			System.out.println();
			System.out.println();
			System.out.println("macro_p :    "+macro_p);
			System.out.println();
			System.out.println("macro_r :    "+macro_r);
			System.out.println();
			System.out.println("macro_F1:    "+macro_F1);
			System.out.println();
			System.out.println("micro_p :    "+micro_p);
			System.out.println();
			System.out.println("micro_r :    "+micro_r);
			System.out.println();
			System.out.println("micro_F1:    "+micro_F1);
			System.out.println();
			System.out.println("Entropy :    "+ClusterEntropy);
		}else if(2==flagP){
			System.out.println();
			System.out.println("macro_p :    "+macro_p);
			System.out.println();
			System.out.println("macro_r :    "+macro_r);
			System.out.println();
			System.out.println("macro_F1:    "+macro_F1);
			System.out.println();
			System.out.println("micro_p :    "+micro_p);
			System.out.println();
			System.out.println("micro_r :    "+micro_r);
			System.out.println();
			System.out.println("micro_F1:    "+micro_F1);
			System.out.println();
			System.out.println("Entropy :    "+ClusterEntropy);
		}
		
		if(1==flagF){
			FileWriter Writer = new FileWriter(DesFile);
			Writer.append("\n");
			for(int i = 1; i <= classes.size(); i++) {
				Writer.append("\n");;
				for(int j = 1; j <= classes.size(); j++) {
					Writer.append(fixedWidthIntegertoString(""+confus[i][j],5) + "    ");
				}
				Writer.append("\n");
			}

			Writer.append("\n");
			Writer.append("numcorrect: ");
			Writer.append(""+numcorrect);
			Writer.append("\n");
			Writer.append("accuracy  : ");
			Writer.append(""+accuracy);	
			
			Writer.append("\n");
			Writer.append("precision : \n");
			for (int i = 1; i <= classes.size(); i++) {
				Writer.append(precision[i]+"    ");
			}
			Writer.append("\n");
			
			Writer.append("recall : \n");
			for (int i = 1; i <= classes.size(); i++) {
				Writer.append(recall[i]+"    ");
			}
			Writer.append("\n");
			
			Writer.append("F1 : \n");
			for (int i = 1; i <= classes.size(); i++) {
				Writer.append(F[i]+"    ");
			}
			Writer.append("\n");
			Writer.append("\n");
			Writer.append("macro_p :    "+macro_p);
			Writer.append("\n");
			Writer.append("macro_r :    "+macro_r);
			Writer.append("\n");
			Writer.append("macro_F1:    "+macro_F1);
			Writer.append("\n");
			Writer.append("micro_p :    "+micro_p);
			Writer.append("\n");
			Writer.append("micro_r :    "+micro_r);
			Writer.append("\n");
			Writer.append("micro_F1:    "+micro_F1);
			Writer.append("\n");
			Writer.append("Entropy :    "+ClusterEntropy);

			Writer.flush();
			Writer.close();
		}
		Map<String, Double> ResultMap = new TreeMap<String, Double>();
		ResultMap.put("macro_p",macro_p);
		ResultMap.put("macro_r",macro_r);
		ResultMap.put("macro_F1",macro_F1);
		ResultMap.put("micro_p",micro_p);
		ResultMap.put("micro_r",micro_r);
		ResultMap.put("micro_F1",micro_F1);
		ResultMap.put("Entropy",ClusterEntropy);

		return ResultMap;
	}
 
	public static void CreatDir(String srcDir) throws IOException {
		String DirName = srcDir.replace("\\", "/");
		File FileDir = new File(DirName);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
	}

	public Map<String, Map<Integer, Double>> ReadFileVsm(String srcFile) throws IOException {

		long linecount = 0;
		String line;
		String[] lineSplitBlock;
		String[] BlockIndexTfidf;
		File trainSamples = new File(srcFile);
		BufferedReader trainSamplesBR = new BufferedReader(new FileReader(trainSamples));
		Map<String, Map<Integer, Double>> trainFileNameWordTFMap = new TreeMap<String, Map<Integer, Double>>();
		TreeMap<Integer, Double> trainWordTFMap = new TreeMap<Integer, Double>();
		while ((line = trainSamplesBR.readLine()) != null) {
			linecount++;
			lineSplitBlock = line.split(" ");
			trainWordTFMap.clear();
			for (int i = 2; i < lineSplitBlock.length; i = i + 1) {
				BlockIndexTfidf = lineSplitBlock[i].split(":");
				trainWordTFMap.put(Integer.valueOf(BlockIndexTfidf[0]), 
					              Double.valueOf(BlockIndexTfidf[1]));
			}
			TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
			tempMap.putAll(trainWordTFMap);
			trainFileNameWordTFMap.put(lineSplitBlock[0]+"_"+lineSplitBlock[1], tempMap);
		}
		trainSamplesBR.close();

		return trainFileNameWordTFMap;
	}	


	public static void printSISMapForLW(String strDir, String fileName,
			Map<String, Map<Integer, String>> wordMap) throws IOException {
		System.out.println("printSISMapForLW:" + strDir + fileName);
		int countLine = 0;
		File FileDir = new File(strDir);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
		
		File outPutFile = new File(strDir + fileName);
		FileWriter outPutFileWriter = new FileWriter(outPutFile);

		Set<Map.Entry<String, Map<Integer, String>>> wordMapSet = wordMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, String>>> it = wordMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, String>> me = it.next();
			outPutFileWriter.write(me.getKey() + "       ");
		
			Set<Map.Entry<Integer, String>> allWords = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, String>> it2 = allWords.iterator(); it2.hasNext();) {
				Map.Entry<Integer, String> me2 = it2.next();
				outPutFileWriter.write(me2.getValue() + "       ");
				
			}
			outPutFileWriter.write("\n");
			countLine++;
		}
		outPutFileWriter.flush();
		outPutFileWriter.close();
		System.out.println(fileName + " size is " + countLine);
	}
	
	public static void printIIDMap(String strDir, String fileName,
			Map<Integer, Map<Integer, Double>> wordMap) throws IOException {
		System.out.println("PrintIIDMap:" + strDir + fileName);
		int countLine = 0;
		File FileDir = new File(strDir);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
		
		File outPutFile = new File(strDir + fileName);
		FileWriter outPutFileWriter = new FileWriter(outPutFile);

		Set<Map.Entry<Integer, Map<Integer, Double>>> wordMapSet = wordMap.entrySet();
		for (Iterator<Map.Entry<Integer, Map<Integer, Double>>> it = wordMapSet.iterator(); it.hasNext();) {
			Map.Entry<Integer, Map<Integer, Double>> me = it.next();
			outPutFileWriter.write(me.getKey() + " ");
		
			Set<Map.Entry<Integer, Double>> allWords = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allWords.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();
				outPutFileWriter.write(me2.getKey() + ":" + me2.getValue() + " ");
				
			}
			outPutFileWriter.write("\n");
			countLine++;
		}
		outPutFileWriter.flush();
		outPutFileWriter.close();
		System.out.println(fileName + " size is " + countLine);
	}


	public static Map<String, Map<Integer, Double>> copyMaptoMap(Map<String, Map<Integer, Double>> NameMap){

		Map<String, Map<Integer, Double>> NameMapNew = new TreeMap<String, Map<Integer, Double>>();
		Set<Map.Entry<String, Map<Integer, Double>>> NameMapSet = NameMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = NameMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			    TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
				Set<Map.Entry<Integer, Double>> cSet = me.getValue().entrySet();
				for (Iterator<Map.Entry<Integer, Double>> cit = cSet.iterator(); cit.hasNext();) {
					Map.Entry<Integer, Double> cme = cit.next();
					tempMap.put(cme.getKey(), cme.getValue());
				}
				NameMapNew.put(me.getKey(), tempMap);
		}
		return NameMapNew;
	}


}
