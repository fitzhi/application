import { DashboardConstants } from './dashboard-constants';

export class DashboardColor {

	public static red(index: number, scale = DashboardConstants.MAX_NUMBER_SKILLS_IN_DIAGRAM): string {
		const s = Math.round(40 + ((139 - 40) * index) / scale).toString(16).toUpperCase();
		return (s.length === 1) ? '0' + s : s;
	}

	public static green(index: number, scale = DashboardConstants.MAX_NUMBER_SKILLS_IN_DIAGRAM) {
		const s = Math.round((167 - (167 * index) / scale)).toString(16).toUpperCase();
		return (s.length === 1) ? '0' + s : s;
	}

	public static blue(index: number, scale = DashboardConstants.MAX_NUMBER_SKILLS_IN_DIAGRAM) {
		const s = Math.round((69 - (69 * index) / scale)).toString(16).toUpperCase();
		return (s.length === 1) ? '0' + s : s;
	}

	public static rgb (index: number, scale = DashboardConstants.MAX_NUMBER_SKILLS_IN_DIAGRAM) {
		return '#' + DashboardColor.red(index, scale) + DashboardColor.green(index, scale) + DashboardColor.blue(index, scale);
	}
}
