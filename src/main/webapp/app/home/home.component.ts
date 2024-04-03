import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { IAppointment } from 'app/entities/appointment/appointment.model';
import { AppointmentService, EntityArrayResponseType } from 'app/entities/appointment/service/appointment.service';
import dayjs from 'dayjs';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

import { MatDividerModule } from '@angular/material/divider';
import { HttpParams } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [SharedModule, RouterModule, MatDividerModule, HasAnyAuthorityDirective],
})
export default class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  isAdmin: boolean = this.accountService.hasAnyAuthority('ROLE_ADMIN');
  isUser: boolean = this.accountService.hasAnyAuthority('ROLE_USER');
  appointments?: IAppointment[];
  allAppointments?: IAppointment[];
  filteredAppointments: IAppointment[] = [];

  private readonly destroy$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    protected appointmentService: AppointmentService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));

    this.loadUserAppt('appt_datetime', 'ASC');
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  loadUserAppt(predicate: string, sort: string): void {
    const userId = this.account?.id;
    let params = new HttpParams();
    if (userId !== null && userId !== undefined) {
      params = new HttpParams().set('userId', userId).set('predicate', predicate).set('sort', sort);
    }
    this.appointmentService.getUserAppt(params).subscribe(res => {
      this.onResponseSuccess(res);
    });
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.appointments = dataFromBody;
    this.filterAppointments();
  }

  protected fillComponentAttributesFromResponseBody(data: IAppointment[] | null): IAppointment[] {
    return data ?? [];
  }

  filterAppointments(): void {
    const today = dayjs().startOf('day');
    if (this.appointments) {
      this.allAppointments = this.appointments.slice();

      this.allAppointments = this.allAppointments.filter(appointment => appointment.status !== 1);

      this.allAppointments = this.allAppointments.filter(appointment => {
        const appointmentDate = dayjs(appointment.apptDatetime, 'DD MMM YYYY HH:mm:ss');
        return appointmentDate.isSame(today, 'day');
      });

      this.allAppointments.sort((a, b) => {
        const dateA = dayjs(a.apptDatetime, 'DD MMM YYYY HH:mm:ss');
        const dateB = dayjs(b.apptDatetime, 'DD MMM YYYY HH:mm:ss');
        return dateA.diff(dateB);
      });
      if (!this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
        this.filteredAppointments = this.allAppointments.filter(appointment => appointment.patientId === this.account?.id);
      } else {
        this.filteredAppointments = this.allAppointments || [];
      }
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
