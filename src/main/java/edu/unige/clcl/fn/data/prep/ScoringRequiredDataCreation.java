package edu.unige.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generate frRelations.xml file and frames.xml file used by the bin/score/score.pl script
 * @author Alex Kabbach
 */
public class ScoringRequiredDataCreation {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) throws IOException {
		ScoringRequiredDataCreation scoringReqDataC = new ScoringRequiredDataCreation();
		scoringReqDataC.logger.info("Creating frames.xml and feRelations.xml files...");
		final String frameNetDataDir = args[0];
		final String preprocessedDataDir = args[1];
		final String frameDir = frameNetDataDir + "/frame";
		final String frRelationFile = frameNetDataDir + "/frRelation.xml";
		final String outputFramesFile = preprocessedDataDir + "/frames.xml";
		final String outputFrRelationFile = preprocessedDataDir + "/frRelations.xml";
		createFramesXmlFile(frameDir, outputFramesFile);
		createFrRelationsXmlFile(frRelationFile, outputFrRelationFile);
		scoringReqDataC.logger.info("Done creating frames.xml and feRelations.xml files");
	}

	private static Element createFrameImportElement(Document frameDoc, Document frameSingleDoc){
		Element frame = frameDoc.getDocumentElement();
		Node frameImportNode = frameSingleDoc.importNode(frame, false);
		Element frameImportElement = (Element) frameImportNode;
		frameImportElement.removeAttribute("xmlns");
		frameImportElement.removeAttribute("xmlns:xsi");
		frameImportElement.removeAttribute("xsi:schemaLocation");
		Element fes = frameSingleDoc.createElement("fes");
		NodeList frameChildren = frame.getChildNodes();
		boolean hasLexUnits = false;
		for(int i=0; i<frameChildren.getLength(); i++){
			String frameChildNodeName = frameChildren.item(i).getNodeName();
			// Append the definition
			if(frameChildNodeName.equals("definition")){
				frameImportElement.appendChild(frameSingleDoc.importNode(frameChildren.item(i), true));
			}else if(frameChildNodeName.equals("FE")){
				Element semTypes = frameSingleDoc.createElement("semTypes");
				Node feNode = frameSingleDoc.importNode(frameChildren.item(i), false);
				NodeList feChildren = frameChildren.item(i).getChildNodes();
				for(int j=0; j<feChildren.getLength(); j++){
					String feChildNodeName = feChildren.item(j).getNodeName();
					// Append the definition
					if(feChildNodeName.equals("definition")){
						feNode.appendChild(frameSingleDoc.importNode(feChildren.item(j), true));
					}
					// Append the semTypes
					if(feChildNodeName.equals("semType")){
						semTypes.appendChild(frameSingleDoc.importNode(feChildren.item(j), true));
					}
					// Append excludesFE
					if(feChildNodeName.equals("excludesFE")){
						feNode.appendChild(frameSingleDoc.importNode(feChildren.item(j), true));
					}
					// Append requiresFE
					if(feChildNodeName.equals("requiresFE")){
						feNode.appendChild(frameSingleDoc.importNode(feChildren.item(j), true));
					}
				}
				feNode.appendChild(semTypes);
				fes.appendChild(feNode);
			}else if(frameChildNodeName.equals("lexUnit")){
				hasLexUnits = true;
			}
		}
		frameImportElement.appendChild(fes);
		if(hasLexUnits){
			Element lexunits = frameSingleDoc.createElement("lexunits");
			frameImportElement.appendChild(lexunits);
		}
		return frameImportElement;
	}

	private static void createFramesXmlFile(String frameDir, String outputFile) throws IOException {
		Document frameSingleDoc = XmlUtils.getNewDocument();
		Element framesRootElement = frameSingleDoc.createElement("frames");
		frameSingleDoc.appendChild(framesRootElement);
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date dateObj = new Date();
		framesRootElement.setAttribute("XMLCreated", dateFormat.format(dateObj));
		Files.walk(Paths.get(frameDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.getFileName().toString().endsWith(".xml")) {
				Document frameDoc = XmlUtils.parseXmlFile(filePath.toString(), false);
				if(frameDoc != null){
					Element frameImportElement = createFrameImportElement(frameDoc, frameSingleDoc);
					framesRootElement.appendChild(frameImportElement);
				}
			}
		});
		NodeList feNodes = frameSingleDoc.getElementsByTagName("FE");
		for (int i = 0; i < feNodes.getLength(); i++) {
			frameSingleDoc.renameNode(feNodes.item(i), null, "fe");
		}
		XmlUtils.writeXML(outputFile, frameSingleDoc);
	}


	private static void createFrRelationsXmlFile(String frRelationFile, String outputFile){
		Document frRelationDoc = XmlUtils.parseXmlFile(frRelationFile, false);
		NodeList frRelationTypeNodeList = frRelationDoc.getElementsByTagName("frameRelationType");
		for(int i=0; i<frRelationTypeNodeList.getLength(); i++){
			Element frameRelationsElement = frRelationDoc.createElement("frame-relations");
			while(frRelationTypeNodeList.item(i).hasChildNodes()){
				Node firstChild = frRelationTypeNodeList.item(i).getFirstChild();
				if(firstChild.getNodeName().equals("frameRelation")){
					frameRelationsElement.appendChild(firstChild);
				}
				frRelationTypeNodeList.item(i).removeChild(frRelationTypeNodeList.item(i).getFirstChild());
			}
			frRelationTypeNodeList.item(i).appendChild(frameRelationsElement);
		}
		// Rename frameRelations --> fr-relations
		NodeList frameRelationsNodes = frRelationDoc.getElementsByTagName("frameRelations");
		for (int i = 0; i < frameRelationsNodes.getLength(); i++) {
			frRelationDoc.renameNode(frameRelationsNodes.item(i), null, "fr-relations");
		}
		// Rename frameRelationType --> frame-relation-type
		NodeList frameRelationsTypeNodes = frRelationDoc.getElementsByTagName("frameRelationType");
		for (int i = 0; i < frameRelationsTypeNodes.getLength(); i++) {
			frRelationDoc.renameNode(frameRelationsTypeNodes.item(i), null, "frame-relation-type");
		}
		// Rename frameRelation --> frame-relation
		NodeList frameRelationNodes = frRelationDoc.getElementsByTagName("frameRelation");
		for (int i = 0; i < frameRelationNodes.getLength(); i++) {
			frRelationDoc.renameNode(frameRelationNodes.item(i), null, "frame-relation");
		}
		// Rename FERelation --> fe-relation
		NodeList feRelatioNodes = frRelationDoc.getElementsByTagName("FERelation");
		for (int i = 0; i < feRelatioNodes.getLength(); i++) {
			frRelationDoc.renameNode(feRelatioNodes.item(i), null, "fe-relation");
		}
		XmlUtils.writeXML(outputFile, frRelationDoc);
	}


}
