package user.management.utils;

import user.management.dto.UserDto;
import user.management.dto.http.UserRequest;
import user.management.model.User;

public final class UserMapper {
    public static UserDto map(User user){
        return UserDto.builder()
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .username(user.getUsername())
                .created(user.getCreated())
                .build();
    }

    public static User map(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(hashPassword(request.getPassword()));
        user.setEmail(request.getEmail());
        return user;
    }

    public static User map(UserRequest request, User current) {
        current.setUsername(request.getUsername());
        current.setPassword(hashPassword(request.getPassword()));
        current.setEmail(request.getEmail());
        return current;
    }

    private static String hashPassword(String psw){
        return psw;//TODO: hash
    }
}
