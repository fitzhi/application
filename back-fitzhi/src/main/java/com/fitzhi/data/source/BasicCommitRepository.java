package com.fitzhi.data.source;

import static com.fitzhi.Global.LN;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.bean.impl.PropectDashboardCustomizerImpl;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Staff;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Repository containing timestamped commits for the given project.
 * <br/>
 * </p>
 * <p>
 * <i>This is the first and basic implementation for the Commit repository</i>.
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
					// This case is possible in case of an on-boarding of an already registered staff member
					// 
					// The record of a staff member might be modified and therefore might match the author name of an operation
					// The sequence might be :
					// Hypothesis
					//   1) The developer John DOO is linked to dew operations identifier by the author trace "John DOO"
					//   2) This developer John DOO has the login attribute "John DOO" in the staff collection
					//   3) There are some records associated to a ghost named "jdoo"
					//
					//   4) End-user change the login of John Doo to "jdoo"
					//   5) We on-board the ghosts once again and John DOO might appear twice the same day on the same source file.
					// 
					// We will remove useless duplicated operation-records later.
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
	public Contributor extractStaffMetrics(Staff staff) {
		BiFunction<Operation, Staff, Boolean> test  = (ope, st) -> ope.getIdStaff() == st.getIdStaff();
		Function<Staff, Contributor> newInstance  = st -> new Contributor (st.getIdStaff());
		return extract(staff, test, newInstance);
	}

	@Override
	public Ghost extractGhostMetrics(Author author) {
		BiFunction<Operation, Author, Boolean> test  = (ope, auth) -> ope.getAuthorName().equals(auth.getName());
		Function<Author, Ghost> newInstance  = auth -> new Ghost(auth.getName(), false);
		return extract(author, test, newInstance);
	}

	/**
	 * Extract the data.
	 * 
	 * <p>
	 * In theory, the function should never return a {@code null}, 
	 * but it appears that this case occurs. Therefore, we put a warning to trace this case and track the issue.
	 * <em>Most probably a filter on files eligibility is the origin of this detect.</em>
	 * </p>
	 * 
	 * @param <R> the returned classname which implements the interface {@link GitMetrics}
	 * @param <T> the passed type
	 * @param t the given object to be used to filter
	 * @param test the filter to be tested on data.
	 * @param create the creation method
	 * 
	 * @return the {@link GitMetrics metrics} implementation
	 */
	public <R extends GitMetrics, T> R extract(
		T t, 
		BiFunction<Operation, T, Boolean> test, 
		Function<T, R> create) {

		int numberOfCommits = 0;
		int numberOfFiles = 0;
		LocalDate lastCommit = LocalDate.MIN; 
		LocalDate firstCommit = LocalDate.MAX; 

		for (CommitHistory history : repo.values()) {
			boolean detected = false;
			for (Operation operation : history.operations) {
				
				if (test.apply(operation, t)) {
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
				log.warn(String.format("Cannot create an empty contributor for %s %s", t.getClass(), t.toString()));
			}
		}
		
		R r = (numberOfCommits == 0) ? null : create.apply(t);

		// If no commit is registered for this committer, we return null.
		if (r != null) {
			r.setFirstCommit(firstCommit);
			r.setLastCommit(lastCommit);
			r.setNumberOfCommits(numberOfCommits);
			r.setNumberOfFiles(numberOfFiles);
		}

		return r;		
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
					operation -> sb.append(history.getSourcePath())
					.append(";")
					.append(operation.getIdStaff())
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
			.filter(ope -> (ope.getIdStaff() == idStaff)).forEach(ope -> {
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
			.filter(ope -> (ope.getIdStaff() == idStaff)).forEach(ope -> {
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
					.filter(ope -> (ope.getIdStaff() == idStaff))
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

	public void removeGhost(String unknownContributor) {
		Iterator<String> ite = this. unknownContributors().iterator();
		while (ite.hasNext()) {
			String ghost = ite.next();
			
			if (unknownContributor.contentEquals(ghost)) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("Removing the ghost %s", ghost));
				}
				ite.remove();
				return;
			}
		}
	}
	
	@Override
	public void dump() {
		StringBuilder sb = new StringBuilder();
		this.repo.values().forEach(history -> {
			sb.append(history.getSourcePath()).append(LN);
			history.operations.stream().forEach((Operation ope) -> {
				sb.append("\t").append(ope.getIdStaff()).append(" ");
				sb.append(ope.getAuthorName()).append(" ");
				sb.append(ope.getDateCommit()).append(LN);
			});
		});
		log.debug(sb.toString());
	}
}
