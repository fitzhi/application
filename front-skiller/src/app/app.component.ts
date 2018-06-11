import { Component } from '@angular/core';
import { CinematicService } from './cinematic.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
   
   public form: String;
 
   constructor(private cinematicService:CinematicService) { 
 
      this.cinematicService.newFormDisplayEmitted$.subscribe(data => {
   	     console.log (data);
         this.form = data;
      })
   }
    
   ngOnInit() {
    this.form = "welcome";
  }
}
