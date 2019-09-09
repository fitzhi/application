import { Component, OnInit, AfterViewInit } from '@angular/core';

@Component({
	selector: 'app-project-audit',
	templateUrl: './project-audit.component.html',
	styleUrls: ['./project-audit.component.css']
})
export class ProjectAuditComponent implements OnInit, AfterViewInit {


	constructor() { }

	ngOnInit() {
	}

	ngAfterViewInit() {
		this.dashboardAudit();
	}

	dashboardAudit() {

		const angleInRadians = angleInDegrees => (angleInDegrees - 90) * (Math.PI / 180.0);

		const polarToCartesian = (centerX, centerY, radius, angleInDegrees) => {
				const a = angleInRadians(angleInDegrees);
				return {
						x: centerX + (radius * Math.cos(a)),
						y: centerY + (radius * Math.sin(a)),
				};
		};

		const arc = (x, y, radius, startAngle, endAngle) => {
				const start = polarToCartesian(x, y, radius, endAngle - 0.01);
				const end = polarToCartesian(x, y, radius, startAngle);
				const arcSweep = endAngle - startAngle <= 180 ? '0' : '1';

				const d = [
						'M', start.x, start.y,
						'A', radius, radius, 0, arcSweep, 0, end.x, end.y,
				].join(' ');

				return d;
		};

		document.getElementById('arc1').setAttribute('d', arc(150, 100, 50, -180, 90));
		document.getElementById('txt1').setAttribute('x', '160');
		document.getElementById('txt1').setAttribute('y', '120');

		document.getElementById('arc2').setAttribute('d', arc(150, 250, 50, -90, 90));
		document.getElementById('arc3').setAttribute('d', arc(150, 400, 50, 15, 90));

		document.getElementById('arc4').setAttribute('d', arc(780, 100, 50, -180, 90));
		document.getElementById('arc5').setAttribute('d', arc(780, 250, 50, -180, 90));
		document.getElementById('arc6').setAttribute('d', arc(780, 400, 50, -30, 90));

		document.getElementById('year').setAttribute('d', arc(530, 200, 80, -180, 180));

	}
}
