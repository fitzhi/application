import { MessageService } from './message/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Constants } from './constants';
import { Router } from '@angular/router';


@Injectable()
export class CustomErrorHandler implements ErrorHandler {

