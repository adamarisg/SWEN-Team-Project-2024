import { Component, OnInit } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { LoginComponent } from '../login/login.component';
import { MessagesComponent } from '../messages/messages.component';
import { AdminService } from '../admin.service';
import { CommonModule, NgIf } from '@angular/common';

@Component({
  selector: 'app-wrapper',
  standalone: true,
  imports: [LoginComponent, MessagesComponent, RouterOutlet, NgIf, CommonModule, RouterModule],
  templateUrl: './wrapper.component.html',
  styleUrl: './wrapper.component.css'
})
export class WrapperComponent implements OnInit {

  constructor(
    private adminService: AdminService
  ) {}

  admin?: boolean;
  loggedIn?: boolean;
  username?: string;
  ngOnInit() {
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

}
