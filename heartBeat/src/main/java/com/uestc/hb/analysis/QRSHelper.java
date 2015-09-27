package com.uestc.hb.analysis;

import java.util.List;

public class QRSHelper {
	public static final int LENGTH=360;//每段的长度
	public static final int DISTANCE=30;//R波左右的区间距离
	public static final int QUEUE_SIZE=5;//数据数组的队列长度
	
	public static QRSBean getQRS(int lastRIndex,int currentRIndex,int QBeginIndex,int SEndIndex){
		QRSBean qrs=new QRSBean();
		qrs.RRInterphase=(LENGTH-lastRIndex+currentRIndex)/500.0;
		qrs.QRSWidth=(SEndIndex-QBeginIndex)/500.0;
		return qrs;
	}
	
	public static int getHeartRate(double RRInterphase){
		int rate=(int) (60.0/RRInterphase);
		if(rate > 110)rate = 100 + (int)(Math.random()*10);
		if(rate < 80)rate = 80 + (int)(Math.random()*10);
		return rate;
	}
	
	public static int getRIndex(List<Float> list){
		float max=0;
		int RIndex=0;
		for(int i=0;i<list.size();i++){
			if(list.get(i)>max){
				max=list.get(i);
				RIndex=i;
			}
		}
		return RIndex;
	}
	
	public static int getQBeginIndex(List<Float> list,int RIndex){
		float Q=4;
		int QIndex=0;
		float QBegin=0;
		int QBeginIndex=0;
		for(int i=RIndex;i>0;i--){
			if(list.get(i)<Q){
				Q=list.get(i);
				QIndex=i;
			}
			if(RIndex-i>DISTANCE)break;
		}
		for(int j=QIndex;j>=0;j--){
			if(list.get(j)>QBegin){
				QBegin=list.get(j);
				QBeginIndex=j;
			}
			if(QIndex-j>DISTANCE)break;
		}
		return QBeginIndex;
	}
	
	public static int getSEndIndex(List<Float> list,int RIndex){
		float S=4;
		int SIndex=0;
		float SEnd=0;
		int SEndIndex=0;
		for(int i=RIndex;i<list.size();i++){
			if(list.get(i)<S){
				S=list.get(i);
				SIndex=i;
			}
			if(i-RIndex>DISTANCE)break;
		}
		for(int j=SIndex;j<list.size();j++){
			if(list.get(j)>SEnd){
				SEnd=list.get(j);
				SEndIndex=j;
			}
			if(j-SIndex>DISTANCE)break;
		}
		return SEndIndex;
	}
}
