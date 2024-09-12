import { Component, OnInit, input } from '@angular/core';
import { Need } from '../need';
import { FormsModule } from '@angular/forms';
import { CommonModule, KeyValue, NgIf, UpperCasePipe } from '@angular/common';
import { NeedService } from '../need.service';
import { MessageService} from '../message.service';

import {
  /* . . . */
  NgFor,
  /* . . . */
} from '@angular/common';
import { NeedDetailComponent } from '../need-detail/need-detail.component';
import { MessagesComponent } from '../messages/messages.component';
import { RouterModule } from '@angular/router';
import { NeedSearchComponent } from '../need-search/need-search.component';
import { AdminService } from '../admin.service';

@Component({
  selector: 'app-needs',
  standalone: true,
  imports: [CommonModule, FormsModule, NgFor, NgIf, UpperCasePipe, NeedDetailComponent, MessagesComponent, RouterModule, NeedSearchComponent],
  templateUrl: './needs.component.html',
  styleUrls: ['./needs.component.css']
})
export class NeedsComponent implements OnInit {

  needs: Need[] = [];
  admin?: boolean;
  sorting: KeyValue<number, string>[] = [
    {key: 0, value: "No sorting"},
    {key: 1, value: "Sort by ID"},
    {key: 2, value: "Sort by Name"},
    {key: 3, value: "Sort by Cost Low-High"},
    {key: 4, value: "Sort by Cost High-Low"},
    {key: 5, value: "Sort by Most Urgent"}
  ]
  sortBy: number = 0;

  constructor(
    private needService: NeedService,
    private messageService: MessageService,
    private adminService: AdminService
  ) { }

  ngOnInit(): void {
    this.getNeeds();
    this.adminService.isAdmin.subscribe(admin => this.admin = admin);
  }

  ngSortingChange(): void {
    this.getNeeds();
    this.adminService.isAdmin.subscribe(admin => this.admin = admin);
  }

  getNeeds(): void {
    this.needService.getNeeds(this.sortBy)
        .subscribe(needs => this.needs = needs);
  }

  add(title: string): void {
    title = title.trim();
    if (!title) { return; }
    this.needService.addNeed({ title } as Need)
      .subscribe(need => {
        this.needs.push(need);
      });
  }

  delete(need: Need): void {
    this.needs = this.needs.filter(h => h !== need);
    this.needService.deleteNeed(need.id).subscribe();
  }
}