package ReduceMethod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import Classification.CBC;
import PreProcess.DataPreProcess;
import SVM.svm_predict;
import SVM.svm_train;
import SVM.libsvm.svm_model;

public class Test{
	
	public static final int LFSVM = 1;
	public static final int PSCC  = 2;
	public static final int VPSVM = 3;
	public static final int NNSVM = 4;
	public static final int KMSVM = 5;
	public static final int SE    = 6;
	public static final int CNN   = 7;
	
	public static void main(String arg[]){
		try {
			SVMMain();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static void SVMMain() throws IOException {
	
	long time1 = 0;
	long time2 = 0;
	long stime = 0;
	double z = 0.0;
	double ybefore = 0;
	double yafter  = 0;

	int FilterMapNum = 4;
	int TrainTestNum = 5; 
	
	String StrSrcDir = "./DataMiningSample/";
	Map<Integer, Double> IndexValueMap = new TreeMap<Integer, Double>();
	Map<Integer, Double> IndexValueMapNew = new TreeMap<Integer, Double>();
	Map<Integer, Double> IndexDisValueMap = new TreeMap<Integer, Double>();
	Map<Integer, Double> IndexDisValueMapNew = new TreeMap<Integer, Double>();
	Map<String, Double> WordTFMap = new TreeMap<String, Double>();
	Map<String, Map<Integer, Double>> trainFileMapVsm  = new TreeMap<String, Map<Integer, Double>>();
	Map<String, Map<Integer, Double>> testFileMapVsm   = new TreeMap<String, Map<Integer, Double>>();
	Map<String, Map<Integer, Double>> trainMapNew  = new TreeMap<String, Map<Integer, Double>>();
	Map<Integer,String> classes  = new TreeMap<Integer, String>();
	Map<String,String> pred   = new TreeMap<String, String>();
	Map<String,String> actual = new TreeMap<String, String>();
	Map<String, Double> TempResultMap = new TreeMap<String, Double>();
	
	Map<Integer, Double> accuracyMap = new TreeMap<Integer, Double>();
	
	DataPreProcess     DataPP = new DataPreProcess();
	
	//DataPreProcess.SetPrintOut(StrSrcDir);
	System.out.println("Test start...");

	int con = 8;
	for(int i=1; i<con; i++)
	{
		time1=0;
		time2=0;
		stime=0;
		String StrSrcDir_Method = "";
		String StrSrcDir_Method_output = "";
		
		double[][] SVMTempResult_macroF1 = new double[TrainTestNum+1][FilterMapNum+1];
		double[][] SVMTempResult_microF1 = new double[TrainTestNum+1][FilterMapNum+1];
		double[][] SVMTempResult_Entropy = new double[TrainTestNum+1][FilterMapNum+1];

		double[][] CBCTempResult_macroF1 = new double[TrainTestNum+1][FilterMapNum+1];
		double[][] CBCTempResult_microF1 = new double[TrainTestNum+1][FilterMapNum+1];
		double[][] CBCTempResult_Entropy = new double[TrainTestNum+1][FilterMapNum+1];

		switch(i)
		{
			case LFSVM:
				StrSrcDir_Method = StrSrcDir+"LFSVM/";
				break;
			case PSCC:
				StrSrcDir_Method = StrSrcDir+"PSCC/";
				break;
			case VPSVM:
				StrSrcDir_Method = StrSrcDir+"VPSVM/";
				break;
			case NNSVM:
				StrSrcDir_Method = StrSrcDir+"NNSVM/";
				break;
			case KMSVM:
				StrSrcDir_Method = StrSrcDir+"KMSVM/";
				break;
			case SE:
				StrSrcDir_Method = StrSrcDir+"SE/";
				break;
			case CNN:
				StrSrcDir_Method = StrSrcDir+"CNN/";
				break;
			default:
				System.err.println("No such method...");
				break;
		}
		
		StrSrcDir_Method_output = StrSrcDir_Method+"0_outputfile/";
		DataPP.CreatDir(StrSrcDir_Method);
		DataPP.CreatDir(StrSrcDir_Method_output);
		
		for(int p=1; p<FilterMapNum; p++)
		{
			for(int j=0; j<TrainTestNum; j++)
			{
				String VsmTestSrcDir  = StrSrcDir+"/4_DocVector/VsmTFIDFMapTestSample"+j+".txt";
				String VsmTrainSrcDir = StrSrcDir+"/4_DocVector/VsmTFIDFMapTrainSample"+j+".txt";
				
				testFileMapVsm  = DataPP.ReadFileVsm(VsmTestSrcDir);
				trainFileMapVsm = DataPP.ReadFileVsm(VsmTrainSrcDir);
		
				Map<String, Map<Integer, Double>> temp = new TreeMap<String, Map<Integer, Double>>();
				temp.putAll(trainFileMapVsm);
				Map<String, Map<Integer, Double>> tempmapvsm = new TreeMap<String, Map<Integer, Double>>();
				ybefore=temp.size();
				time1=System.currentTimeMillis();
				if(true){
					if(i==SE){
						System.out.println("SE start...");
						SE se=new SE();
						z=0.8;
						double delt = 0.01;
						tempmapvsm = se.SEMain(temp, z, 0xFF, p/10.0, delt);
					}
					
					if(i==PSCC){
						System.out.println("PSCC start...");
						z=0;
						z+=0.08*p;
						Pscccut ps=new Pscccut();
						tempmapvsm=ps.Useps(temp, z);
						//For debug
						//SEtool.printSIDMapForSE(StrSrcDir_Method,"Reducedmapvsm.txt",tempmapvsm);
					}
					
					if(i==VPSVM){
						System.out.println("VPSVM start...");
						z=0.2;
						z-=0.05*(double)p;
						Shadowcut sh=new Shadowcut();
						tempmapvsm=sh.Usesh(temp, z, 20);
					}
				}
				
				if(true){
					if(i==LFSVM){
						System.out.println("LFSVM start...");
						Centrcut cut=new Centrcut();
						tempmapvsm =cut.Usecen(temp);
					}
					if(i==NNSVM){
						System.out.println("NNSVM start...");
						Nncut nc=new Nncut();
						tempmapvsm=nc.Nncutmain(temp);
					}
					if(i==KMSVM){
						System.out.println("KMSVM start...");
						int c=200;
						Fcmcut fc=new Fcmcut();
						tempmapvsm =fc.Fcmcutmain(temp,c, 10);
					}
					if(i==CNN){
						System.out.println("CNN start...");
						CNN NN=new CNN();
						tempmapvsm =NN.CNNmain(temp);
					}
				}
				time2  = System.currentTimeMillis();
				stime  = time2-time1;
				yafter = tempmapvsm.size();
				
				System.out.println("  Cost time : "+stime);
				System.out.println("Size before : "+ybefore);
				System.out.println("   Size end : "+yafter);
				System.out.println("      Ratio : "+(1-(yafter/ybefore))*100);
				
				System.out.println("SVM start...i = "+i+", j = "+j);
				
				//For Dermatology
				String SVM_train_argv = "-s 1 -t 0 -c 128 -g 0.0001 -n 0.01 VsmTFIDFMapTrainSample"+j;
				//For Glass
				//String SVM_train_argv = "-s 0 -t 1 -c 128 -g 1 -n 0.2 VsmTFIDFMapTrainSample"+j;
				//For Heart
				//String SVM_train_argv = "-s 0 -t 1 -c 0.25 -g 0.1 -n 0.1 VsmTFIDFMapTrainSample"+j;			
				//For Ionosphere
				//String SVM_train_argv = "-s 0 -t 0 -c 32 -g 0.01 -n 0.2 VsmTFIDFMapTrainSample"+j;	
				//For Isolet
				//String SVM_train_argv = "-s 0 -t 1 -c 0.25 -g 0.01 -n 0.1 VsmTFIDFMapTrainSample"+j;	
				//For Letter
				//String SVM_train_argv = "-s 0 -t 1 -c 2 -g 0.01 -n 0.1 VsmTFIDFMapTrainSample"+j;
				//For Segment
				//String SVM_train_argv = "-s 0 -t 1 -c 0.5 -g 0.01 -n 0.1 VsmTFIDFMapTrainSample"+j;
				//For USPS
				//String SVM_train_argv = "-s 0 -t 2 -c 128 -g 0.01 -n 0.1 VsmTFIDFMapTrainSample"+j;
				//For Waveform
				//String SVM_train_argv = "-s 0 -t 1 -c 0.25 -g 0.01 -n 0.1 VsmTFIDFMapTrainSample"+j;
				//For Newsgroup
				//String SVM_train_argv = "-s 0 -t 2 -c 128 -g 0.1 -n 0.1 VsmTFIDFMapTrainSample"+j;
				//For Reuters
				//String SVM_train_argv = "-s 0 -t 2 -c 128 -g 0.1 -n 0.1 VsmTFIDFMapTrainSample"+j;
	
				String SVM_test_argv  = "-m 1 VsmTFIDFMapTestSample"+j+" VsmTFIDFMapTrainSample"+j+".model "+"output"+j;
	
				String[] train_argv_Split = SVM_train_argv.split(" ");
				String[] test_argv_Split  = SVM_test_argv.split(" ");
				svm_train   SVM_train = new svm_train();
				
				svm_predict SVM_test  = new svm_predict();
				time1=System.currentTimeMillis();
				svm_model model = SVM_train.run(train_argv_Split, tempmapvsm );
				time2=System.currentTimeMillis();
				actual.clear();
				pred.clear();
				classes.clear();
				double accuracy = SVM_test.run(test_argv_Split, model, testFileMapVsm, actual, pred);
				
				String DesFile = StrSrcDir_Method_output+"SVM_"+i+"_"+j+".txt";
				TempResultMap = DataPP.compute_accuracy_F_RetMap(actual, pred, classes, DesFile, 2, 1);
	
				SVMTempResult_macroF1[j][p] = TempResultMap.get("macro_F1");
				SVMTempResult_microF1[j][p] = TempResultMap.get("micro_F1");
				SVMTempResult_Entropy[j][p] = TempResultMap.get("Entropy");		
	
				actual.clear();
				pred.clear();
				classes.clear();
				time1=System.currentTimeMillis();
				accuracy = CBC.CBCMain(StrSrcDir_Method_output,tempmapvsm ,testFileMapVsm,actual,pred);
				time2=System.currentTimeMillis();
	
				String DesFileCBC2 = StrSrcDir_Method_output+"CBC_"+i+"_"+j+".txt";
	
				TempResultMap = DataPP.compute_accuracy_F_RetMap(actual, pred, classes, DesFileCBC2, 2, 1);
	
				CBCTempResult_macroF1[j][p] = TempResultMap.get("macro_F1");
				CBCTempResult_microF1[j][p] = TempResultMap.get("micro_F1");
				CBCTempResult_Entropy[j][p] = TempResultMap.get("Entropy");
			}
	
			for(int m=0; m<TrainTestNum; m++)
			{
				SVMTempResult_macroF1[TrainTestNum][p] += SVMTempResult_macroF1[m][p];
				SVMTempResult_microF1[TrainTestNum][p] += SVMTempResult_microF1[m][p];
				SVMTempResult_Entropy[TrainTestNum][p] += SVMTempResult_Entropy[m][p];
	
				CBCTempResult_macroF1[TrainTestNum][p] += CBCTempResult_macroF1[m][p];
				CBCTempResult_microF1[TrainTestNum][p] += CBCTempResult_microF1[m][p];
				CBCTempResult_Entropy[TrainTestNum][p] += CBCTempResult_Entropy[m][p];
	
			}
	
				SVMTempResult_macroF1[TrainTestNum][p] = SVMTempResult_macroF1[TrainTestNum][p]/5.0;
				SVMTempResult_microF1[TrainTestNum][p] = SVMTempResult_microF1[TrainTestNum][p]/5.0;
				SVMTempResult_Entropy[TrainTestNum][p] = SVMTempResult_Entropy[TrainTestNum][p]/5.0;
	
				CBCTempResult_macroF1[TrainTestNum][p] = CBCTempResult_macroF1[TrainTestNum][p]/5.0;
				CBCTempResult_microF1[TrainTestNum][p] = CBCTempResult_microF1[TrainTestNum][p]/5.0;
				CBCTempResult_Entropy[TrainTestNum][p] = CBCTempResult_Entropy[TrainTestNum][p]/5.0;
	
				Map<String, Map<Integer, String>> RESULTMAP = new TreeMap<String, Map<Integer, String>>();
				Map<Integer,String> IndexMapNewNumMap  = new TreeMap<Integer, String>();
				Map<Integer,String> SVMTempResult_macroF1Map  = new TreeMap<Integer, String>();
				Map<Integer,String> SVMTempResult_microF1Map  = new TreeMap<Integer, String>();
				Map<Integer,String> SVMTempResult_EntropyMap  = new TreeMap<Integer, String>();
				Map<Integer,String> CBCTempResult_macroF1Map  = new TreeMap<Integer, String>();
				Map<Integer,String> CBCTempResult_microF1Map  = new TreeMap<Integer, String>();
				Map<Integer,String> CBCTempResult_EntropyMap  = new TreeMap<Integer, String>();
				
	
				for(int m=0; m<=p; m++)
				{
					SVMTempResult_macroF1Map.put(m+1, DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_macroF1[TrainTestNum][m],20));
					SVMTempResult_microF1Map.put(m+1, DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_microF1[TrainTestNum][m],20));
					SVMTempResult_EntropyMap.put(m+1, DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_Entropy[TrainTestNum][m],20));
					CBCTempResult_macroF1Map.put(m+1, DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_macroF1[TrainTestNum][m],20));
					CBCTempResult_microF1Map.put(m+1, DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_microF1[TrainTestNum][m],20));
					CBCTempResult_EntropyMap.put(m+1, DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_Entropy[TrainTestNum][m],20));
					
					System.out.println("=============================================");
					System.out.println("SVMTempResult_macroF1 :"+DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_macroF1[TrainTestNum][m],20));
					System.out.println("SVMTempResult_microF1 :"+DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_microF1[TrainTestNum][m],20));
					System.out.println("SVMTempResult_Entropy :"+DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_Entropy[TrainTestNum][m],20));
					System.out.println("CBCTempResult_macroF1 :"+DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_macroF1[TrainTestNum][m],20));
					System.out.println("CBCTempResult_microF1 :"+DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_microF1[TrainTestNum][m],20));
					System.out.println("CBCTempResult_Entropy :"+DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_Entropy[TrainTestNum][m],20));
	
				}
	
				RESULTMAP.put("002_SVMTempResult_macroF1Map", SVMTempResult_macroF1Map);
				RESULTMAP.put("003_SVMTempResult_microF1Map", SVMTempResult_microF1Map);
				RESULTMAP.put("004_SVMTempResult_EntropyMap", SVMTempResult_EntropyMap);
				RESULTMAP.put("005_CBCTempResult_macroF1Map", CBCTempResult_macroF1Map);
				RESULTMAP.put("006_CBCTempResult_microF1Map", CBCTempResult_microF1Map);
				RESULTMAP.put("007_CBCTempResult_EntropyMap", CBCTempResult_EntropyMap);
	
				String FileName = "Reduce_Method_"+i+".txt";
				DataPP.printSISMapForLW(StrSrcDir_Method, FileName , RESULTMAP);	
			}
		}
		System.out.println("Test Finished!!!");
	}
}
