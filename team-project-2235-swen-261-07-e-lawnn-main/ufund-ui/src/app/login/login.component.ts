import { Component, Input } from '@angular/core';
import { AdminService } from '../admin.service';
import { CommonModule, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../user.service';
import { User } from '../user';
import { Location } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [NgIf, CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  users: User[] = [];

  @Input() username?: string

  constructor(
    private adminService: AdminService,
    private userService: UserService,
    private location: Location
  ) { }

  isNewUser(uname: string): boolean {
    for (let i = 0; i<this.users.length; i++) {
      if (uname == this.users[i].username) {
        return false;
      }
    }
    return true;
  }

  login() {
    if (this.username) {
      if (this.isNewUser(this.username)) {
        this.userService.addUser(this.username).subscribe()
      }
      this.adminService.isLoggedIn = true
      this.adminService.isAdmin = this.username == "admin"
      this.adminService.username = this.username
    }
  }

  logout() {
    this.adminService.isLoggedIn = false
    this.adminService.isAdmin = false
    this.adminService.username = ""
  }

  admin: boolean = false;
  loggedIn: boolean = false;
  ngOnInit() {
    this.getUsers();
    this.adminService.isAdmin.subscribe(value => {
      this.admin = value;
    })
    this.adminService.isLoggedIn.subscribe(value => {
      this.loggedIn = value;
    })
    this.adminService.username.subscribe(value => {
      this.username = value;
    })
  }

  getUsers(): void {
    this.userService.getUsers().subscribe(users => this.users = users);
  }

}
