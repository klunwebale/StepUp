package edu.dance;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		try {
			
			
			System.out.println(new File(".").getCanonicalPath());
//			CreateNodes creationObj = new CreateNodes(GraphUtil.getGraphConnection());
//			creationObj.createMasterMoveNodes();
//			creationObj.createDanceNodes();
//			creationObj.createMoveNodes();
			
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
}
