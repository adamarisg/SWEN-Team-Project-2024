import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { User } from '../user';
import { Need } from '../need';
import { AdminService } from '../admin.service';
import { NeedService } from '../need.service';

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './shopping-cart.component.html',
  styleUrl: './shopping-cart.component.css'
})
export class ShoppingCartComponent implements OnInit {

  username?: string;
  cart: Need[] = [];

  constructor(
    private userService: UserService,
    private adminService: AdminService,
    private needService: NeedService
  ) { }

  ngOnInit(): void {
    this.adminService.username.subscribe(result => this.getCart(result));
    this.getCart;
  }

  getCart(username: string): void {
    if (username) {
      this.username = username;
      this.userService.getUserCart(this.username).subscribe(result => this.cart = result);
    }
  }

  removeFromCart(needId: number): void {
    this.userService.addNeedToCart(this.username!, needId).subscribe();
    this.getCart(this.username!);
  }

  checkout(deleteNeeds: boolean): void {
    this.cart.forEach(element => {
      this.userService.addNeedToCart(this.username!, element.id).subscribe();
      if (deleteNeeds) {
        this.needService.deleteNeed(element.id).subscribe();
      }
    });
    this.getCart(this.username!);
  }

}
