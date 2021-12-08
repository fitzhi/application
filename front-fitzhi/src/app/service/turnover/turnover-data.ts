/**
 * This class contains all intermediate data to process the turnover and the resulting turnover as well.
 * 
 * There should be one TurnoverData per year.
 */
export class TurnoverData {

    public static NO_DATA_AVAILABLE = -1;
   
	public static noDataAvailable(year: number): TurnoverData {
		return new TurnoverData(year, 0, 0, 0, TurnoverData.NO_DATA_AVAILABLE);
	}

	constructor(
		public year,
		public arrival = 0,
		public resignation = 0,
		public total = 0,
		public calculation = 0) {}
}
