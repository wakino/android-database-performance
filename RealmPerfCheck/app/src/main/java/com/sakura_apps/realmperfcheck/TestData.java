package com.sakura_apps.realmperfcheck;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class TestData extends RealmObject {
	@PrimaryKey
	private long id;
	private int normalNumber;
	@Index
	private int indexedNumber;
	private String normalText;
	@Index
	private String indexedText;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNormalNumber() {
		return normalNumber;
	}

	public void setNormalNumber(int normalNumber) {
		this.normalNumber = normalNumber;
	}

	public int getIndexedNumber() {
		return indexedNumber;
	}

	public void setIndexedNumber(int indexedNumber) {
		this.indexedNumber = indexedNumber;
	}

	public String getNormalText() {
		return normalText;
	}

	public void setNormalText(String normalText) {
		this.normalText = normalText;
	}

	public String getIndexedText() {
		return indexedText;
	}

	public void setIndexedText(String indexedText) {
		this.indexedText = indexedText;
	}
}
