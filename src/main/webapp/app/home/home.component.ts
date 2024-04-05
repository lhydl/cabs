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
  today: string = dayjs().format('YYYY-MM-DD');
  appointments?: IAppointment[] = [];
  userTodaysAppointments?: IAppointment[] = [];
  currentAppointment?: IAppointment;
  nextAppointment?: IAppointment;

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

    this.getTodaysAppointments();

    if (!this.isAdmin) {
      // TODO -> poll backend every 5 sec for real time q status
      /* if is user, poll backend to get real time q updates for users */
    }
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  getTodaysAppointments(userId?: number): void {
    const params = new HttpParams().set('today', this.today);
    this.appointmentService.getTodaysAppointments(params).subscribe((res: any) => {
      /* can use appt id as q number, then disclaimer q number may not be called in sequence */
      this.appointments = res;
      if (this.appointments && !this.isAdmin) {
        this.userTodaysAppointments = this.appointments.filter(appointment => appointment.patientId === this.account?.id);
      }
    });
  }

  getCurrentQueue(): void {
    // TODO -> get current appt in the queue, and check how many ppl in front of q
    /* to implement appt status: 0 = new, 1 = completed, 2 = missed*/
  }

  onClickNext(isMissed?: boolean): void {
    // TODO -> write api for admin to next (user completed or missed appt)
    /* make two buttons, "skip" and "next" that calls this with diff params */
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
