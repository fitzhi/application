package fr.skiller.data.external;

import java.util.List;

import fr.skiller.data.internal.Committer;
import fr.skiller.data.internal.DataChart;
import fr.skiller.data.internal.RiskDashboard;

/**
 * <p>This class is used as a Data Transfer Object between the spring boot
 * application and the Angular front.</p> 
 * <p><i>FIXME one day : I did not find a way to use HTTP headers for transferring additional information <b>in the POST request</b>. 
 * So these data are embedded in the data transfer object.</i></p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class SunburstDTO extends BaseDTO {

	/**
	 * Project risk livel.
	 */
	private int projectRiskLevel;
	
	/**
	 * Project identifier.
	 */
	private int idProject;
	
	/**
	 * Data ready made for the sunburst chart.
	 */
	private DataChart sunburstData;

	/**
	 * Array of unknown contributors who are unknown in the staff collection.
	 */
	private List<Committer> ghosts;
	
	/**
	 * @param idProject project identifier.
	 * @param projectRiskLevel evaluated risk level of the whole project.
	 * @param riskDashboard Dashboard data ready to be injected in the sunburst chart.
	 */
	public SunburstDTO(final int idProject, final int projectRiskLevel, RiskDashboard riskDashboard) {
		super();
		this.setIdProject(idProject);
		this.projectRiskLevel = projectRiskLevel;
		this.setSunburstData(riskDashboard.riskChartData);
		this.setGhosts(riskDashboard.undefinedContributors);
	}

	/**
	 * @param idProject project identifier.
	 * @param projectRiskLevel evaluated risk level of the whole project.
	 * @param sunburstData data ready to be injected in the sunburst chart.
	 * @param code code of processing error
	 * @param message corresponding message of error
	 */
	public SunburstDTO(final int idProject, final int projectRiskLevel, DataChart sunburstData, int code, String message) {
		super();
		this.setIdProject(idProject);
		this.projectRiskLevel = projectRiskLevel;		
		this.setSunburstData(sunburstData);
		this.code = code;
		this.message = message;
	}

	/**
	 * @param idProject project identifier.
	 * @param projectRiskLevel evaluated risk level of the whole project.
	 * @param code code of processing error
	 * @param message corresponding message of error
	 */
	public SunburstDTO(final int idProject, final int projectRiskLevel, int code, String message) {
		this(idProject, projectRiskLevel, null, code, message);
	}
	
	/**
	 * Empty constructor.
	 */
	public SunburstDTO() { 
	}

	/**
	 * @return the idProject
	 */
	public int getIdProject() {
		return idProject;
	}

	/**
	 * @param idProject the idProject to set
	 */
	public void setIdProject(int idProject) {
		this.idProject = idProject;
	}

	/**
	 * @return the sunburstData
	 */
	public DataChart getSunburstData() {
		return sunburstData;
	}

	/**
	 * @param sunburstData the sunburstData to set
	 */
	public void setSunburstData(DataChart sunburstData) {
		this.sunburstData = sunburstData;
	}

	/**
	 * @return the ghosts
	 */
	public List<Committer> getGhosts() {
		return ghosts;
	}

	/**
	 * @param ghosts the ghosts to set
	 */
	public void setGhosts(List<Committer> ghosts) {
		this.ghosts = ghosts;
	}

	/**
	 * @return the projectRiskLevel
	 */
	public int getProjectRiskLevel() {
		return projectRiskLevel;
	}

	/**
	 * @param projectRiskLevel the projectRiskLevel to set
	 */
	public void setProjectRiskLevel(int projectRiskLevel) {
		this.projectRiskLevel = projectRiskLevel;
	}
	
	
}
