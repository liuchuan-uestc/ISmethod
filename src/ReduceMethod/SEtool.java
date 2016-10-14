package ReduceMethod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class SEtool{	
	double w;
	double K;
	double B;
	double a;
	double xMin;
	double xMax;
	double delt;
	int Mnum;
	Map<String, Map<String, Integer>> CatePointName;
	Map<String, Map<Integer, Double>> CenterFixed;
	Map<String, Map<Integer, Double>> CenterPoint;
	Map<String, Map<Integer, Double>> Trainpoint;
	Map<String, Double> ClusterRadius;
	Map<String, String> MinFreeSpacePointToPoint;
	Map<String, Double> CenterPointMinFreeSpace;
	Map<String, String> MinLWindexPointToPoint;
	Map<String, Double> CenterPointMinLWindex;

	Map<String, Integer> TrainFileFlagMapVsm;
	//---------------------------------------------------------------
	//	Constructors
	//---------------------------------------------------------------	

	public SEtool(Map<String, Map<String, Integer>> CatePointNameMap,
				  Map<String, Map<Integer, Double>> CenterFixedMap,
				  Map<String, Map<Integer, Double>> CateCenterMap, 
				  Map<String, Map<Integer, Double>> TrainPointMap){

		//CenterPointMap = new TreeMap<String, Map<Integer, Double>>();
		//TrainpointMap  = new TreeMap<String, Map<Integer, Double>>();
		w = 1;
		K = 3.0;
		B = 1;
		a = 0.01;
		delt = 0.01;
		xMin = 0.0;
		xMax = 0.8;
		Mnum = 3;
		CatePointName  = CatePointNameMap;
		CenterFixed    = CenterFixedMap;
		CenterPoint    = CateCenterMap;
		Trainpoint     = TrainPointMap;
		
		CenterPointMinFreeSpace  = new TreeMap<String, Double>();
		MinFreeSpacePointToPoint = new TreeMap<String, String>();
		CenterPointMinLWindex  = new TreeMap<String, Double>();
		MinLWindexPointToPoint = new TreeMap<String, String>();

		ClusterRadius = new TreeMap<String, Double>();
		TrainFileFlagMapVsm = new TreeMap<String, Integer>();
	}

	public void SetA(double i){
		a = i;
	}

	public void SetDelt(double i){
		delt = i;
	}
	
	public void SetxMinMax(double iMin, double iMax){
		xMin = iMin;
		xMax = iMax;
	}
	
	public Map<String, Map<Integer, Double>> GetCenterPointMap(){
		return CenterPoint;
	}

	public Map<String, Double> GetCenterPointMinFreeSpace(){
		return CenterPointMinFreeSpace;
	}

	public Map<String, Double> GetClusterRadius(){
		return ClusterRadius;
	}

	public double GetMinFreeSpace(){
		double MinFreeSpace = 10.0;

		Set<Map.Entry<String, Double>> allFreeSpaceSet = CenterPointMinFreeSpace.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allFreeSpaceSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			if(MinFreeSpace > me.getValue())
			{
				MinFreeSpace = me.getValue();
			}
		}
		return MinFreeSpace;
	}

	public String GetMinFreeSpaceCluster(){
		double MinFreeSpace = 10.0;
		String MinFreeSpaceName = "";
		Set<Map.Entry<String, Double>> allFreeSpaceSet = CenterPointMinFreeSpace.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allFreeSpaceSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			if(MinFreeSpace > me.getValue())
			{
				MinFreeSpace = me.getValue();
				MinFreeSpaceName = me.getKey();
			}
		}
		return MinFreeSpaceName;
	}

	public String GetMinFreeSpaceClusterSlave(String CenterPoint){
		return MinFreeSpacePointToPoint.get(CenterPoint);
	}
	
	public double GetAverageFreeSpace(){
		double SumFreeSpace = 0.0;

		Set<Map.Entry<String, Double>> allFreeSpaceSet = CenterPointMinFreeSpace.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allFreeSpaceSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			SumFreeSpace += me.getValue();
		}
		SumFreeSpace = SumFreeSpace/CenterPointMinFreeSpace.size();
		return SumFreeSpace;
	}

	public double GetMaxClusterRadius(){
		double MaxClusterRadius = 0.0;

		Set<Map.Entry<String, Double>> allClusterRadiusSet = ClusterRadius.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allClusterRadiusSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			if(MaxClusterRadius < me.getValue())
			{
				MaxClusterRadius = me.getValue();
			}
		}
		return MaxClusterRadius;
	}

	public double GetAverageClusterRadius(){
		double SumClusterRadius = 0.0;

		Set<Map.Entry<String, Double>> allClusterRadiusSet = ClusterRadius.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allClusterRadiusSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			SumClusterRadius += me.getValue();
		}
		SumClusterRadius = SumClusterRadius/ClusterRadius.size();
		return SumClusterRadius;
	}
	
	public double CptTotalFreeSpace(){
		double sum = 0.0;
		double FreeSpace = 0.0;
		double MinFreeSpace = 0.0;
		String MinFreeSpaceName = "";
		CenterPointMinFreeSpace.clear();
		MinFreeSpacePointToPoint.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MinFreeSpace = 10.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					FreeSpace = CptFreeSpace(me.getKey(), me2.getKey());
					sum += FreeSpace;
					if(MinFreeSpace > FreeSpace){
						MinFreeSpace = FreeSpace;
						MinFreeSpaceName = me2.getKey();
					}
				}
			}
			CenterPointMinFreeSpace.put(me.getKey(), MinFreeSpace);
			MinFreeSpacePointToPoint.put(me.getKey(), MinFreeSpaceName);
		}
		return sum;
	}
	
	public double CptFreeSpace2(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		String NearestPointToMain  = FindNearestPoint3(CenterPointMain, CenterPointSlave);
		String NearestPointToSlave = FindNearestPoint3(CenterPointSlave, CenterPointMain);
		double distanceToMain  = 1-CptSimility(NearestPointToMain, CenterPointMain);
		double distanceToSlave = 1-CptSimility(NearestPointToSlave, CenterPointMain);
		sum = distanceToMain - distanceToSlave;
		if(false)
		{
			System.out.println("CptFreeSpace distanceToMain : "+distanceToMain);
			System.out.println("CptFreeSpace distanceToSlave: "+distanceToSlave);
			System.out.println("CptFreeSpace CenterPointMain : "+CenterPointMain);
			System.out.println("CptFreeSpace CenterPointSlave: "+CenterPointSlave);
			System.out.println("CptFreeSpace NearestPointToMain : "+NearestPointToMain);
			System.out.println("CptFreeSpace NearestPointToSlave: "+NearestPointToSlave);
		}
		return sum;
	}
	
	public double CptFreeSpace(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		double distanceSlaveToMain  = FindNearestPoint(CenterPointMain, CenterPointSlave);
		double distanceMainToMain = FindNearestPoint2(CenterPointMain, CenterPointSlave);
		sum = distanceSlaveToMain - distanceMainToMain;
		if(false)
		{
			System.out.println("CptFreeSpace distanceSlaveToMain : "+distanceSlaveToMain);
			System.out.println("CptFreeSpace distanceMainToMain  : "+distanceMainToMain);
		}
		return sum;
	}

	public String FindNearestPoint3(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		double MaxSimility      = -10.0;
		double SimilityWithMain = 0.0;
		String NearestPoint = "";
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointSlave).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();
			SimilityWithMain = CptSimility(me.getKey(), CenterPointMain);
			if(MaxSimility < SimilityWithMain){
				MaxSimility  = SimilityWithMain;
				NearestPoint = me.getKey();
			}
		}
		return NearestPoint;
	}
	public double FindNearestPoint(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		double Mindistance      = 0.0;
		double DistanceWithMain = 0.0;
		int MnumBak = Mnum;

		String NearestPoint = "";
		SortedMap<Double, Integer> MindistanceMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointSlave).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();
			DistanceWithMain = 1-CptSimility(me.getKey(), CenterPointMain);
            MindistanceMap.put(DistanceWithMain, 1);
		}
		
		Set<Map.Entry<Double, Integer>> MindistanceMapSet2 = MindistanceMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MindistanceMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(MnumBak>0){
				Mindistance += me2.getKey();
				MnumBak--;
			}else{
				break;
			}
		}
		
		if(false)
		{
			System.out.println("1 Mindistance/Mnum : "+Mindistance/Mnum);
		}
		return Mindistance/Mnum;
	}
	
	public double FindNearestPoint2(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		double Mindistance      = 0.0;
		double DistanceWithMain = 0.0;
		int MnumBak = Mnum;

		String NearestPoint = "";
		SortedMap<Double, String> MindistanceMap = new TreeMap<Double, String>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();
			DistanceWithMain = 1-CptSimility(me.getKey(), CenterPointSlave);
            MindistanceMap.put(DistanceWithMain, me.getKey());
		}
		
		Set<Map.Entry<Double, String>> MindistanceMapSet2 = MindistanceMap.entrySet();
		for (Iterator<Map.Entry<Double, String>> it2 = MindistanceMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, String> me2 = it2.next();
			if(MnumBak>0){
				Mindistance += CptSimility(me2.getValue(), CenterPointMain);
				MnumBak--;
			}else{
				break;
			}
		}
		if(false)
		{
			System.out.println("2 Mindistance/Mnum : "+Mindistance/Mnum);
		}
		return Mindistance/Mnum;
	}


	public double CptClusterRadius(){
		double sum = 0.0;
		double Radius = 0.0;
		ClusterRadius.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			//Radius = CptOneClusterRadius(me.getKey());
			Radius = CptOneClusterRadiusAverage(me.getKey());
			sum += Radius;
			//System.out.println("CptClusterRadius Radius("+me.getKey()+")    : "+Radius);
			ClusterRadius.put(me.getKey(), Radius);
		}
		return sum;
	}

	public double CptOneClusterRadius(String CenterPoint){
		int MnumBak = Mnum;
		int index = 0;
		double sum = 0.0;
		double Radius = 0.0;
		double MaxRadius = 0.0;
		SortedMap<Double, Integer> MaxRadiusMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPoint).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
			Map.Entry<String, Integer> me2 = it2.next();
			Radius = CptSimility(me2.getKey(), CenterPoint);
			sum += 1-Radius;
			index++;
            MaxRadiusMap.put(Radius, 1);
		}
		sum = sum/index;

		Set<Map.Entry<Double, Integer>> MaxRadiusMapSet2 = MaxRadiusMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MaxRadiusMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(MnumBak>0){
				MaxRadius += 1-me2.getKey();
				MnumBak--;
			}else{
				break;
			}
		}
		
		return MaxRadius/Mnum;
	}

	public double CptOneClusterRadiusAverage(String CenterPoint){
		int MnumBak = 0;
		int index = 0;
		double sum = 0.0;
		double Radius = 0.0;
		double MaxRadius = 0.0;
		SortedMap<Double, Integer> MaxRadiusMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPoint).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
			Map.Entry<String, Integer> me2 = it2.next();
			Radius = CptSimility(me2.getKey(), CenterPoint);
			sum += 1-Radius;
			index++;
            MaxRadiusMap.put(Radius, 1);
		}
		sum = sum/index;

		Set<Map.Entry<Double, Integer>> MaxRadiusMapSet2 = MaxRadiusMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MaxRadiusMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			MaxRadius += 1-me2.getKey();
			MnumBak++;
		}
		
		return MaxRadius/MnumBak;
	}
//===========================================================
	public double CptTotalLWindex(){
		double sum = 0.0;
		double LWindex = 0.0;
		double MinLWindex = 0.0;
		String MinLWindexName = "";
		CenterPointMinLWindex.clear();
		MinLWindexPointToPoint.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MinLWindex = 10.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					LWindex = CptLWindex(me.getKey(), me2.getKey());
					sum += LWindex;
					if(MinLWindex > LWindex){
						MinLWindex = LWindex;
						MinLWindexName = me2.getKey();
					}
				}
			}
			CenterPointMinLWindex.put(me.getKey(), MinLWindex);
			MinLWindexPointToPoint.put(me.getKey(), MinLWindexName);
		}
		return sum;
	}

	public double CptLWindex(String CenterPointMain, String CenterPointSlave){
        int Separable = 0;
		double LWindex = 0.0;
		double distance_ij = 1-CptSimilityCTC(CenterPointMain, CenterPointSlave);
        double MinRadius = 0.0;
		double Radius_i = CptOneClusterRadius(CenterPointMain);
		double Radius_j = CptOneClusterRadius(CenterPointSlave);
		if((distance_ij<Radius_i)||(distance_ij<Radius_j))
		{
			LWindex = distance_ij-(Radius_i+Radius_j);
		}else{
			LWindex = CptFreeSpace(CenterPointMain, CenterPointSlave);
			//LWindex_ji = CptFreeSpace(CenterPointSlave, CenterPointMain);
		}

		if(Radius_i<Radius_j)
		{
			MinRadius = Radius_i;
		}else{
			MinRadius = Radius_j;
		}

		if(LWindex<-2*MinRadius){
			Separable = 1;
		}else{
			if(LWindex<0){
				Separable = 2;
			}else{
				Separable = 3;
			}
		}

		if(true)
		{
			System.out.println("CptLWindex distance("+CenterPointMain+","+CenterPointSlave+"): "+distance_ij);
			System.out.println("CptLWindex Radius("+CenterPointMain+")    : "+Radius_i);
			System.out.println("CptLWindex Radius("+CenterPointSlave+")    : "+Radius_j);
			System.out.println("CptLWindex LWindex     : "+LWindex);
			switch(Separable)
			{
				case 1:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly non-separable");
					break;
				case 2:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  approximate linearly separable");
					break;
				case 3:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly separable");
					break;
				default:
					System.out.println("CptLWindex Separable    : "+Separable);
			}
		    System.out.println("-----------------------------------------");
		}
		return LWindex;
	}
	
	public double GetMinLWindex(){
		double MinLWindex = 0xffffff;

		Set<Map.Entry<String, Double>> allLWindexSet = CenterPointMinLWindex.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allLWindexSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			if(MinLWindex > me.getValue())
			{
				MinLWindex = me.getValue();
			}
		}
		return MinLWindex;
	}

	public double GetAverageLWindex(){
		double SumLWindex = 0.0;

		Set<Map.Entry<String, Double>> allFreeSpaceSet = CenterPointMinLWindex.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allFreeSpaceSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			SumLWindex += me.getValue();
		}
		SumLWindex = SumLWindex/CenterPointMinLWindex.size();
		return SumLWindex;
	}

	public void DataAnalyse()
	{
		double TotalLWindex = CptTotalLWindex();
		double MinLWindex   = GetMinLWindex();
		double AverageLWindex = GetAverageLWindex();
		System.out.println(" TotalLWindex  : "+TotalLWindex);
		System.out.println(" MinLWindex    : "+MinLWindex);
		System.out.println(" AverageLWindex: "+AverageLWindex);

		CptClusterRadius();
		double MaxClusterRadius     = GetMaxClusterRadius();
		double AverageClusterRadius = GetAverageClusterRadius();
		System.out.println(" MaxClusterRadius     : "+MaxClusterRadius);
		System.out.println(" AverageClusterRadius : "+AverageClusterRadius);

		double DI     = DunnIndex();
		double DBI    = DaviesBouldinIndex();
		System.out.println(" DunnIndex            : "+DI);
		System.out.println(" DaviesBouldinIndex   : "+DBI);
	    System.out.println("-----------------------------------------");
	}

	public double DaviesBouldinIndex()
	{
		double sum  = 0.0;
		double R_ij = 0.0;
		double MaxR_ij = 0.0;

		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MaxR_ij = 0.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					R_ij = R_fun(me.getKey(), me2.getKey());
					if(MaxR_ij < R_ij){
						MaxR_ij = R_ij;
					}
				}
			}
			sum += MaxR_ij;
		}
		return sum/CenterPoint.size();
	}

	public double R_fun(String CenterPointMain, String CenterPointSlave)
	{
		double Radius_i = CptOneClusterRadius(CenterPointMain);
		double Radius_j = CptOneClusterRadius(CenterPointSlave);
		double distance_ij = 1-CptSimilityCTC(CenterPointMain, CenterPointSlave);
		return (Radius_i+Radius_j)/distance_ij;
	}

	public double DunnIndex()
	{
		double sum  = 0.0;
		double Radius_i = 0.0;
		double MaxRadius_i = 0.0;
		double Distance_ij = 0.0;
		double Mindistance_ij = 1.0;

		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			Radius_i = CptOneClusterRadius(me.getKey());
			if(MaxRadius_i < Radius_i){
				MaxRadius_i = Radius_i;
			}
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					Distance_ij = 1-CptSimilityCTC(me.getKey(), me2.getKey());
					if(Mindistance_ij > Distance_ij){
						Mindistance_ij = Distance_ij;
					}
				}
			}
		}
		return Mindistance_ij/MaxRadius_i;
	}

//===========================================================
	private static void normalization(Map<Integer, Double> c)
	{
		int j = 0;
		double sum = 0.0;
		Set<Map.Entry<Integer, Double>> xSet = c.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> xit = xSet.iterator(); xit.hasNext();) {
			Map.Entry<Integer, Double> xme = xit.next();

			sum += xme.getValue()*xme.getValue();
		}
		sum = Math.sqrt(sum);
		Set<Map.Entry<Integer, Double>> xSet1 = c.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> xit1 = xSet1.iterator(); xit1.hasNext();) {
			Map.Entry<Integer, Double> xme1 = xit1.next();
			c.put(xme1.getKey(), xme1.getValue()/sum);
		}
	}

	static double dot(Map<Integer, Double> x, Map<Integer, Double> c)
	{
		double sum = 0;
		Set<Map.Entry<Integer, Double>> xSet = x.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> xit = xSet.iterator(); xit.hasNext();) {
			Map.Entry<Integer, Double> xme = xit.next();

			if(c.containsKey(xme.getKey())){
				sum += xme.getValue()*c.get(xme.getKey());
			}
		}
		return sum;
	}

	private static double computeSim(Map<Integer, Double> testWordTFMap,
			Map<Integer, Double> trainWordTFMap) {
		// TODO Auto-generated method stub
		double mul = 0, testAbs = 0, trainAbs = 0;
		Set<Map.Entry<Integer, Double>> testWordTFMapSet = testWordTFMap
				.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> it = testWordTFMapSet
				.iterator(); it.hasNext();) {
			Map.Entry<Integer, Double> me = it.next();
			if (trainWordTFMap.containsKey(me.getKey())) {
				mul += me.getValue() * trainWordTFMap.get(me.getKey());
			}
			testAbs += me.getValue() * me.getValue();
		}
		testAbs = Math.sqrt(testAbs);

		Set<Map.Entry<Integer, Double>> trainWordTFMapSet = trainWordTFMap
				.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> it = trainWordTFMapSet
				.iterator(); it.hasNext();) {
			Map.Entry<Integer, Double> me = it.next();
			trainAbs += me.getValue() * me.getValue();
		}
		trainAbs = Math.sqrt(trainAbs);
		mul = mul/(testAbs * trainAbs);
		if (Double.isNaN(mul)) {
			System.out.println("computeSim:Find a NaN ");
		}
		if(mul == 1){
			//mul = 0.0;
		}
		return mul;
	}

	public double CptSimility(String xname, String cname){
		double sum = 0.0;
		sum += computeSim(Trainpoint.get(xname),CenterPoint.get(cname));
		return sum;
	}

	public double CptSimility2(String xname, String cname){
		double sum = 0.0;
		sum += computeSim(Trainpoint.get(xname),CenterFixed.get(cname));
		return sum;
	}

	public double CptSimilityCTC(String cname1, String cname2){
		double sum = 0.0;
		sum += computeSim(CenterPoint.get(cname1),CenterPoint.get(cname2));
		return sum;
	}
	
	public double CptDistance(String xname, String pname, String qname){
		double sum = 0.0;
		double L0 = CptSimility(xname, pname);
		double L1 = CptSimility(xname, qname);	
		double L3 = 2-L0-L1;
		if(L3 == 0){
			L3 = 0.0001;
			//System.out.println("CptDistance:L0: " +L0+", L1: "+L1);
		}
		sum += (L0-L1)/L3;
		if (Double.isNaN(sum)) {
			System.out.println("CptDistance:Find a NaN");
		}

		return sum;
	}


	//=================================================================================

	public static Map<String, Map<Integer, Double>> GetCenterPoint(Map<String, Map<Integer, Double>> TrainFileMapVsm, 
																   Map<String, Map<String, Integer>> CateMap,
								              					   Map<String, Integer> TrainFileFlagMapVsm) {
		//训练集中相同的类放在一个map中，形成同类样本map
		int  Tsize = TrainFileMapVsm.size();
		//Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();

		Set<Map.Entry<String, Map<Integer, Double>>> TrainFileMapVsmSet = TrainFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			if(1==TrainFileFlagMapVsm.get(me.getKey())){
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
        //printSIDMap(strDir, "CateCenterMap.txt", CateCenterMap);

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
		return CateCenterMap;
	}


	public Map<String, Map<Integer, Double>> SelectFileAlgorithm(double phi)
	{
		System.out.println("---------------------SelectFileAlgorithm---------------------");
		double distance = 0.0;
		int IterationNum = 0;
		int IterationDel = 1;
		int TotalDel = 0;
		Map<String, Map<Integer, Double>> TrainFileMap  = new TreeMap<String, Map<Integer, Double>>();
		Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();
		Map<String, Double> ClusterFilterThreshold = new TreeMap<String, Double>();
		CptClusterRadius();
		Set<Map.Entry<String, Double>> ClusterRadiusSet = ClusterRadius.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = ClusterRadiusSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			//System.out.println( me.getKey()+"  : "+phi*me.getValue());
			ClusterFilterThreshold.put(me.getKey(), phi*me.getValue());
		}
		//System.out.println(" ClusterFilterThreshold     : "+ClusterFilterThreshold.size());
		
		Set<Map.Entry<String, Map<Integer, Double>>> TrainpointSet = Trainpoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainpointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			TrainFileFlagMapVsm.put(me.getKey(), 1);
		}
		//System.out.println(" TrainFileFlagMapVsm     : "+TrainFileFlagMapVsm.size());
		while (IterationDel>0){
			IterationNum++;
			IterationDel = 0;
			CateMap.clear();
			CateCenterMap = GetCenterPoint(Trainpoint, CateMap, TrainFileFlagMapVsm);
			System.out.println(" CateMap      : "+CateMap.size());
			Set<Map.Entry<String, Map<String, Integer>>> CateMapSet = CateMap.entrySet();
			for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet.iterator(); it.hasNext();) {
				Map.Entry<String, Map<String, Integer>> me = it.next();
				Set<Map.Entry<String, Integer>> allVsmSet2 = me.getValue().entrySet();
				for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
					Map.Entry<String, Integer> me2 = it2.next();
					distance = 1-computeSim(CateCenterMap.get(me.getKey()), Trainpoint.get(me2.getKey()));
					if((distance<ClusterFilterThreshold.get(me.getKey()))&&
					(me.getValue().size()>0.4*CatePointName.get(me.getKey()).size())&&
					 (me.getValue().size()>50)){
						TrainFileFlagMapVsm.put(me2.getKey(), 2);
						IterationDel++;
						TotalDel++;
					}			
				}
			}
		    System.out.println("---------------------"+IterationNum+"--------------------");
			System.out.println(" IterationDel : "+IterationDel);
			System.out.println(" TotalDel     : "+TotalDel);
		}
		
		Set<Map.Entry<String, Map<Integer, Double>>> TrainpointSet1 = Trainpoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainpointSet1.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			if(1==TrainFileFlagMapVsm.get(me.getKey())){
			    TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
				Set<Map.Entry<Integer, Double>> cSet = me.getValue().entrySet();
				for (Iterator<Map.Entry<Integer, Double>> cit = cSet.iterator(); cit.hasNext();) {
					Map.Entry<Integer, Double> cme = cit.next();
					tempMap.put(cme.getKey(), cme.getValue());
				}
				TrainFileMap.put(me.getKey(), tempMap);
			}
		}
		System.out.println("TrainFileMap    size: "+TrainFileMap.size());
		return TrainFileMap;
	}
	
	public Map<String, Map<Integer, Double>> SelectFileAlgorithmIncreasePhi(double phi, double maxphi, double percent)
	{
		System.out.println("---------------------SelectFileAlgorithmIncreasePhi---------------------");
		double distance = 0.0;
		double R = 0.0;
		int Total = 0;
		int IterationNum = 0;
		int IterationDel = 1;
		int TotalDel = 0;
		Map<String, Map<Integer, Double>> TrainFileMap  = new TreeMap<String, Map<Integer, Double>>();
		Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();
		Map<String, Double> ClusterFilterThreshold = new TreeMap<String, Double>();
		Map<String, Double> ClusterDelMaxnum = new TreeMap<String, Double>();
		Map<String, Double> ClusterAlrDelnum = new TreeMap<String, Double>();
		CptClusterRadius();

		//System.out.println(" ClusterFilterThreshold     : "+ClusterFilterThreshold.size());
		
		Set<Map.Entry<String, Map<Integer, Double>>> TrainpointSet = Trainpoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainpointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			TrainFileFlagMapVsm.put(me.getKey(), 1);
		}
		//System.out.println(" TrainFileFlagMapVsm     : "+TrainFileFlagMapVsm.size());
		CateMap.clear();
		CateCenterMap = GetCenterPoint(Trainpoint, CateMap, TrainFileFlagMapVsm);
		Set<Map.Entry<String, Map<String, Integer>>> CateMapSet2 = CateMap.entrySet();
		for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet2.iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, Integer>> me = it.next();
			double maxnum = percent*CatePointName.get(me.getKey()).size();
			ClusterDelMaxnum.put(me.getKey(), maxnum);
			ClusterAlrDelnum.put(me.getKey(), 0.0);
			Total += maxnum;
		}
		
		while (!(((IterationDel==0)&&(R==maxphi))||(Total<TotalDel+1))){
			IterationNum++;
			IterationDel = 0;
			
			Set<Map.Entry<String, Double>> ClusterRadiusSet = ClusterRadius.entrySet();
			for (Iterator<Map.Entry<String, Double>> it = ClusterRadiusSet.iterator(); it.hasNext();) {
				Map.Entry<String, Double> me = it.next();
				//System.out.println( me.getKey()+"  : "+phi*me.getValue());
				R = phi+(IterationNum-1)*delt;
				R = R>maxphi?maxphi:R;
				ClusterFilterThreshold.put(me.getKey(), R*me.getValue());
			}
			
			CateMap.clear();
			CateCenterMap = GetCenterPoint(Trainpoint, CateMap, TrainFileFlagMapVsm);
			System.out.println(" CateMap      : "+CateMap.size());
			Set<Map.Entry<String, Map<String, Integer>>> CateMapSet = CateMap.entrySet();
			for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet.iterator(); it.hasNext();) {
				Map.Entry<String, Map<String, Integer>> me = it.next();
				Set<Map.Entry<String, Integer>> allVsmSet2 = me.getValue().entrySet();
				for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
					Map.Entry<String, Integer> me2 = it2.next();
					distance = 1-computeSim(CateCenterMap.get(me.getKey()), Trainpoint.get(me2.getKey()));
					//distance = DataPreProcess.EuclideanDistance(CateCenterMap.get(me.getKey()), Trainpoint.get(me2.getKey()));
					if((distance<(ClusterFilterThreshold.get(me.getKey())))&&
					(ClusterAlrDelnum.get(me.getKey())<ClusterDelMaxnum.get(me.getKey()))){
						TrainFileFlagMapVsm.put(me2.getKey(), 0);
						IterationDel++;
						TotalDel++;
						ClusterAlrDelnum.put(me.getKey(),ClusterAlrDelnum.get(me.getKey())+1);
					}			
				}
				//System.out.println("-----------"+ClusterAlrDelnum.get(me.getKey()));
			}
			if(IterationDel>0){
			    System.out.println("---------------------"+IterationNum+"--------------------"+R);
				System.out.println(" IterationDel : "+IterationDel);
				System.out.println(" TotalDel     : "+TotalDel);
				if((IterationNum<100)||(IterationNum%100==0)){
					TrainFilePrint(IterationNum, TrainFileFlagMapVsm);
				}
			}
		}
		
		Set<Map.Entry<String, Map<Integer, Double>>> TrainpointSet1 = Trainpoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainpointSet1.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			if(1==TrainFileFlagMapVsm.get(me.getKey())){
			    TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
				Set<Map.Entry<Integer, Double>> cSet = me.getValue().entrySet();
				for (Iterator<Map.Entry<Integer, Double>> cit = cSet.iterator(); cit.hasNext();) {
					Map.Entry<Integer, Double> cme = cit.next();
					tempMap.put(cme.getKey(), cme.getValue());
				}
				TrainFileMap.put(me.getKey(), tempMap);
			}
		}
		System.out.println("TrainFileMap    size: "+TrainFileMap.size());
		try {
			printSIDMapForSE("./DataMiningSample/SE","/ReducedmapvsmLast.txt",TrainFileMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return TrainFileMap;
	}
	
	public void TrainFilePrint(int IterationNum, Map<String, Integer> TrainFileFlagMap)
	{
		Map<String, Map<Integer, Double>> TrainFileMap  = new TreeMap<String, Map<Integer, Double>>();
		Set<Map.Entry<String, Map<Integer, Double>>> TrainpointSet1 = Trainpoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainpointSet1.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			if(1==TrainFileFlagMap.get(me.getKey())){
			    TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
				Set<Map.Entry<Integer, Double>> cSet = me.getValue().entrySet();
				for (Iterator<Map.Entry<Integer, Double>> cit = cSet.iterator(); cit.hasNext();) {
					Map.Entry<Integer, Double> cme = cit.next();
					tempMap.put(cme.getKey(), cme.getValue());
				}
				TrainFileMap.put(me.getKey(), tempMap);
			}
		}
		
		try {
			printSIDMapForSE("./DataMiningSample/SE","/Reducedmapvsm_"+IterationNum+".txt",TrainFileMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printSIDMapForSE(String strDir, String fileName,
			Map<String, Map<Integer, Double>> wordMap) throws IOException {
		System.out.println("printSIDMap:" + strDir + fileName);
		int countLine = 0;
		File FileDir = new File(strDir);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
		
		File outPutFile = new File(strDir + fileName);
		FileWriter outPutFileWriter = new FileWriter(outPutFile);

		Set<Map.Entry<String, Map<Integer, Double>>> wordMapSet = wordMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = wordMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			outPutFileWriter.write(me.getKey() + " ");
		
			Set<Map.Entry<Integer, Double>> allWords = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allWords.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();
				outPutFileWriter.write(me2.getValue() + " ");
				
			}
			outPutFileWriter.write("\n");
			countLine++;
		}
		outPutFileWriter.flush();
		outPutFileWriter.close();
		System.out.println(fileName + " size is " + countLine);
	}

}
