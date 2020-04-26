import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, Subscription, throwError, of, timer } from 'rxjs';
import { catchError, retry, map } from 'rxjs/operators';
import { RequestAccountResponse } from './request-account-response';
import { RequestAccountStatusResponse } from './request-account-status-response';
import { FormGroup } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class AccountRequestService {

  requestAccountUrl = "http://localhost:9001/api/v1/account/savings/request";
  messages: string[] = [];
  accountRequestId: number;
  
  requestCheckingTimer: Observable<number> = timer(0, 1000);//millis
  timeSubscription: Subscription;
  
  constructor(private http: HttpClient) { 
  }


  requestAccount(accountRequestForm: FormGroup): void {
    this.messages = [];

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
    
    this.http
    .post<RequestAccountResponse>(this.requestAccountUrl, accountRequestForm.value, httpOptions)
    .pipe(
        catchError(this.handleError)
     )
    .toPromise()
    .then(
      (data: RequestAccountResponse) => {
        //console.warn("RECEIVED: status=" + data.status+", message="+data.message+", accountRequestId="+data.accountRequestId);
        this.messages.push(data.message);
        this.accountRequestId = data.accountRequestId;
        
        if (this.accountRequestId > 0) {
          this.subscribeToTimer();
        }
      }
     );

  }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code. // The response body may contain clues as to what went wrong,
        //console.info("status "+error.status+", statusText="+error.statusText+", error message: "+error.error.message);

        if (error.status == 400 || error.status == 409) {
          let responseObject: RequestAccountResponse  = {
            "status": "FAILED", 
            "message": error.error.message,
            "accountRequestId" : -1
          };
          return of(responseObject);
        }
    }
    // return an observable with a user-facing error message
    return throwError('Something bad happened; please try again later.');
  };

  subscribeToTimer(){
    if (this.timeSubscription) {
      this.timeSubscription.unsubscribe();
    }
    
    this.timeSubscription = this.requestCheckingTimer.subscribe( counter => {
      let statusUrl = this.requestAccountUrl +"/"+this.accountRequestId+"/status";
      
      this.http.get<RequestAccountStatusResponse>(statusUrl).toPromise().then( (response: RequestAccountStatusResponse) => {
        console.info("checked request "+this.accountRequestId+" status: "+response.status);
        
        if (response.status == "PROCESSED") {
          this.messages.push("Account was successfully created !");
          this.unsubscribeToTimer();
        } else {
          let lastMessage = this.messages[this.messages.length - 1];
          lastMessage = lastMessage + " .";
          this.messages[this.messages.length - 1] = lastMessage;
        }
      });
      
    });
  }

  unsubscribeToTimer(){
    this.timeSubscription.unsubscribe();
  }

}