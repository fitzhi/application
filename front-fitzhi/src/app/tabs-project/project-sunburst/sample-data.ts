export const data = { 
	"code": 0, 
	"message": "", 
	"projectRiskLevel": 0, 
	"idProject": 3, 
	"sunburstData": {  
		location: "root",
		color: "lightGreen",
		children: [
			{
			location: "leafA",
			importance: 3,
			color: "red"
			},
			{
			location: "nodeB",
			color: "orange",
			children: [
				{
				location: "leafBA",
				importance: 5,
				color: 'green'
				},
				{
				location: "leafBB",
				importance: 1,
				color: "blue"
				}
			]
			}
		]
	}, 
	"ghosts": []
}