import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { CustomError, Error } from '../interface/error.interface';

@Injectable({ providedIn: 'root' })
export class ErrorService {
  constructor(private toastr: ToastrService) {}

  public showError(error: any): void {
    if (error.error.status == 406) {
      this.notAcceptableError(error.error);
      return;
    }

    if (error.error.status == 500) {
      let err: Error = { error: "Erro de servidor, contate o administrador!", path: "", status: 500, timestamp: "" };
      this.commumError(err);
      return;
    }

    this.commumError(error.error);
  }

  private notAcceptableError(error: CustomError) {
    if (error.errors.length == 1) {
      this.toastr.error(error.errors[0].message);
      return;
    }

    let message = "<ul>";

    for (let i = 0; i < error.errors.length; i++) {
      message += "<li>";
      message += error.errors[i].message;
      message += "</li>";
    }

    message += "</ul>";

    this.toastr.error(message, '', { enableHtml: true });
  }

  private commumError(error: Error): void {
    this.toastr.error(error.error);
  }
}
