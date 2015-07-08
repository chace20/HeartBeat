package com.uestc.hb.common;

public class IllnessType {
	//正常
	public static final int NORMAL = 0x1;
	// 窦性心动过缓
	public static final int SINUS_BRADYCARDIA = 0x2;
	// 窦性心动过速
	public static final int NODAL_TACHYCARDIA = 0x3;
	// 停搏
	public static final int ARREST = 0x4;
	// 漏搏
	public static final int MISSED_BEAT = 0x5;
	// 室性早搏
	public static final int PVB = 0x6;
	// 二联律
	public static final int BIGEMINY = 0x7;
	// 三联律
	public static final int TRIGEMINY = 0x8;
	// R on T
	public static final int R_ON_T = 0x9;
	// 室性心动过速
	public static final int VT = 0xA;
	// 室性扑动
	public static final int VENTRICULAR_FLUTTER = 0xB;
	// 窦性心律不齐
	public static final int SINUS_IRREGULARITY = 0xC;
	
	public static String getIllnessTypeString(int type){
		switch(type){
		case SINUS_BRADYCARDIA:
			return "窦性心动过缓";
		case NODAL_TACHYCARDIA:
			return "窦性心动过速";
		case ARREST:
			return "停搏";
		case MISSED_BEAT:
			return "漏搏";
		case PVB:
			return "室性早搏";
		case BIGEMINY:
			return "二联律";
		case TRIGEMINY:
			return "三联律";
		case R_ON_T:
			return "R on T";
		case VT:
			return "室性心动过速";
		case VENTRICULAR_FLUTTER:
			return "室性扑动";
		case SINUS_IRREGULARITY:
			return "窦性心律不齐";
		default:
			return "正常，怎么可能嘛？";
		}
	}
}
