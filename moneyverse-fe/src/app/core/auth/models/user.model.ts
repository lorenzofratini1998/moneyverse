export interface UserModel {
  userId: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
}

export interface UserUpdateRequestDto {
  firstName: string;
  lastName: string;
  email: string;
}
