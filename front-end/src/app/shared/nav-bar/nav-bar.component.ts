import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../services/auth.service";
import { LoginComponent } from 'src/app/login/login.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})

export class NavBarComponent implements OnInit {

  collapsed = true;
  role: string;
  isLoggedIn = false;
  //showAdminBoard = false;
  showModeratorBoard = false;
  email?: string;
  firstName?: string;
  lastName?: string;

  userFirstName: string = this.loginComponent.userFirstName;
  userLastName: string = this.loginComponent.userLastName;
  userSessionToken: string = this.loginComponent.userSessionToken;
  userEmail: string = this.loginComponent.userEmail;
  userIsAdmin: boolean = this.loginComponent.userIsAdmin;

  constructor( private authService: AuthService, private router: Router, public loginComponent: LoginComponent ){ }

  ngOnInit(): void {
    this.isLoggedIn = !!this.authService.getToken();

    if (this.isLoggedIn) {
      const user = this.authService.getUser();
      this.role = user.isAdmin;

      //this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
      //this.showModeratorBoard = this.roles.includes('ROLE_MODERATOR');
      this.firstName = user.firstName;
      this.lastName = user.lastName;
      this.email = user.email;

      if (this.role) {
        this.showModeratorBoard = true;
      }
    }
  }

  logout(): void {
    this.authService.signOut();
    this.isLoggedIn = false;
    this.showModeratorBoard = false;
    //window.location.reload();
    this.router.navigate(['/']);
  }

}
