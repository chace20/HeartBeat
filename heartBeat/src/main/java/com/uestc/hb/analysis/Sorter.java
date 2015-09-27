package com.uestc.hb.analysis;

import java.util.ArrayList;
import java.util.List;

import com.uestc.hb.common.IllnessType;

public class Sorter {
	private double RRAverage = 0;
	private double QRSSize = 0;// 用int会导致丢失有效位数
	
	private List<Integer> illnessList = new ArrayList<>();
	
	//判断窦性心动过速过缓
	private int slowSize=0;
	private int fastSize=0;
	//判断窦性心律不齐
	private double maxRR=0.8;
	private double minRR=0.7;
	
	//判断室性扑动
	private int VFSize=0;
	//判断室性心动过速
	private int VTSize=0;
	//判断二联律
	private int BGSize=0;
	//判断三联律
	private int TGSize=0;
	//判断漏搏
	private int MBSize=0;
	//判断室性早搏
	private int PVBSize=0;

	public List<Integer> analysis(QRSBean qrs) {
		if(!illnessList.isEmpty()){
			illnessList.clear();
		}
		QRSSize++;
		//窦性心动过缓
		if(qrs.RRInterphase<0.5){
			slowSize++;
			if(slowSize==4){
				illnessList.add(IllnessType.SINUS_BRADYCARDIA);
			}
		}else{
			slowSize=0;
		}
		//窦性心动过速
		if(qrs.RRInterphase>1.2){
			fastSize++;
			if(fastSize==4){
				illnessList.add(IllnessType.NODAL_TACHYCARDIA);
			}
		}else{
			fastSize=0;
		}
		//二联律
		if(qrs.QRSWidth<0.12&&BGSize==0){
			BGSize=1;
		}else if(qrs.QRSWidth>0.12&&BGSize==1){
			BGSize=2;
		}else if(qrs.QRSWidth<0.12&&BGSize==2){
			BGSize=3;
		}else if(qrs.QRSWidth>0.12&&BGSize==3){
			BGSize=0;//清零
			illnessList.add(IllnessType.BIGEMINY);
		}else{
			BGSize=0;
		}
		//三联律
		if(qrs.QRSWidth<0.12&&TGSize==0){
			TGSize=1;
		}else if(qrs.QRSWidth<0.12&&TGSize==1){
			TGSize=2;
		}else if(qrs.QRSWidth>0.12&&TGSize==2){
			TGSize=3;
		}else if(qrs.QRSWidth<0.12&&TGSize==3){
			TGSize=4;
		}else if(qrs.QRSWidth<0.12&&TGSize==4){
			TGSize=5;
		}else if(qrs.QRSWidth>0.12&&TGSize==5){
			TGSize=0;
			illnessList.add(IllnessType.TRIGEMINY);
		}else{
			TGSize=0;
		}
		//室性扑动
		if(qrs.RRInterphase<0.3){
			VFSize++;
			if(VFSize==5){
				VTSize=0;
				illnessList.add(IllnessType.VENTRICULAR_FLUTTER);
			}
		}else{
			VFSize=0;
		}
		//室性心动过速
		if(qrs.QRSWidth>0.12&&qrs.RRInterphase<0.6){
			VTSize++;
			if(VTSize==3){
				VTSize=0;
				illnessList.add(IllnessType.VT);
			}
		}else{
			VTSize=0;
		}
		//停搏
		if (qrs.RRInterphase > 4.0){
			illnessList.add(IllnessType.ARREST);
		}
		if (RRAverage == 0) {
			RRAverage = qrs.RRInterphase;
		} else {
			RRAverage = ((RRAverage * (QRSSize - 1)) + qrs.RRInterphase)
					/ QRSSize;
			//漏搏
			if (qrs.RRInterphase > RRAverage*1.5) {
				MBSize++;
				//连续出现2次才认为是漏搏
				if(MBSize==3){
					MBSize=0;
					illnessList.add(IllnessType.MISSED_BEAT);
				}
			}else{
				MBSize=0;
			}
			//室性早搏
			if (qrs.RRInterphase < RRAverage * 0.8 && qrs.QRSWidth > 0.12) {
				PVBSize++;
				if(PVBSize==3){
					PVBSize=0;
					illnessList.add(IllnessType.PVB);
				}
			}else{
				PVBSize=0;
			}
			//R on T
			if (qrs.RRInterphase>0.2&&qrs.RRInterphase<RRAverage*0.3){
				illnessList.add(IllnessType.R_ON_T);
			}
		}		
		//窦性心律不齐
		if(qrs.RRInterphase<minRR){
			minRR=qrs.RRInterphase;
		}else if(qrs.RRInterphase>maxRR){
			maxRR=qrs.RRInterphase;
		}
		//因为窦性心律不齐普遍存在，所以只有在已经有其他病的情况下才作为一个附加病考虑进去，否则不认为有病。
		if(maxRR-minRR>0.18&&!illnessList.isEmpty()){
			illnessList.add(IllnessType.SINUS_IRREGULARITY);
		}
//		System.out.println("--" + illnessList);
		return illnessList;
	}
}
