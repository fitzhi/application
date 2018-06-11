import { Injectable } from '@angular/core';
import { Subject } from "rxjs/Subject";

@Injectable({
  providedIn: 'root'
})
export class CinematicService {

    private emitActualComponentSource = new Subject<string>();

    newFormDisplayEmitted$ = this.emitActualComponentSource.asObservable();

  constructor() { }
  
  setForm (form: string) {
  	console.log (form);
    this.emitActualComponentSource.next(form);
  }

}
