import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { AccountRequestService } from '../account-request.service';
import { RequestAccountResponse } from '../request-account-response';

@Component({
  selector: 'account-request',
  templateUrl: './account-request.component.html',
  styleUrls: ['./account-request.component.css']
})
export class AccountRequestComponent implements OnInit {

  accountRequestForm = new FormGroup({
    clientCnp: new FormControl('', [Validators.required]),
    initialDeposit: new FormControl('', [Validators.required, Validators.pattern(/^(\d*)(\.(\d{0,2}))?$/)]),
  });

  
  constructor(public accountService: AccountRequestService) { }

  ngOnInit(): void {
  }

  submitAccountRequest(){
    //console.warn(this.accountRequestForm.value);
    this.accountService.requestAccount(this.accountRequestForm);
  }

}
