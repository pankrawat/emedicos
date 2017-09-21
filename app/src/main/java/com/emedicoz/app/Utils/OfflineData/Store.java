package com.emedicoz.app.Utils.OfflineData;



public abstract class Store {

	public static final int STORE_SUCCESS = 0;
	public static final int STORE_FAIL = -1;
	public static final int STORE_OPEN_FAIL = -100;
	public static final int STORE_LOAD_FAIL = -200;
	public static final int STORE_CLOSE_FAIL = -300;
	public static final int STORE_NOT_FOUND = -400;
	public static final int STORE_DELETE_RECORD_FAILED = -500;
	public static final int STORE_ADD_RECORD_FAILED = -600;
	public static final int STORE_UPDATE_RECORD_FAILED = -700;
	public static final int STORE_NOT_IMPLEMENTED = -800;
	public static final int STORE_FULL = -900;

	public static final int DATA_SIZE = 1;
	public static final int DATA_SIZE_AVAILABLE = 2;
	public static final int ACTIVE_RECORD_COUNT = 3;
	public static final int TOTAL_RECORD_COUNT = 4;

}



