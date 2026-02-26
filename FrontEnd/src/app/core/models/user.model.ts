export interface SecurityQuestion {
  question: string;
  answer: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone: string;
  securityQuestions?: SecurityQuestion[]; // Optional array
}