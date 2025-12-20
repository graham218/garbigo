enum UserRole {
  CLIENT,
  COLLECTOR,
  ADMIN,
  OPERATIONS,
  FINANCE,
  SUPPORT
}

enum AccountStatus {
  ACTIVE,
  PENDING,
  SUSPENDED,
  BLOCKED,
  ARCHIVED
}

class UserModel {
  final String id;
  final String email;
  final String? username;
  final String? phoneNumber;
  final UserRole role;
  final AccountStatus accountStatus;
  final bool isEmailVerified;
  final String? firstName;
  final String? middleName;
  final String? lastName;
  final String? profilePictureUrl;

  UserModel({
    required this.id,
    required this.email,
    this.username,
    this.phoneNumber,
    required this.role,
    required this.accountStatus,
    required this.isEmailVerified,
    this.firstName,
    this.middleName,
    this.lastName,
    this.profilePictureUrl,
  });

  String get fullName {
    if ((firstName ?? '').isEmpty && (lastName ?? '').isEmpty) {
      return email.split('@')[0];
    }
    return '${firstName ?? ''} ${lastName ?? ''}'.trim();
  }

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] ?? '',
      email: json['email'] ?? '',
      username: json['username'],
      phoneNumber: json['phoneNumber'],
      role: UserRole.values.firstWhere(
            (e) => e.name == json['role'],
        orElse: () => UserRole.CLIENT,
      ),
      accountStatus: AccountStatus.values.firstWhere(
            (e) => e.name == json['accountStatus'],
        orElse: () => AccountStatus.PENDING,
      ),
      isEmailVerified: json['isEmailVerified'] ?? false,
      firstName: json['firstName'],
      middleName: json['middleName'],
      lastName: json['lastName'],
      profilePictureUrl: json['profilePictureUrl'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'username': username,
      'phoneNumber': phoneNumber,
      'role': role.name,
      'accountStatus': accountStatus.name,
      'isEmailVerified': isEmailVerified,
      'firstName': firstName,
      'middleName': middleName,
      'lastName': lastName,
      'profilePictureUrl': profilePictureUrl,
    };
  }
}