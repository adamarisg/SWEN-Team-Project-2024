import { Injectable } from '@angular/core';
import { User } from './user';
import { Observable, of } from 'rxjs';
import { MessageService } from './message.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { Need } from './need';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient,
    private messageService: MessageService) { }
 
  private usersUrl = 'http://localhost:8080/users';  // URL to web api

  /** Log a UserService message with the MessageService */
  private log(message: string) {
    this.messageService.add(`UserService: ${message}`);
  }

  /**
  * Handle Http operation that failed.
  * Let the app continue.
  * @param operation - name of the operation that failed
  * @param result - optional value to return as the observable result
  */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      this.log(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  /** GET users from the server */
  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.usersUrl)
      .pipe(
        tap(_ => this.log('fetched users')),
        catchError(this.handleError<User[]>('getUsers', []))
      );
  }

  /** GET user by username. Will 404 if username not found */
  getUser(username: string): Observable<User> {
    const url = `${this.usersUrl}/${username}`;
    return this.http.get<User>(url).pipe(
      tap(_ => this.log(`fetched user: ${username}`)),
      catchError(this.handleError<User>(`getUser username=${username}`))
    );
  }

  /** GET user by username. Will 404 if username not found */
  getUserCart(username: string): Observable<Need[]> {
    const url = `${this.usersUrl}/${username}/cart`;
    return this.http.get<Need[]>(url).pipe(
      tap(_ => this.log(`fetched user: ${username}`)),
      catchError(this.handleError<Need[]>(`getUser username=${username}`))
    );
  }

  /** PUT: add need to users cart */
  addNeedToCart(username: string, needId: number): Observable<any> {
    const body = `{"username":"${username}","needId":"${needId}"}`;
    return this.http.put(this.usersUrl, body, this.httpOptions).pipe(
      tap(_ => this.log(`added need:${needId} to ${username}'s cart`)),
      catchError(this.handleError<any>('addNeedToCart'))
    );
  }

  /** POST: add a new user to the server */
  addUser(username: string): Observable<User> {
    return this.http.post<User>(this.usersUrl, username, this.httpOptions).pipe(
      tap((newUser: User) => this.log(`added user: ${username}`)),
      catchError(this.handleError<User>('addUser'))
    );
  }

}
