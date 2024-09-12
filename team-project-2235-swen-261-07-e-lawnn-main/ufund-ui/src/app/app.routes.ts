import { Routes } from '@angular/router';
import { NeedsComponent } from './needs/needs.component';
import { RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { NeedDetailComponent } from './need-detail/need-detail.component';
import { ShoppingCartComponent } from './shopping-cart/shopping-cart.component';

export const routes: Routes = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'detail/:id', component: NeedDetailComponent },
    { path: 'inventory', component: NeedsComponent },
    { path: 'cart', component: ShoppingCartComponent }
  ];