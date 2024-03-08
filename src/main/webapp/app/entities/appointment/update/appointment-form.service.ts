import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAppointment, NewAppointment } from '../appointment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppointment for edit and NewAppointmentFormGroupInput for create.
 */
type AppointmentFormGroupInput = IAppointment | PartialWithRequiredKeyOf<NewAppointment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAppointment | NewAppointment> = Omit<T, 'apptDatetime'> & {
  apptDatetime?: string | null;
};

type AppointmentFormRawValue = FormValueOf<IAppointment>;

type NewAppointmentFormRawValue = FormValueOf<NewAppointment>;

type AppointmentFormDefaults = Pick<NewAppointment, 'id' | 'apptDatetime'>;

type AppointmentFormGroupContent = {
  id: FormControl<AppointmentFormRawValue['id'] | NewAppointment['id']>;
  apptType: FormControl<AppointmentFormRawValue['apptType']>;
  apptDatetime: FormControl<AppointmentFormRawValue['apptDatetime']>;
  remarks: FormControl<AppointmentFormRawValue['remarks']>;
  patientId: FormControl<AppointmentFormRawValue['patientId']>;
  firstName: FormControl<AppointmentFormRawValue['firstName']>;
  lastName: FormControl<AppointmentFormRawValue['lastName']>;
  email: FormControl<AppointmentFormRawValue['email']>;
  phoneNumber: FormControl<AppointmentFormRawValue['phoneNumber']>;
  // doctorId: FormControl<AppointmentFormRawValue['doctorId']>;
};

export type AppointmentFormGroup = FormGroup<AppointmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppointmentFormService {
  createAppointmentFormGroup(
    appointment: AppointmentFormGroupInput = { id: null },
    isNewPatient: boolean,
    isAdmin: boolean,
  ): AppointmentFormGroup {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({
      ...this.getFormDefaults(),
      ...appointment,
    });
    return new FormGroup<AppointmentFormGroupContent>({
      id: new FormControl(
        { value: appointmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      apptType: new FormControl(appointmentRawValue.apptType, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      apptDatetime: new FormControl(appointmentRawValue.apptDatetime, {
        validators: [Validators.required],
      }),
      remarks: new FormControl(appointmentRawValue.remarks, {
        validators: [Validators.maxLength(200)],
      }),
      patientId: new FormControl(appointmentRawValue.patientId, {
        validators: !isNewPatient && isAdmin ? [Validators.required] : [],
      }),
      firstName: new FormControl(appointmentRawValue.firstName, {
        validators: isNewPatient && isAdmin ? [Validators.required] : [],
      }),
      lastName: new FormControl(appointmentRawValue.lastName, {
        validators: isNewPatient && isAdmin ? [Validators.required] : [],
      }),
      email: new FormControl(appointmentRawValue.email, {
        validators:
          isNewPatient && isAdmin ? [Validators.required, Validators.pattern('[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}')] : [],
      }),
      phoneNumber: new FormControl(appointmentRawValue.phoneNumber, {
        validators: isNewPatient && isAdmin ? [Validators.required, Validators.pattern('^[0-9]{1,8}$')] : [],
      }),
      // doctorId: new FormControl(appointmentRawValue.doctorId, {
      //   validators: [Validators.required],
      // }),
    });
  }

  getAppointment(form: AppointmentFormGroup): IAppointment | NewAppointment {
    return this.convertAppointmentRawValueToAppointment(form.getRawValue() as AppointmentFormRawValue | NewAppointmentFormRawValue);
  }

  resetForm(form: AppointmentFormGroup, appointment: AppointmentFormGroupInput): void {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({ ...this.getFormDefaults(), ...appointment });
    form.reset(
      {
        ...appointmentRawValue,
        id: { value: appointmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AppointmentFormDefaults {
    const currentTime = dayjs();
    return {
      id: null,
      apptDatetime: currentTime,
    };
  }

  private convertAppointmentRawValueToAppointment(
    rawAppointment: AppointmentFormRawValue | NewAppointmentFormRawValue,
  ): IAppointment | NewAppointment {
    return {
      ...rawAppointment,
      apptDatetime: dayjs(rawAppointment.apptDatetime, DATE_TIME_FORMAT),
    };
  }

  private convertAppointmentToAppointmentRawValue(
    appointment: IAppointment | (Partial<NewAppointment> & AppointmentFormDefaults),
  ): AppointmentFormRawValue | PartialWithRequiredKeyOf<NewAppointmentFormRawValue> {
    return {
      ...appointment,
      apptDatetime: appointment.apptDatetime ? appointment.apptDatetime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
