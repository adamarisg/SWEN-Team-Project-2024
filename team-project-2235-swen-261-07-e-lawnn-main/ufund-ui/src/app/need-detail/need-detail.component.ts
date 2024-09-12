import { Component, Input, numberAttribute } from '@angular/core';
import {Need} from '../need';
import { CommonModule, NgIf, UpperCasePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Location } from '@angular/common';
import { NeedService } from '../need.service';
import { AdminService } from '../admin.service';
import { UserService } from '../user.service';

@Component({
  selector: 'app-need-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf],
  templateUrl: './need-detail.component.html',
  styleUrl: './need-detail.component.css'
})
export class NeedDetailComponent {
  @Input() need?: Need;

  constructor(
    private adminService: AdminService,
    private route: ActivatedRoute,
    private needService: NeedService,
    private userService: UserService,
    private location: Location
  ) {}

  username: string = "";
  admin?: boolean;
  loggedIn?: boolean;
  inCart: boolean = false;
  ngOnInit() {
    this.getNeed();
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
  

  getNeed(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.needService.getNeed(id)
      .subscribe(need => {this.need = need; this.getCartStatus(need.id);});
  }

  getCartStatus(needId: number): void {
    this.userService.getUser(this.username).subscribe(user =>
      {
        let index: Number = user.cart.indexOf(needId);
        this.inCart = index != -1;
      });
  }

  goBack(): void {
    this.location.back();
  }

  save(): void {
    if (this.need) {
      //checks if the urgency num is valid 
      if (this.admin && this.checkUrgency(this.need.urgency)) {
        this.needService.updateNeed(this.need)
        .subscribe(() => this.goBack());
      }else{
        //alerts user that urgency num is out of range
        alert("Urgency must be a number from 1-10")
      }
    }
  }
  
  /**
    * Function to check if the urgency number given is within the range of 1-10.
  */
  checkUrgency(urgency: number): boolean {
    return urgency >= 1 && urgency <=10;
  }

  addToCart(): void {
    if (!this.loggedIn) {
      alert("You must be logged in to add something to your Shopping Cart!\nPlease login at the top of the screen")
      return
    }
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.userService.addNeedToCart(this.username, id).subscribe();
    this.inCart = !this.inCart
    //this.getCartStatus(this.need!.id);
  }

}

