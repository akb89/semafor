package edu.unige.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.SerializedObjects;
import gnu.trove.THash;
import gnu.trove.THashMap;
import gnu.trove.TObjectHash;

import java.util.Map;

/**
 * @author Alex Kabbach
 */
public class ReqDataTest {

	public static final String SEMAFOR_DIR = "/Users/AKB/Dropbox/GitHub/semafor/";
	public static final String RESOURCES_DIR = SEMAFOR_DIR + "resources/";
	public static final String EXPERIMENT_DIR = SEMAFOR_DIR
			+ "experiments/acl2015_baseline_replication_with_homemade_preprocessing/";
	public static final String MODEL_DIR = EXPERIMENT_DIR + "model/";
	public static final String oldHVFile = RESOURCES_DIR + "hvlemmas.ser";
	public static final String newHVFile = MODEL_DIR + "hvlemmas.ser";

	public static void main(String[] args){
		THashMap<TObjectHash, THash> oldHVMap = (THashMap<TObjectHash, THash>) SerializedObjects
				.readSerializedObject(oldHVFile);
		THashMap<TObjectHash, THash> newHVMap = (THashMap<TObjectHash, THash>) SerializedObjects
				.readSerializedObject(newHVFile);

		for (Map.Entry<TObjectHash, THash> entry : oldHVMap.entrySet()) {
			//System.out.println(entry.getKey());
			//System.out.println(entry.getValue());
			if(!newHVMap.containsKey(entry)){
				System.out.println("Old entry not in new Map = " + entry.getKey());
			}
		}
		for (Map.Entry<TObjectHash, THash> entry : newHVMap.entrySet()) {
			System.out.println(entry.getKey());
			//System.out.println(entry.getValue());
		}
		System.out.println("OldHVMap size = " + oldHVMap.size());
		System.out.println("NewHVMap size = " + newHVMap.size());
	}
}
