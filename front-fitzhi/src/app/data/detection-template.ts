/**
 * The class represents a skill detection template within the repository.
 *
 * The **Filename filter pattern** filtering on pattern `.java$` is the first exemple of detection templates.
 * In this scenario,
 * * `typeDetecton` will represents the `filename filter`.
 * * `pattern` will contain `.java$`.
 */
export class DetectionTemplate {

	constructor(
		public detectionType: number,
		public pattern: string) {}

}
