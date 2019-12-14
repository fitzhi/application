/**
 * 
 */
package fr.skiller.data.source;

import static fr.skiller.Global.LN;
import static fr.skiller.Error.CODE_CONTRIBUTOR_INVALID;
import static fr.skiller.Error.MESSAGE_CONTRIBUTOR_INVALID;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.StaffHandler;
import fr.skiller.bean.impl.PropectDashboardCustomizerImpl;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;
import fr.skiller.security.CustomAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Repository containing time-stamped commits for the given project.
 * <br/>
 * <i>This is the first & basic implementation for the Commit repository</i>.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
public class BasicCommitRepository implements CommitRepository {

	/**
	 * Map of commits identified by source code path.
	 */
	Map<String, CommitHistory> repo = new HashMap<>();
	
	/**
	 * This set contains the developers/contributors retrieved in the repository 
	 * but unrecognized during the parsing process.
	 */
	Set<String> unknownContributors = new HashSet<>();
		
	@Override
	public void addCommit(String sourceCodePath, int idStaff, String authorName, LocalDate dateCommit, long importance) {
		
		if (repo.containsKey(sourceCodePath)) {
			final CommitHistory history = repo.get(sourceCodePath);
			history.handle(idStaff, authorName, dateCommit);
		} else {
			CommitHistory fileLogs = new CommitHistory(sourceCodePath, importance);
			fileLogs.addOperation(new Operation(idStaff, authorName, dateCommit));
			repo.put(sourceCodePath, fileLogs);
		}
	}
	
	@Override
	public void addCommit(String sourceCodePath, int idStaff, String authorName, Date dateCommit, long importance) {
		addCommit(sourceCodePath, idStaff, authorName, dateCommit.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), importance);
	}
	
	@Override
	public void onBoardStaff(StaffHandler staffHandler, Staff staff) {
		this.getRepository().values().stream().forEach(history -> {
			history.operations.forEach(operation -> {
				if (staffHandler.isEligible(staff, operation.getAuthorName())) {
					
					//
					// This case is possible in case of an on-boarding post creation
					// Temporary, 2 different developers might be present
					// The complete rebuilt will later rectify this state
					//
					if (operation.getIdStaff() == -1) {
						operation.setIdStaff(staff.getIdStaff());
					}
				}
			});
		});
		
		//
		// There might be duplicates at a given date with the same Staff identifier. We remove the useless operations.
		//
		this.getRepository().values()
				.stream()
				.forEach(history -> PropectDashboardCustomizerImpl.removeDuplicateEntries(history.operations));
		
	}

	@Override
	public Contributor extractContribution(Staff staff) {
		
		int numberOfCommits = 0;
		int numberOfFiles = 0;
		LocalDate lastCommit = LocalDate.MIN; 
		LocalDate firstCommit = LocalDate.MAX; 

		for (CommitHistory history : repo.values()) {
			boolean detected = false;
			for (Operation operation : history.operations) {
				
				if (operation.getIdStaff() == staff.getIdStaff()) {
					if (!detected) {
						detected = true;
						numberOfFiles++;
					}
					// No else : the first detected operation has to be taken in account.
					if (detected) {
						numberOfCommits++;
						if (operation.getDateCommit().isBefore(firstCommit)) {
							firstCommit = operation.getDateCommit();
						}
						if (operation.getDateCommit().isAfter(lastCommit)) {
							lastCommit = operation.getDateCommit();
						}
					}
				}
			}
		}
		if (numberOfCommits == 0) {
			if (log.isWarnEnabled()) {
				log.warn(String.format("Cannot create an empty contributor for the staff member %s", staff.fullName()));
			}
		}
		
		return (numberOfCommits == 0) ? null :
				new Contributor(staff.getIdStaff(), firstCommit, lastCommit, numberOfCommits, numberOfFiles);
	}
	
	@Override
	public boolean containsSourceCode(String sourceCodePath) {
		return repo.containsKey(sourceCodePath);
	}

	@Override
	public LocalDate getLastDateCommit(final String sourceCodePath, final int author) {
		if (repo.containsKey(sourceCodePath)) {
			final CommitHistory history = repo.get(sourceCodePath);
			return history.getDateCommit(author);
		} else {
			return null;
		}
	}

	@Override
	public String extractCSV() {
		final StringBuilder sb = new StringBuilder();
		repo.values().stream().forEach(history -> {
			history.operations.stream().forEach(
					operation -> sb.append(history.sourcePath)
					.append(";")
					.append(operation.idStaff)
					.append(";")
					.append(operation.getDateCommit())
					.append(LN)
					);
		});
		return sb.toString();
	}

	@Override
	public int size() {
		return repo.size();
	}

	@Override
	public Map<String, CommitHistory> getRepository() {
		return this.repo;
	}

	/**
	 * Working variable used by method {@link #lastCommit(int)}
	 */
	private LocalDate lastCommit;
	
	@Override
	public LocalDate lastCommit (final int idStaff) {
		lastCommit = LocalDate.MIN; 
		this.repo.values().stream().forEach(history -> 
			history.operations.stream()
			.filter(ope -> (ope.idStaff == idStaff)).forEach(ope -> {
				if (ope.getDateCommit().isAfter (lastCommit)) {
					lastCommit = ope.getDateCommit();
				}
			}));
		return lastCommit;
	}

	@Override
	public LocalDate firstCommit (final int idStaff) {
		lastCommit = LocalDate.MAX; 
		this.repo.values().stream().forEach(history -> 
			history.operations.stream()
			.filter(ope -> (ope.idStaff == idStaff)).forEach(ope -> {
				if (ope.getDateCommit().isBefore(lastCommit)) {
					lastCommit = ope.getDateCommit();
				}
			}));
		return lastCommit;
	}
	
	@Override
	public int numberOfFileCommits (final int idStaff) {
		return (int) this.repo.values().stream()
				.mapToLong( 
					 history -> history.operations.stream()
					.filter(ope -> (ope.idStaff == idStaff))
					.count())
				.asDoubleStream()
				.sum();
	}
	
	
	@Override
	public int numberOfFiles (final int idStaff) {
		return (int) this.repo.values()
				.stream()
				.filter(history -> history.hasWorkedOnThisFile(idStaff))
				.count();
	}
	
	@Override
	public Set<String> unknownContributors() {
		return this.unknownContributors;
	}

	@Override
	public void setUnknownContributors(Set<String> unknowns) {
		this.unknownContributors = unknowns;
	}

	@Override
	public List<String> extractMatchingUnknownContributors(StaffHandler staffHandler, Staff staff) {
		List<String> list = new ArrayList<>();
		for (String contributor : this.unknownContributors()) {
			if (staffHandler.isEligible(staff, contributor)) {
				list.add(contributor);
			}
		}
		return list;
	}

	@Override
	public void dump() {
		StringBuilder sb = new StringBuilder();
		this.repo.values().forEach(history -> {
			sb.append(history.sourcePath).append(LN);
			history.operations.stream().forEach((Operation ope) -> {
				sb.append("\t").append(ope.getIdStaff()).append(" ");
				sb.append(ope.getAuthorName()).append(" ");
				sb.append(ope.getDateCommit()).append(LN);
			});
		});
		System.out.println(sb.toString());
	}
}
