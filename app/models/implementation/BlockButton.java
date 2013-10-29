package models.implementation;

import models.BlockButtonInterface;

public class BlockButton implements BlockButtonInterface{
	private int index, type;
	
	public BlockButton(int pType){
		type = pType;
	}
	
	@Override
	public void setIndex(int pIndex){
		index = pIndex;
	}
	
	@Override
	public int getType(){
		return type;
	}
	
	@Override
	public int getIndex(){
		return index;
	}

	@Override
	public void setType(int pType) {
		type = pType;
	}
}
