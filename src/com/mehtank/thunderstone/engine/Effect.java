package com.mehtank.thunderstone.engine;

public abstract class Effect {
	public enum EffectType {
		VILLAGE, 
		DUNGEON, 
		REPEATVILLAGE, 
		REPEATDUNGEON, 
		VILLAGEDUNGEON, 
		ALWAYSVILLAGE, 
		ALWAYSDUNGEON, 
		BATTLE, 
		BREACH}
	
	String text = "";
	String success = "";
	String failure = "";


	public Effect setText(String text) { this.text = text; return this; }
	public Effect setSuccess(String success) { this.success = success; return this; }
	public Effect setFailure(String failure) { this.failure = failure; return this; }

	public String getText() { return text; }
	public String getSuccess() { return success; }
	public String getFailure() { return failure; }

	public abstract boolean effect(Context context);

}
