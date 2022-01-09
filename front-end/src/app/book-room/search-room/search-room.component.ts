import { Time } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { PickerInteractionMode } from 'igniteui-angular';
import { AuthService } from 'src/app/services/auth.service';
import { Equipment } from './equipment-room.module';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-search-room',
  templateUrl: './search-room.component.html',
  styleUrls: ['./search-room.component.css'],
})
export class SearchRoomComponent implements OnInit {
  minDate = new Date(2021, 0, 1);
  maxDate = new Date(2025, 0, 1);

  public mode: PickerInteractionMode = PickerInteractionMode.DropDown;
  public format = 'hh:mm tt';
  public date: Date = new Date();

  minCapacity = 0;
  maxCapacity = 100;

  public capacity: number = 0;

  constructor(private http: HttpClient, authService: AuthService) {}

  ngOnInit() {
    let token = window.sessionStorage.getItem('auth-user');
    let sessionToken;
    if (token != null) {
      let arr: Array<string> = JSON.parse(token);
      for (var index in arr) {
        if (index == 'sessionToken') {
          sessionToken = arr[index];
        }
      }
    }
    const httpOptions = {
      headers: new HttpHeaders({ 'session-token': `${sessionToken}` }),
      responseType: 'text' as 'json',
    };

    this.http
      .get(`${environment.apiUrl}/user/get/organizations`, httpOptions)
      .subscribe((data) => {
        this.http
          .get(
            `${environment.apiUrl}/room/maxCapacity/${
              JSON.parse(data.toString())[0].id
            }`,
            httpOptions
          )
          .subscribe((data) => (this.maxCapacity = Number(data.toString())));
      });
  }

  equipments: Equipment[] = [
    new Equipment(1, 'Computers', ''),
    new Equipment(2, 'Board', ''),
    new Equipment(3, 'Projector screen', ''),
    new Equipment(4, 'Projector', ''),
    new Equipment(5, 'HDMI', ''),
    new Equipment(6, 'TV', ''),
  ];

  selectedEquipments = [];

  formatLabel(capacity: number) {
    this.capacity = capacity;
    if (this.capacity >= 1000) {
      return Math.round(this.capacity / 1000);
    }

    return this.capacity;
  }
}
