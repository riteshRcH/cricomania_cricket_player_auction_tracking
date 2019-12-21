package org.csiVesit.cricomania.feeder;

public class CricoManiaPlayer
{
	int playerID, poolID, age, brandValue;
	String playerName, country, typeOfPlayer, photoFilePath;
	boolean isAvailable, isSold;
	double basePrice;
	
	public CricoManiaPlayer(int playerID, int poolID, int age, int brandValue, String playerName, String country, String typeOfPlayer, String photoFilePath, boolean isAvailable, boolean isSold, double basePrice)
	{
		this.playerID = playerID;
		this.poolID = poolID;
		this.age = age;
		this.brandValue = brandValue;
		this.playerName = playerName;
		this.country = country;
		this.typeOfPlayer = typeOfPlayer;
		this.photoFilePath = photoFilePath;
		this.isAvailable = isAvailable;
		this.isSold = isSold;
		this.basePrice = basePrice;
	}

	int getPlayerID() {
		return playerID;
	}

	int getPoolID() {
		return poolID;
	}

	int getAge() {
		return age;
	}

	int getBrandValue() {
		return brandValue;
	}

	String getPlayerName() {
		return playerName;
	}

	String getCountry() {
		return country;
	}

	String getTypeOfPlayer() {
		return typeOfPlayer;
	}

	String getPhotoFilePath() {
		return photoFilePath;
	}

	boolean isAvailable() {
		return isAvailable;
	}

	boolean isSold() {
		return isSold;
	}

	double getBasePrice() {
		return basePrice;
	}
}