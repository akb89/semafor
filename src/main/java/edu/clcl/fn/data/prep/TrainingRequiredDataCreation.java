package edu.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.SerializedObjects;
import edu.cmu.cs.lti.ark.util.XmlUtils;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.Map;

/**
 * @author Alex Kabbach
 */
public class TrainingRequiredDataCreation {

	public static void main(String[] args) throws IOException {
		//final String frameNetDataDir = args[0];
		//final String preprocessedDataDir = args[1];
		final String frameNetDataDir = "/Users/AKB/Dropbox/FrameNetData/fndata-1.5";
		final String preprocessedDataDir = "/Users/AKB/Dropbox/GitHub/semafor/data";
		final String outputFramesFile = preprocessedDataDir + "/frames.xml";
		THashMap<String,THashSet<String>> newMap = createFrameFEMap(outputFramesFile);
		THashMap<String,THashSet<String>> oldMap = (THashMap<String,THashSet<String>>) SerializedObjects
				.readSerializedObject("/Users/AKB/Dropbox/GitHub/semafor/data/framenet.frame.element.map.old");
		THashMap<String,THashSet<String>> originalMap = (THashMap<String,THashSet<String>>)SerializedObjects
				.readSerializedObject("/Users/AKB/Dropbox/GitHub/semafor/data/framenet.original.map.old");
		System.out.println(originalMap);
		//compareMaps(oldMap, newMap);
	}

	/**
	 * Populates the given map object with frames (as keys) and sets of target words that evoke
	 * those frames in the given corresponding sentences (as values)
	 * @param map
	 * @param frames
	 * @param sentences
	 * @author dipanjan
	 */

	private static void compareMaps(THashMap<String,THashSet<String>> originalMap, THashMap<String,THashSet<String>> newMap){
		System.out.println("New Map Size = "+newMap.size());
		System.out.println("Old Map Size = "+originalMap.size());
		int counter = 0;
		for(Map.Entry<String, THashSet<String>> entry : newMap.entrySet()){
			if(!originalMap.containsKey(entry.getKey())){
				System.out.println("Original map does not contain frame: "+entry.getKey());
				counter+=1;
			}
		}
		for(Map.Entry<String, THashSet<String>> entry : originalMap.entrySet()){
			if(!newMap.containsKey(entry.getKey())){
				System.out.println("New map does not contain frame: "+entry.getKey());
			}
		}
		System.out.println("Counter = "+counter);
	}

	private static THashMap<String,THashSet<String>> createFrameFEMap(String framesXmlFile){
		System.out.println(framesXmlFile);
		THashMap<String,THashSet<String>> frameFEMap = new THashMap<>();
		Document framesDoc = XmlUtils.parseXmlFile(framesXmlFile, false);
		Element frames = framesDoc.getDocumentElement();
		NodeList frameNodes = frames.getChildNodes();
		for(int i=0; i<frameNodes.getLength(); i++){
			THashSet<String> feSet = new THashSet<>();
			boolean hasLexUnits = false;
			if(frameNodes.item(i).getNodeName().equals("frame")){
				Element frameNodeElement = (Element)frameNodes.item(i); // Only frame nodes
				NodeList frameNodeChildren = frameNodeElement.getChildNodes();
				for(int j=0; j<frameNodeChildren.getLength(); j++){
					if(frameNodeChildren.item(j).getNodeName().equals("fes")){
						Node fesNode = frameNodeChildren.item(j);
						NodeList fesNodeChildren = fesNode.getChildNodes();
						for(int k=0; k<fesNodeChildren.getLength(); k++){
							if(fesNodeChildren.item(k).getNodeName().equals("fe")){
								Element feElement = (Element)fesNodeChildren.item(k);
								feSet.add(feElement.getAttribute("name"));
							}
						}
					}else if(frameNodeChildren.item(j).getNodeName().equals("lexunits")){
						hasLexUnits = true;
					}
				}
				if(hasLexUnits){
					frameFEMap.put(frameNodeElement.getAttribute("name"), feSet);
				}
			}
		}
		return frameFEMap;
	}
}
