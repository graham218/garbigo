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

  // Location fields
  final String? addressLine1;
  final String? addressLine2;
  final String? city;
  final String? stateOrProvince;
  final String? postalCode;
  final String? country;
  final double? latitude;
  final double? longitude;

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
    this.addressLine1,
    this.addressLine2,
    this.city,
    this.stateOrProvince,
    this.postalCode,
    this.country,
    this.latitude,
    this.longitude,
  });

  // Computed full name
  String get fullName {
    final nameParts = [firstName, middleName, lastName]
        .where((part) => part != null && part.isNotEmpty)
        .join(' ');
    return nameParts.isEmpty ? email.split('@')[0] : nameParts;
  }

  // Computed full address
  String get fullAddress {
    final parts = [
      addressLine1,
      addressLine2,
      city,
      stateOrProvince,
      postalCode,
      country,
    ].where((part) => part != null && part.isNotEmpty).join(', ');
    return parts.isEmpty ? 'No address provided' : parts;
  }

  // Check if user has location set
  bool get hasLocation => latitude != null && longitude != null;

  // From JSON (used when receiving from backend)
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
      addressLine1: json['addressLine1'],
      addressLine2: json['addressLine2'],
      city: json['city'],
      stateOrProvince: json['stateOrProvince'],
      postalCode: json['postalCode'],
      country: json['country'],
      latitude: json['latitude']?.toDouble(),
      longitude: json['longitude']?.toDouble(),
    );
  }

  // To JSON (used when sending updates to backend)
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
      'addressLine1': addressLine1,
      'addressLine2': addressLine2,
      'city': city,
      'stateOrProvince': stateOrProvince,
      'postalCode': postalCode,
      'country': country,
      'latitude': latitude,
      'longitude': longitude,
    };
  }

  // Copy with method for easy updates
  UserModel copyWith({
    String? id,
    String? email,
    String? username,
    String? phoneNumber,
    UserRole? role,
    AccountStatus? accountStatus,
    bool? isEmailVerified,
    String? firstName,
    String? middleName,
    String? lastName,
    String? profilePictureUrl,
    String? addressLine1,
    String? addressLine2,
    String? city,
    String? stateOrProvince,
    String? postalCode,
    String? country,
    double? latitude,
    double? longitude,
  }) {
    return UserModel(
      id: id ?? this.id,
      email: email ?? this.email,
      username: username ?? this.username,
      phoneNumber: phoneNumber ?? this.phoneNumber,
      role: role ?? this.role,
      accountStatus: accountStatus ?? this.accountStatus,
      isEmailVerified: isEmailVerified ?? this.isEmailVerified,
      firstName: firstName ?? this.firstName,
      middleName: middleName ?? this.middleName,
      lastName: lastName ?? this.lastName,
      profilePictureUrl: profilePictureUrl ?? this.profilePictureUrl,
      addressLine1: addressLine1 ?? this.addressLine1,
      addressLine2: addressLine2 ?? this.addressLine2,
      city: city ?? this.city,
      stateOrProvince: stateOrProvince ?? this.stateOrProvince,
      postalCode: postalCode ?? this.postalCode,
      country: country ?? this.country,
      latitude: latitude ?? this.latitude,
      longitude: longitude ?? this.longitude,
    );
  }
}