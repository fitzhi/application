import { AfterViewInit, Component, EventEmitter, HostBinding, Input, OnInit, Output } from '@angular/core';
import { traceOn } from 'src/app/global';

@Component({
  selector: 'app-skyline-icon',

  templateUrl: './skyline-icon.component.html',
  styleUrls: ['./skyline-icon.component.css']
})
export class SkylineIconComponent implements OnInit, AfterViewInit {

  /**
   * Width of the skyline icon
   */
  @HostBinding('style.--skyline-icon-width')
  @Input() width = '100px';

  /**
   * Height of the skyline icon
   */
  @HostBinding('style.--skyline-icon-height')
  @Input() height = '100px';

  /**
   * We'll send to the parent component (startingSetup) the new user has been created.
   */
	@Output() onClick = new EventEmitter<number>();

  constructor() { }

  ngAfterViewInit(): void {
    console.log('ngAfterViewInit.');
  }

  ngOnInit(): void {
    console.log ('Creation');
  }

  public click() {
    if (traceOn()) {
      console.log ('Clicking on the Skyline icon');
    }
    this.onClick.emit(1);
  }
}
