package nl.jeldertpol.xtc.server.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Representation of a project.
 * 
 * @author Jeldert Pol
 */
public class Project {

	String projectName;
	Long revision;
	HashMap<String, LinkedList<Change>> changes;
	
	/**
	 * 
	 */
	public Project(String project, Long revision, ArrayList<String> resources) {
		// naam project, revision, resources, changes to resources,
		super();
		this.projectName = project;
		this.revision = revision;
		changes = new HashMap<String, LinkedList<Change>>(resources.size());
		for (String resource : resources) {
			changes.put(resource, new LinkedList<Change>());
		}
	}

	/**
	 * @return the project
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return the revision
	 */
	public Long getRevision() {
		return revision;
	}
	
}
