package fr.skiller.bean;

/**
 * Interface in charge of the shuffle process to anonymize critical data, for documentation purpose.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface ShuffleService {

	/**
	 * Shuffle the input string into a new one.
	 * @param input the input string
	 * @return the resulting shuffled input
	 */
	String shuffle (final String input);

	/**
	 * This method is for letters, what scramble eggs is chicken.
	 * @param input the input string
	 * @return the resulting rotated input
	 */
	String scramble (final String input);
	
	/**
	 * @return <code>true</code> if the back-end is running in shuffle mode.
	 */
	boolean isShuffleMode();
	
}
