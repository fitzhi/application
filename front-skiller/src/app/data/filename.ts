export class Filename {
	filename: string;
	lastCommit: Date;

	public constructor(filename: string, lastCommit: Date) {
		this.filename = filename;
		this.lastCommit = lastCommit;
	}
}
