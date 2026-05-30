package ru.seleznev.dto.users;

import org.springframework.stereotype.Component;
import ru.seleznev.domain.UserModel;

@Component
public class UserDtoMapper {

    public UserModel toModel(CreateUserRequest request) {
        return new UserModel(
                request.getLogin(),
                request.getName(),
                request.getAge(),
                request.getGender(),
                request.getHairColor()
        );
    }

    public UserResponse toResponse(UserModel user) {
        return new UserResponse(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getAge(),
                user.getGender(),
                user.getHairColor()
        );
    }
}
