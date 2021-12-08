/**
 * This class contains all intermediate data to process the turnover and the resulting turnover as well.
 */
export class TurnoverData {

    public static NO_DATA_AVAILABLE = -1;
   
	public static noDataAvailable(): TurnoverData {
		return new TurnoverData(0, 0, 0, TurnoverData.NO_DATA_AVAILABLE);
	}

	constructor(
		public arrival = 0,
		public resignation = 0,
		public total = 0,
		public calculation = 0) {}
}
