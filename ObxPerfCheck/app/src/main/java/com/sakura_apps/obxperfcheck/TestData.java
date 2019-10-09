package com.sakura_apps.obxperfcheck;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
class TestData {
	@Id
	public long id;
	public int normalNumber;
	@Index
	public int indexedNumber;
	public String normalText;
	@Index
	public String indexedText;

}
