package org.mikhailov.dm.eventmanager.users;

import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    public User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getPasswordHash(),
                UserRole.valueOf(userEntity.getRole())
        );
    }
}
