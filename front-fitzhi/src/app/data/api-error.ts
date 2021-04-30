export class ApiError {
	public constructor(
		public flagApiError: number,
		public status: string,
		public timestamp: string,
		public code: number,
		public message: string,
		public debugMessage: string) {}
}
