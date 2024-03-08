import dayjs from 'dayjs/esm';

export interface IAppointment {
  id: number;
  appt_type?: string | null;
  appt_datetime?: dayjs.Dayjs | null;
  remarks?: string | null;
  patient_id?: number | null;
  doctor_id?: number | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
}

export type NewAppointment = Omit<IAppointment, 'id'> & { id: null };
