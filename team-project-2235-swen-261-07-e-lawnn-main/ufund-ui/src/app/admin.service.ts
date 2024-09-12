import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor() { }

  private _isAdmin: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  set isAdmin(value: boolean) {
    this._isAdmin.next(value);
  }
  get isAdmin(): Observable<boolean> {
    return this._isAdmin.asObservable();
  }

  private _isLoggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  set isLoggedIn(value: boolean) {
    this._isLoggedIn.next(value)
  }
  get isLoggedIn(): Observable<boolean> {
    return this._isLoggedIn.asObservable();
  }

  private _username: BehaviorSubject<string> = new BehaviorSubject<string>("");
  set username(value: string) {
    this._username.next(value)
  }
  get username(): Observable<string> {
    return this._username.asObservable();
  }

}
