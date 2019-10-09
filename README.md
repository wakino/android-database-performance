# ObjectBox performance test on android

This program evaluates the performance of [ObjectBox](https://objectbox.io/) on Android.
It only evaluates __select time__, Insert and update, delete time are not our scope.

Test data is following
```java
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

```

Once you open Application, it creates 10000 records and end.
Next open, it evaluates select-time at first data and at 5K, 10K and then display it on the application view.
Abrreviations on this view are following.
- NN: Normal Number(Not indexed)
- IN: Indexed Number
- NT: Normal Text(Not indexed)
- IT: Indexed Text








